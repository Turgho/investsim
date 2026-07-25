package com.turgho.investsim.domain.investment;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

import com.turgho.investsim.domain.rate.DailyRate;

// Rende um percentual do CDI acumulado durante o periodo
public class RegraCdb implements InvestmentRule {

    private final BigDecimal cdiPercentage;
    private static final MathContext PRECISAO = MathContext.DECIMAL128;

    public RegraCdb(BigDecimal cdiPercentage) {
        this.cdiPercentage = cdiPercentage;
    }

    @Override
    public BigDecimal calculate(BigDecimal amount, List<DailyRate> dailyRates, int months) {
        BigDecimal acumulado = BigDecimal.ONE;
        for (DailyRate rate : dailyRates) {
            BigDecimal cdiDia = rate.value().multiply(cdiPercentage, PRECISAO);
            acumulado = acumulado.multiply(
                BigDecimal.ONE.add(cdiDia, PRECISAO), PRECISAO);
        }

        return amount.multiply(acumulado, PRECISAO)
            .setScale(2, RoundingMode.HALF_UP);
    }
}
