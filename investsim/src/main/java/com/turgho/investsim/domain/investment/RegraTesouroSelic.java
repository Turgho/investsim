package com.turgho.investsim.domain.investment;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

import com.turgho.investsim.domain.rate.DailyRate;

// Acompanha a taxa Selic integral (sem percentual contratado)
public class RegraTesouroSelic implements InvestmentRule {

    private static final MathContext PRECISAO = MathContext.DECIMAL128;

    @Override
    public BigDecimal calculate(BigDecimal amount, List<DailyRate> dailyRates, int months) {
        BigDecimal acumulado = BigDecimal.ONE;
        for (DailyRate rate : dailyRates) {
            acumulado = acumulado.multiply(
                BigDecimal.ONE.add(rate.value(), PRECISAO), PRECISAO);
        }

        return amount.multiply(acumulado, PRECISAO)
            .setScale(2, RoundingMode.HALF_UP);
    }
}
