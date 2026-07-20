package com.turgho.investsim.domain.exception;

public class InvalidDaysException extends DomainException {

    public InvalidDaysException(int days) {
        super("Dias invalidos: " + days + ". Minimo: 0");
    }
}
