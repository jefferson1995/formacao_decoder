package com.ead.payment.services.impl;

import com.ead.payment.enums.PaymentControl;
import com.ead.payment.models.CreditCardModel;
import com.ead.payment.models.PaymentModel;
import com.ead.payment.services.PaymentStripeService;
import com.stripe.Stripe;
import com.stripe.exception.CardException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class PaymentStripeServiceImpl implements PaymentStripeService {

    @Value(value = "${ead.stripe.secretKey}")
    private String secretKeyStripe;

    @Override
    public PaymentModel processStripePayment(PaymentModel paymentModel, CreditCardModel creditCardModel) {

        Stripe.apiKey = secretKeyStripe;
        String paymentIntentId = null;

        try {
            //passo 1 - payment intenção de pagamento.
            PaymentIntentCreateParams paramsPaymentIntent =
                    PaymentIntentCreateParams.builder()
                            .setAmount(paymentModel.getValuePaid().multiply(new BigDecimal("100")).longValue())
                            .setCurrency("brl")
                            .setAutomaticPaymentMethods(
                                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                            .setEnabled(true)
                                            .build()
                            )
                            .build();
            PaymentIntent paymentIntentResource = PaymentIntent.create(paramsPaymentIntent);
            paymentIntentId = paymentIntentResource.getId();


            //Passo 2 -> criar a confirmação de criação de pagamento
            PaymentIntent resource = PaymentIntent.retrieve(paymentIntentId);
            PaymentIntentConfirmParams params =
                    PaymentIntentConfirmParams.builder()
                            .setPaymentMethod("pm_card_visa_debit")
                            .setReturnUrl("https://www.example.com")
                            .build();
            PaymentIntent confirmPaymentIntent = resource.confirm(params);

            if (confirmPaymentIntent.getStatus().equals("succeeded")) {
                paymentModel.setPaymentControl(PaymentControl.EFFECTED);
                paymentModel.setPaymentMessage("payment effected - paymentIntent" + paymentIntentId);
                paymentModel.setPaymentCompletionDate(LocalDateTime.now(ZoneId.of("UTC")));
            } else {
                paymentModel.setPaymentControl(PaymentControl.ERROR);
                paymentModel.setPaymentMessage("payment error v1 - paymentIntent" + paymentIntentId);
            }
        } catch (CardException cardException) {
            System.out.println("A payment error occurred: {}");
            try {
                paymentModel.setPaymentControl(PaymentControl.REFUSED);
                PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
                paymentModel.setPaymentMessage("payment refused v1 - paymentIntent: " + paymentIntentId +
                        ", cause: " + paymentIntent.getLastPaymentError().getCode() +
                        ", message: " + paymentIntent.getLastPaymentError().getMessage());
            } catch (Exception exception) {
                paymentModel.setPaymentMessage("payment refused v2 - paymentIntent: " + paymentIntentId);
                System.out.println("Another problem occurred, maybe unrelated to Stripe.");
            }
        } catch (Exception exception) {
            paymentModel.setPaymentControl(PaymentControl.ERROR);
            paymentModel.setPaymentMessage("payment error v2 - paymentIntent: " + paymentIntentId);
            System.out.println("Another problem occurred, maybe unrelated to Stripe.");
        }
        return paymentModel;
    }

}
