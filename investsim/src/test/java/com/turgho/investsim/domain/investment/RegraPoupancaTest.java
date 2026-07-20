package com.turgho.investsim.domain.investment;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.turgho.investsim.domain.rate.DailyRate;

class RegraPoupancaTest {

    private final RegraPoupanca rule = new RegraPoupanca();

    // Cenario: Selic > 8,5% a.a. -> taxa fixa de 0,5% ao mes
    @Test
    @DisplayName("Selic alta: deve usar taxa fixa de 0,5% ao mes")
    void selicAltaDeveUsarTaxaFixa() {
        // Simula Selic diaria que resulta em > 8,5% a.a.
        // 12% a.a. = taxa diaria ~= 0,0443%
        BigDecimal selicDiaria = new BigDecimal("0.000443");
        List<DailyRate> rates = criarTaxas(selicDiaria, 252); // 1 ano de taxas

        BigDecimal valor = new BigDecimal("10000.00");
        BigDecimal resultado = rule.calculate(valor, rates, 12);

        // 10000 * (1 + 0,005)^12 = 10000 * 1,06168 = 10616,78
        // Arredondamento: 10616,78
        assertThat(resultado).isEqualByComparingTo(new BigDecimal("10616.78"));
    }

    // Cenario: Selic <= 8,5% a.a. -> 70% da Selic mensalizada
    @Test
    @DisplayName("Selic baixa: deve usar 70% da Selic mensalizada")
    void selicBaixaDeveUsar70PorCento() {
        // Simula Selic diaria que resulta em 8% a.a.
        // 8% a.a. / 12 = ~0,6667% ao mes
        // 70% disso = ~0,4667% ao mes
        BigDecimal selicDiaria = new BigDecimal("0.000308"); // ~8% a.a.
        List<DailyRate> rates = criarTaxas(selicDiaria, 252);

        BigDecimal valor = new BigDecimal("10000.00");
        BigDecimal resultado = rule.calculate(valor, rates, 12);

        // Calculo manual:
        // Selic anual acumulada ~= 0,08 (8%)
        // Selic mensal ~= 0,08 / 12 = 0,006667
        // Taxa Poupanca = 0,006667 * 0,70 = 0,004667
        // Montante = 10000 * (1 + 0,004667)^12 ~= 10000 * 1,0575 = 10575,00
        assertThat(resultado).isGreaterThan(new BigDecimal("10500.00"));
        assertThat(resultado).isLessThan(new BigDecimal("10600.00"));
    }

    // Helper: cria lista de taxas diarias iguais
    private List<DailyRate> criarTaxas(BigDecimal taxa, int dias) {
        return java.util.stream.IntStream.range(0, dias)
            .mapToObj(i -> new DailyRate(
                LocalDate.now().minusDays(dias - i), taxa))
            .toList();
    }
}
