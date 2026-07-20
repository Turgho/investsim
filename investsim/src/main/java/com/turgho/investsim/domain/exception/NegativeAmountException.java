package com.turgho.investsim.domain.exception;

import java.math.BigDecimal;

public class NegativeAmountException extends DomainException {

    public NegativeAmountException(BigDecimal amount) {
        super("Valor nao pode ser negativo: " + amount);
    }
}
