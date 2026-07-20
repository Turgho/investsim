package com.turgho.investsim.domain.rate;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyRate(
    LocalDate date,             // data da taxa
    BigDecimal value            // valor da taxa (ex: 0.00013699 para Selic diaria)
) { }
