package com.turgho.investsim.domain.investment;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

import com.turgho.investsim.domain.rate.DailyRate;

// Regra de calculo do CDB (Certificado de Deposito Bancario)
// Rende um percentual do CDI acumulado durante o periodo
public class RegraCdb implements InvestmentRule {

    // Percentual do CDI contratado (ex: 1.00 = 100% do CDI, 1.10 = 110%)
    private final BigDecimal cdiPercentage;

    // Precisao de 34 casas significativas — double tem ~15, causaria erro em acumulacao de taxas
    private static final MathContext PRECISAO = MathContext.DECIMAL128;

    public RegraCdb(BigDecimal cdiPercentage) {
        this.cdiPercentage = cdiPercentage;
    }

    @Override
    public BigDecimal calculate(BigDecimal amount, List<DailyRate> dailyRates, int months) {
        BigDecimal acumulado = BigDecimal.ONE;
        for (DailyRate rate : dailyRates) {
            // Multiplica pelo percentual contratado antes de acumular
            BigDecimal cdiDia = rate.value().multiply(cdiPercentage, PRECISAO);
            acumulado = acumulado.multiply(
                BigDecimal.ONE.add(cdiDia, PRECISAO), PRECISAO);
        }

        return amount.multiply(acumulado, PRECISAO)
            .setScale(2, RoundingMode.HALF_UP);
    }
}
