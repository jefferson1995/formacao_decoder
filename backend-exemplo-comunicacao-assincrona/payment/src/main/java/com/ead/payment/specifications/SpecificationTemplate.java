package com.ead.payment.specifications;

import com.ead.payment.models.PaymentModel;
import com.ead.payment.models.UserModel;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.UUID;

public class SpecificationTemplate {

    @And({
            @Spec(path = "paymentControl", spec = Equal.class),
            @Spec(path = "valuePaid", spec = Equal.class),
            @Spec(path = "lestDigitsCreditCard", spec = Like.class),
            @Spec(path = "paymentMessage", spec = Like.class)
    })
    public interface PaymentSpec extends Specification<PaymentModel> {
    }

    public static Specification<PaymentModel> paymentUserId(final UUID userId){
        return ((root, query, criteriaBuilder) -> {
            query.distinct(true);
            Root<PaymentModel> payment = root;
            Root<UserModel> user = query.from(UserModel.class);
            Expression<Collection<PaymentModel>> usersPayments = user.get("payments");
            return criteriaBuilder.and(criteriaBuilder.equal(user.get("userId"), userId), criteriaBuilder.isMember(payment, usersPayments));
        } );
    }


}
