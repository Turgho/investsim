package com.turgho.investsim.infrastructure.client;

// Campos em português propositalmente — espelha o JSON da API SGS
public record TaxaSgsDto(
    String data,
    String valor
) {}
