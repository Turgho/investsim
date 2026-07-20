package com.turgho.investsim.domain.investment;

import java.math.BigDecimal;

public record Investment(
    BigDecimal initialValue,    // valor aplicado
    int months,                 // meses de aplicacao
    InvestmentType type         // POUPANCA, CDB, TESOURO_SELIC
) { }
