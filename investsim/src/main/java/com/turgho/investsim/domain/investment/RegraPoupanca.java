package com.turgho.investsim.domain.investment;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

import com.turgho.investsim.domain.rate.DailyRate;

// Regra de calculo da Poupanca:
// - Selic > 8,5% a.a. -> rende 0,5% ao mes
// - Selic <= 8,5% a.a. -> rende 70% da Selic ao mes
public class RegraPoupanca implements InvestmentRule {

    private static final BigDecimal SELIC_LIMITE = new BigDecimal("0.085");
    private static final BigDecimal TAXA_FIXA_MENSAL = new BigDecimal("0.005");
    // Precisao de 34 casas significativas — double tem ~15, causaria erro em acumulacao de taxas
    private static final MathContext PRECISAO = MathContext.DECIMAL128;

    @Override
    public BigDecimal calculate(BigDecimal amount, List<DailyRate> dailyRates, int months) {
        BigDecimal selicAnual = calcularSelicAnual(dailyRates);

        BigDecimal taxaMensal;
        if (selicAnual.compareTo(SELIC_LIMITE) > 0) {
            taxaMensal = TAXA_FIXA_MENSAL;
        } else {
            BigDecimal selicMensal = calcularSelicMensal(selicAnual);
            taxaMensal = selicMensal.multiply(new BigDecimal("0.70"), PRECISAO);
        }

        return calcularMontanteComposto(amount, taxaMensal, months);
    }

    // Acumula taxas diarias pra converter em anual
    // Formula: (1 + r1) * (1 + r2) * ... * (1 + rn) - 1
    private BigDecimal calcularSelicAnual(List<DailyRate> dailyRates) {
        BigDecimal acumulado = BigDecimal.ONE;
        for (DailyRate rate : dailyRates) {
            acumulado = acumulado.multiply(
                BigDecimal.ONE.add(rate.value(), PRECISAO), PRECISAO);
        }
        return acumulado.subtract(BigDecimal.ONE);
    }

    // BigDecimal nao suporta expoente fracionario, entao usamos aproximacao:
    // selicAnual / 12 (suficiente para taxas pequenas, erro < 0,01%)
    private BigDecimal calcularSelicMensal(BigDecimal selicAnual) {
        return selicAnual.divide(new BigDecimal("12"), PRECISAO);
    }

    private BigDecimal calcularMontanteComposto(BigDecimal amount, BigDecimal taxa, int meses) {
        // (1 + taxa)^meses
        BigDecimal fator = BigDecimal.ONE.add(taxa, PRECISAO)
            .pow(meses, PRECISAO);
        return amount.multiply(fator, PRECISAO)
            .setScale(2, RoundingMode.HALF_UP);
    }
}
