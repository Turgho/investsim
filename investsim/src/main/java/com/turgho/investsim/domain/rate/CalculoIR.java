package com.turgho.investsim.domain.rate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

// Tabela regressiva: <=180d=22,5% | <=360d=20% | <=720d=17,5% | >720d=15%
public final class CalculoIR {

    private static final MathContext PRECISAO = MathContext.DECIMAL128;

    private CalculoIR() {}

    // Retorna o valor do imposto (montante * aliquota), nao o liquido
    public static BigDecimal apply(BigDecimal grossAmount, int days) {
        if (days < 0) {
            throw new IllegalArgumentException("Dias nao podem ser negativos: " + days);
        }
        TaxBracket bracket = TaxBracket.findByDays(days);
        return grossAmount.multiply(bracket.rate(), PRECISAO)
            .setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal findRate(int days) {
        return TaxBracket.findByDays(days).rate();
    }
}
