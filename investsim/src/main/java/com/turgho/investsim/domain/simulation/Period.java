package com.turgho.investsim.domain.simulation;

import java.time.LocalDate;

public record Period(
    LocalDate start,            // data inicio
    LocalDate end               // data fim
) { }
