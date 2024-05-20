package com.ead.authuser.consumers;

import com.ead.authuser.dtos.PaymentEventDTO;
import com.ead.authuser.enums.PaymentControl;
import com.ead.authuser.enums.RoleType;
import com.ead.authuser.enums.UserType;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.RoleService;
import com.ead.authuser.services.UserService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class PaymentConsummer {

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${ead.broker.queue.paymentEventQueue.name}", durable = "true"),
            exchange = @Exchange(value = "${ead.broker.exchange.paymentEvent}", type = ExchangeTypes.FANOUT, ignoreDeclarationExceptions = "true")
    ))
    public void listenPaymentEvent(@Payload PaymentEventDTO paymentEventDTO) {
        UserModel userModel = userService.findById(paymentEventDTO.getUserId()).get();
        var roleModel = roleService.findByRoleName(RoleType.ROLE_STUDENT)
                .orElseThrow(() -> new RuntimeException("ERROR: role is not found."));
        switch (PaymentControl.valueOf(paymentEventDTO.getPaymentControl())) {
            case EFFECTED:
                if (userModel.getUserType().equals(UserType.USER)) {
                    userModel.setUserType(UserType.STUDENT);
                    userModel.getRoles().add(roleModel);
                    userService.updateUser(userModel);
                }
                break;
            case REFUSED:
                if (userModel.getUserType().equals(UserType.STUDENT)) {
                    userModel.setUserType(UserType.USER);
                    userModel.getRoles().remove(roleModel);
                    userService.updateUser(userModel);
                }
                break;
            case ERROR:
                System.out.println("Payment error.");
        }
    }

}
