package com.turgho.investsim.domain.simulation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.turgho.investsim.domain.exception.InvalidMonthsException;
import com.turgho.investsim.domain.exception.NegativeAmountException;
import com.turgho.investsim.domain.investment.Investment;
import com.turgho.investsim.domain.investment.InvestmentType;
import com.turgho.investsim.domain.rate.DailyRate;

class SimulatorServiceTest {

    private final SimulatorService service = new SimulatorService();

    // Cenario: Poupanca com Selic > 8,5% a.a.
    @Test
    @DisplayName("Poupanca com Selic alta deve usar taxa fixa 0,5%/mes")
    void poupancaComSelicAlta() {
        Investment investment = new Investment(
            new BigDecimal("10000.00"), 12, InvestmentType.POUPANCA);

        // Selic ~12% a.a. (> 8,5%)
        List<DailyRate> selicRates = criarTaxas(new BigDecimal("0.000443"), 252);
        List<DailyRate> cdiRates = List.of();
        BigDecimal cdiPercentual = new BigDecimal("1.00");

        ScenarioResult result = service.simulate(
            investment, selicRates, cdiRates, cdiPercentual);

        // Montante bruto: 10000 * (1 + 0,005)^12 = 10616,78
        assertThat(result.grossAmount())
            .isEqualByComparingTo(new BigDecimal("10616.78"));
        // IR: 10616,78 * 20% (360 dias) = 2123,36
        assertThat(result.incomeTax())
            .isEqualByComparingTo(new BigDecimal("2123.36"));
        // Liquido: 10616,78 - 2123,36 = 8493,42
        assertThat(result.netAmount())
            .isEqualByComparingTo(new BigDecimal("8493.42"));
        assertThat(result.type()).isEqualTo(InvestmentType.POUPANCA);
    }

    // Cenario: CDB 100% do CDI
    @Test
    @DisplayName("CDB 100% CDI deve acumular taxa diaria")
    void cdb100Cdi() {
        Investment investment = new Investment(
            new BigDecimal("10000.00"), 12, InvestmentType.CDB);

        List<DailyRate> selicRates = List.of();
        // CDI ~10% a.a.
        List<DailyRate> cdiRates = criarTaxas(new BigDecimal("0.00039"), 252);
        BigDecimal cdiPercentual = new BigDecimal("1.00");

        ScenarioResult result = service.simulate(
            investment, selicRates, cdiRates, cdiPercentual);

        // Montante bruto deve ser > 10000 (rendeu)
        assertThat(result.grossAmount()).isGreaterThan(new BigDecimal("10000.00"));
        // IR deve ser > 0
        assertThat(result.incomeTax()).isGreaterThan(BigDecimal.ZERO);
        // Liquido < Bruto (descontou IR)
        assertThat(result.netAmount()).isLessThan(result.grossAmount());
        assertThat(result.type()).isEqualTo(InvestmentType.CDB);
    }

    // Cenario: valor negativo deve lancar excecao
    @Test
    @DisplayName("Valor negativo deve lancar NegativeAmountException")
    void valorNegativoDeveLancarExcecao() {
        Investment investment = new Investment(
            new BigDecimal("-1000.00"), 12, InvestmentType.POUPANCA);

        assertThatThrownBy(() -> service.simulate(
            investment, List.of(), List.of(), new BigDecimal("1.00")))
            .isInstanceOf(NegativeAmountException.class);
    }

    // Cenario: meses < 1 deve lancar excecao
    @Test
    @DisplayName("Meses invalido deve lancar InvalidMonthsException")
    void mesesInvalidoDeveLancarExcecao() {
        Investment investment = new Investment(
            new BigDecimal("10000.00"), 0, InvestmentType.CDB);

        assertThatThrownBy(() -> service.simulate(
            investment, List.of(), List.of(), new BigDecimal("1.00")))
            .isInstanceOf(InvalidMonthsException.class);
    }

    // Helper: cria lista de taxas diarias iguais
    private List<DailyRate> criarTaxas(BigDecimal taxa, int dias) {
        return java.util.stream.IntStream.range(0, dias)
            .mapToObj(i -> new DailyRate(
                LocalDate.now().minusDays(dias - i), taxa))
            .toList();
    }
}
