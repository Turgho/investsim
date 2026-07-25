package com.turgho.investsim.domain.exception;

public class TaxaIndisponivelException extends DomainException {

    public TaxaIndisponivelException(String serie) {
        super("Taxa indisponivel para a serie: " + serie);
    }
}
