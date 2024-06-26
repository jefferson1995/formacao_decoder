package com.ead.payment.consumers;

import com.ead.payment.dtos.PaymentCommandDTO;
import com.ead.payment.services.PaymentService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class PaymenntConsummer {

    @Autowired
    PaymentService paymentService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${ead.broker.queue.paymentCommandQueue.name}", durable = "true"),
            exchange = @Exchange(value = "${ead.broker.exchange.paymentCommandExchange}", type = ExchangeTypes.TOPIC, ignoreDeclarationExceptions = "true"),
            key = "${ead.broker.key.paymentCommandKey}")
    )
    public void listenPaymentCommandDTO(@Payload PaymentCommandDTO paymentCommandDTO) {
        //processo de pagamento
        paymentService.makePayment(paymentCommandDTO);
        System.out.println("userID: " + paymentCommandDTO.getUserId());
        System.out.println("paymentID: " + paymentCommandDTO.getPaymentId());
    }
}
