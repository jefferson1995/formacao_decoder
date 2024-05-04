package com.ead.payment.dtos;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;

public class PaymentRequestDTO {

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 5, fraction = 2)
    private BigDecimal valuePaid;
    @NotBlank
    private String cardHolderFullName;
    @NotBlank
    @CPF
    private String cardHolderCpf;
    @NotBlank
    @Size(min = 16, max = 20)
    private String creditCardNumber;
    @NotBlank
    @Size(min = 4, max = 10)
    private String expirationDate;
    @Size(min = 3, max = 3)
    private String cvvCode;

    public @NotNull @DecimalMin(value = "0.0", inclusive = false) @Digits(integer = 5, fraction = 2) BigDecimal getValuePaid() {
        return valuePaid;
    }

    public void setValuePaid(@NotNull @DecimalMin(value = "0.0", inclusive = false) @Digits(integer = 5, fraction = 2) BigDecimal valuePaid) {
        this.valuePaid = valuePaid;
    }

    public @NotBlank String getCardHolderFullName() {
        return cardHolderFullName;
    }

    public void setCardHolderFullName(@NotBlank String cardHolderFullName) {
        this.cardHolderFullName = cardHolderFullName;
    }

    public @NotBlank @CPF String getCardHolderCpf() {
        return cardHolderCpf;
    }

    public void setCardHolderCpf(@NotBlank @CPF String cardHolderCpf) {
        this.cardHolderCpf = cardHolderCpf;
    }

    public @NotBlank @Size(min = 16, max = 20) String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(@NotBlank @Size(min = 16, max = 20) String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public @NotBlank @Size(min = 4, max = 10) String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(@NotBlank @Size(min = 4, max = 10) String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public @Size(min = 3, max = 3) String getCvvCode() {
        return cvvCode;
    }

    public void setCvvCode(@Size(min = 3, max = 3) String cvvCode) {
        this.cvvCode = cvvCode;
    }
}
