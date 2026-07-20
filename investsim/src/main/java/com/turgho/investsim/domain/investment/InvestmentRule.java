package com.turgho.investsim.domain.investment;

import java.math.BigDecimal;
import java.util.List;

import com.turgho.investsim.domain.rate.DailyRate;

// Contrato para todas as regras de investimento (Poupanca, CDB, Tesouro Selic)
// Cada implementacao define como calcular o montante bruto
public interface InvestmentRule {

    BigDecimal calculate(BigDecimal amount, List<DailyRate> dailyRates, int months);
}
