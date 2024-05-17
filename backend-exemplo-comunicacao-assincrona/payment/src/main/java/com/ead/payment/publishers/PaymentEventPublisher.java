package com.ead.payment.publishers;

import com.ead.payment.dtos.PaymentEventDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventPublisher {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value(value = "${ead.broker.exchange.paymentEventExchange}")
    private String exchangePaymentEvent;

    public void publishPaymentEvent(PaymentEventDTO paymentEventDTO){
        rabbitTemplate.convertAndSend(exchangePaymentEvent, "",paymentEventDTO);
    }
}
