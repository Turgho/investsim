package com.turgho.investsim.domain.simulation;

import java.math.BigDecimal;

import com.turgho.investsim.domain.investment.InvestmentType;

public record ScenarioResult(
    InvestmentType type,         // qual cenario (POUPANCA, CDB, TESOURO_SELIC)
    BigDecimal grossAmount,      // montante bruto
    BigDecimal incomeTax,        // imposto de renda pago
    BigDecimal netAmount,        // montante liquido
    BigDecimal netReturn         // rentabilidade liquida (netAmount - initialValue)
) {}
