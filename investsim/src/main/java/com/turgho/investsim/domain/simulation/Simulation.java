package com.turgho.investsim.domain.simulation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Tipo de dominio puro — sem anotacoes JPA, testavel sem Spring
public record Simulation(
    Long id,
    BigDecimal initialValue,
    int months,
    String investmentType,
    BigDecimal cdiPercentage,
    BigDecimal grossAmount,
    BigDecimal incomeTax,
    BigDecimal netAmount,
    BigDecimal netReturn,
    LocalDateTime createdAt
) {}
