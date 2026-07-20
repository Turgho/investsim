package com.turgho.investsim.domain.rate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

// Calculo do Imposto de Renda Regressivo
// Tabela: <=180d=22,5% | <=360d=20% | <=720d=17,5% | >720d=15%
public final class CalculoIR {

    // Precisao de 34 casas significativas — double tem ~15, causaria erro em acumulacao de taxas
    private static final MathContext PRECISAO = MathContext.DECIMAL128;

    // Construtor privado impede instanciacao (classe utilitaria)
    private CalculoIR() {}

    // Retorna o valor do imposto (montante * aliquota), nao o montante liquido
    public static BigDecimal apply(BigDecimal grossAmount, int days) {
        if (days < 0) {
            throw new IllegalArgumentException("Dias nao podem ser negativos: " + days);
        }
        TaxBracket bracket = TaxBracket.findByDays(days);
        return grossAmount.multiply(bracket.rate(), PRECISAO)
            .setScale(2, RoundingMode.HALF_UP);
    }

    // Util pra testes e pra expor no response se necessario
    public static BigDecimal findRate(int days) {
        return TaxBracket.findByDays(days).rate();
    }
}
