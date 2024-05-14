package com.ead.payment.services.impl;

import com.ead.payment.models.CreditCardModel;
import com.ead.payment.models.PaymentModel;
import com.ead.payment.services.PaymentStripeService;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodCreateParams;
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
                            .setPaymentMethod("pm_card_visa")
                            .setReturnUrl("https://www.example.com")
                            .build();
            PaymentIntent paymentIntent = resource.confirm(params);

        }catch (Exception e){

        }
        return null;
    }

    public long formataNumero(String numero){
        return (Long.parseLong(numero));
    }
}
