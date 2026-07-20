package com.turgho.investsim.domain.exception;

public class InvalidMonthsException extends DomainException {

    public InvalidMonthsException(int months) {
        super("Meses de aplicacao invalido: " + months + ". Minimo: 1");
    }
}
