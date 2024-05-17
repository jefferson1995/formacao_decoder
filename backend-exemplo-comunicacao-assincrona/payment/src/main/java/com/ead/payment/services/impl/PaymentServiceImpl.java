package com.ead.payment.services.impl;

import com.ead.payment.dtos.PaymentCommandDTO;
import com.ead.payment.dtos.PaymentRequestDTO;
import com.ead.payment.enums.PaymentControl;
import com.ead.payment.enums.PaymentStatus;
import com.ead.payment.models.CreditCardModel;
import com.ead.payment.models.PaymentModel;
import com.ead.payment.models.UserModel;
import com.ead.payment.publishers.PaymentCommandPublisher;
import com.ead.payment.publishers.PaymentEventPublisher;
import com.ead.payment.repositories.CreditCardRepository;
import com.ead.payment.repositories.PaymentRepository;
import com.ead.payment.repositories.UserRepository;
import com.ead.payment.services.PaymentService;
import com.ead.payment.services.PaymentStripeService;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LogManager.getLogger(PaymentServiceImpl.class);

    @Autowired
    CreditCardRepository creditCardRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    PaymentCommandPublisher paymentCommandPublisher;

    @Autowired
    PaymentStripeService paymentStripeService;

    @Autowired
    PaymentEventPublisher paymentEventPublisher;

    @Autowired
    private UserRepository userRepository;


    @Transactional
    @Override
    public PaymentModel requestPayment(PaymentRequestDTO paymentRequestDTO, UserModel userModel) {

        //Save CreditCardModel -> salva o cartão
        var creditCardModel = new CreditCardModel();
        var creditCardModelOptional = creditCardRepository.findByUser(userModel);

        if (creditCardModelOptional.isPresent()) { //Substitui os dados do DTO
            creditCardModel = creditCardModelOptional.get();
        }

        BeanUtils.copyProperties(paymentRequestDTO, creditCardModel);
        creditCardModel.setUser(userModel);
        creditCardRepository.save(creditCardModel);

        //Save PaymentModel -> Salva o pagamento com o estado de solicitado
        var paymentModel = new PaymentModel();
        paymentModel.setPaymentControl(PaymentControl.REQUESTED);
        paymentModel.setPaymentRequestDate(LocalDateTime.now(ZoneId.of("UTC")));
        paymentModel.setPaymentExpirationDate(LocalDateTime.now(ZoneId.of("UTC")).plusDays(30));
        paymentModel.setLastDigitsCreditCard(paymentRequestDTO.getCreditCardNumber().substring(
                paymentRequestDTO.getCreditCardNumber().length() - 4));
        paymentModel.setValuePaid(paymentRequestDTO.getValuePaid());
        paymentModel.setUser(userModel);
        paymentRepository.save(paymentModel);

        //Send Request to queue -> Envia para fila e depois o consumer vai receber a msg e iniciar o processo de pagamento
        try {
            var paymmentCommandDTO = new PaymentCommandDTO();
            paymmentCommandDTO.setUserId(userModel.getUserId());
            paymmentCommandDTO.setPaymentId(paymentModel.getPaymentId());
            paymmentCommandDTO.setCardId(creditCardModel.getCardId());
            paymentCommandPublisher.publishPaymentCommand(paymmentCommandDTO);
        } catch (Exception e) {
            logger.warn("erro ao enviar mensagem payment command!");
        }
        return paymentModel;
    }

    @Override
    public Optional<PaymentModel> findLastPaymentByUser(UserModel userModel) {
        return paymentRepository.findTopByUserOrderByPaymentRequestDateDesc(userModel);
    }

    @Override
    public Page<PaymentModel> findAllByUser(Specification<PaymentModel> spec, Pageable pageable) {
        return paymentRepository.findAll(spec, pageable);
    }

    @Override
    public Optional<PaymentModel> findPaymentByUser(UUID userId, UUID paymentId) {
        return paymentRepository.findPaymentByUser(userId, paymentId);
    }

    @Transactional
    @Override
    public void makePayment(PaymentCommandDTO paymentCommandDTO) {
        var paymentModel = paymentRepository.findById(paymentCommandDTO.getPaymentId()).get();
        var userModel = userRepository.findById(paymentCommandDTO.getUserId()).get();
        var creditCardModel = creditCardRepository.findById(paymentCommandDTO.getCardId()).get();

        paymentModel = paymentStripeService.processStripePayment(paymentModel, creditCardModel);
        paymentRepository.save(paymentModel);

        if (paymentModel.getPaymentControl().equals(PaymentControl.EFFECTED)) {
            userModel.setPaymentStatus(PaymentStatus.PAYNG);
            userModel.setLastPaymentDate(LocalDateTime.now(ZoneId.of("UTC")));
            userModel.setPaymentExpirationDate(LocalDateTime.now(ZoneId.of("UTC")).plusDays(30));
            if (userModel.getFirstPaymentDate() == null) {
                userModel.setFirstPaymentDate(LocalDateTime.now(ZoneId.of("UTC")));
            }
        } else {
            userModel.setPaymentStatus(PaymentStatus.DEBITOR);
        }
        userRepository.save(userModel);

        //enviar evento

        if (paymentModel.getPaymentControl().equals(PaymentControl.EFFECTED) || paymentModel.getPaymentControl().equals(PaymentControl.REFUSED)) {
            paymentEventPublisher.publishPaymentEvent(paymentModel.convertToPaymentEventDTO());
        } else if (paymentModel.getPaymentControl().equals(PaymentControl.ERROR)) {
            //politica de retentativa
        }

    }
}
