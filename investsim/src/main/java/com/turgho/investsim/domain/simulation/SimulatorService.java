package com.turgho.investsim.domain.simulation;

import java.math.BigDecimal;
import java.util.List;

import com.turgho.investsim.domain.exception.InvalidMonthsException;
import com.turgho.investsim.domain.exception.NegativeAmountException;
import com.turgho.investsim.domain.investment.Investment;
import com.turgho.investsim.domain.investment.RegraCdb;
import com.turgho.investsim.domain.investment.RegraPoupanca;
import com.turgho.investsim.domain.investment.RegraTesouroSelic;
import com.turgho.investsim.domain.rate.CalculoIR;
import com.turgho.investsim.domain.rate.DailyRate;

// Camada Domain: sem dependencia de Spring, testavel com JUnit puro
public class SimulatorService {

    // Sem estado — reutilizaveis entre chamadas
    private final RegraPoupanca regraPoupanca = new RegraPoupanca();
    private final RegraTesouroSelic regraTesouroSelic = new RegraTesouroSelic();

    public ScenarioResult simulate(
            Investment investment,
            List<DailyRate> selicRates,
            List<DailyRate> cdiRates,
            BigDecimal cdiPercentage) {

        validate(investment);

        BigDecimal grossAmount = calculateGrossAmount(
            investment, selicRates, cdiRates, cdiPercentage);

        // meses * 30: aproximacao aceitavel na tabela regressiva do IR
        int days = investment.months() * 30;

        BigDecimal incomeTax = CalculoIR.apply(grossAmount, days);
        BigDecimal netAmount = grossAmount.subtract(incomeTax);
        BigDecimal netReturn = netAmount.subtract(investment.initialValue());

        return new ScenarioResult(
            investment.type(),
            grossAmount,
            incomeTax,
            netAmount,
            netReturn
        );
    }

    private void validate(Investment investment) {
        if (investment.initialValue().compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeAmountException(investment.initialValue());
        }
        if (investment.months() < 1) {
            throw new InvalidMonthsException(investment.months());
        }
    }

    private BigDecimal calculateGrossAmount(
            Investment investment,
            List<DailyRate> selicRates,
            List<DailyRate> cdiRates,
            BigDecimal cdiPercentage) {

        return switch (investment.type()) {
            case POUPANCA -> regraPoupanca.calculate(
                investment.initialValue(), selicRates, investment.months());
            case CDB -> new RegraCdb(cdiPercentage).calculate(
                investment.initialValue(), cdiRates, investment.months());
            case TESOURO_SELIC -> regraTesouroSelic.calculate(
                investment.initialValue(), selicRates, investment.months());
        };
    }
}
