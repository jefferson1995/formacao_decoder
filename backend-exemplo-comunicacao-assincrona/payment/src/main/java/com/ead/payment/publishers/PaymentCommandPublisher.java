package com.ead.payment.publishers;

import com.ead.payment.dtos.PaymentCommandDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaymentCommandPublisher {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value(value = "${ead.broker.exchange.paymentCommandExchange}")
    private String paymentCommandExchange;

    @Value(value = "${ead.broker.key.paymentCommandKey}")
    private String paymentCommandKey;

    public void publishPaymentCommand(PaymentCommandDTO paymentCommandDTO){
        rabbitTemplate.convertAndSend(paymentCommandExchange, paymentCommandKey, paymentCommandDTO);
    }

}
