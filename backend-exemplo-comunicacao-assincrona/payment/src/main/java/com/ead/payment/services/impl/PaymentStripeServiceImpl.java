package com.ead.payment.services.impl;

import com.ead.payment.models.CreditCardModel;
import com.ead.payment.models.PaymentModel;
import com.ead.payment.services.PaymentStripeService;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            Map<String, String> cardOptions = new HashMap<>();
            List<String> paymentMethodTypes = new ArrayList<>();
            paymentMethodTypes.add("card");
            Map<String, Object> paramsPaymentIntent = new HashMap<>();
            paramsPaymentIntent.put("amount", paymentModel.getValuePaid().multiply(new BigDecimal("100")).longValue());
            paramsPaymentIntent.put("capture_method", "automatic");
            paramsPaymentIntent.put("description", "valor de pagamento teste");
            paramsPaymentIntent.put("currency", "brl");
            paramsPaymentIntent.put("payment_method", "pm_1PGIq4EyE3SweTIaMbCqXhZ8");
            paramsPaymentIntent.put("payment_method_types", paymentMethodTypes);
            paramsPaymentIntent.put("moto", "true");

            PaymentIntent paymentIntent = PaymentIntent.create(paramsPaymentIntent);
            paymentIntentId = paymentIntent.getId();

            System.out.println(paymentIntentId);

            //passo 2 -> Método de pagamento
            Map<String, Object> card = new HashMap<>();
            card.put("number", creditCardModel.getCreditCardNumber().replaceAll(" ", ""));
            card.put("exp_month", creditCardModel.getExpirationDate().split("/")[0]);
            card.put("exp_year", creditCardModel.getExpirationDate().split("/")[1]);
            card.put("cvc", creditCardModel.getCvvCode());
            Map<String, Object> paramsPaymentMethod = new HashMap<>();
            paramsPaymentMethod.put("type", "card");
            paramsPaymentMethod.put("card", card);
            PaymentMethod paymentMethod = PaymentMethod.create(paramsPaymentMethod);

            //Passo 3 -> criar a confirmação de criação de pagamento
            Map<String, Object> paramsPaymentConfirm = new HashMap<>();
            paramsPaymentConfirm.put("payment_method", paymentMethod.getId());
            PaymentIntent confirmPaymentIntent = paymentIntent.confirm(paramsPaymentConfirm);


        }catch (Exception e){

        }
        return null;
    }
}
