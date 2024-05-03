package com.ead.payment.models;

import com.ead.payment.enums.PaymentControl;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL) //Para excluir os campos nulos
@Entity
@Table(name = "TB_PAYMENTS")
public class PaymentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID paymentId;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentControl paymentControl;
    @Column(nullable = false)
    private LocalDateTime paymentRequestDate;
    @Column
    private LocalDateTime paymentCompletionDate;
    @Column(nullable = false)
    private LocalDateTime paymentExpirationDate;
    @Column(nullable = false, length = 4)
    private String lastDigitsCreditCard;
    @Column(nullable = false)
    private BigDecimal valuePaid;
    @Column(length = 150)
    private String paymentMessage;
    @Column
    private boolean recurrence;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private UserModel user;


    public UUID getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(UUID paymentId) {
        this.paymentId = paymentId;
    }

    public PaymentControl getPaymentControl() {
        return paymentControl;
    }

    public void setPaymentControl(PaymentControl paymentControl) {
        this.paymentControl = paymentControl;
    }

    public LocalDateTime getPaymentRequestDate() {
        return paymentRequestDate;
    }

    public void setPaymentRequestDate(LocalDateTime paymentRequestDate) {
        this.paymentRequestDate = paymentRequestDate;
    }

    public LocalDateTime getPaymentCompletionDate() {
        return paymentCompletionDate;
    }

    public void setPaymentCompletionDate(LocalDateTime paymentCompletionDate) {
        this.paymentCompletionDate = paymentCompletionDate;
    }

    public LocalDateTime getPaymentExpirationDate() {
        return paymentExpirationDate;
    }

    public void setPaymentExpirationDate(LocalDateTime paymentExpirationDate) {
        this.paymentExpirationDate = paymentExpirationDate;
    }

    public String getLastDigitsCreditCard() {
        return lastDigitsCreditCard;
    }

    public void setLastDigitsCreditCard(String lastDigitsCreditCard) {
        this.lastDigitsCreditCard = lastDigitsCreditCard;
    }

    public BigDecimal getValuePaid() {
        return valuePaid;
    }

    public void setValuePaid(BigDecimal valuePaid) {
        this.valuePaid = valuePaid;
    }

    public String getPaymentMessage() {
        return paymentMessage;
    }

    public void setPaymentMessage(String paymentMessage) {
        this.paymentMessage = paymentMessage;
    }

    public boolean isRecurrence() {
        return recurrence;
    }

    public void setRecurrence(boolean recurrence) {
        this.recurrence = recurrence;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}
