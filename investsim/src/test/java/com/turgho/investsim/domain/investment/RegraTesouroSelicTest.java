package com.turgho.investsim.domain.investment;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.turgho.investsim.domain.rate.DailyRate;

class RegraTesouroSelicTest {

    private final RegraTesouroSelic rule = new RegraTesouroSelic();

    // Cenario: Tesouro Selic com taxa constante
    @Test
    @DisplayName("Tesouro Selic deve acumular taxa diaria integral")
    void tesouroSelicDeveAcumularSelic() {
        // Selic diaria constante: ~12% a.a. ~= 0,0443%
        BigDecimal selicDiaria = new BigDecimal("0.000443");
        List<DailyRate> rates = criarTaxas(selicDiaria, 252); // 1 ano

        BigDecimal valor = new BigDecimal("10000.00");
        BigDecimal resultado = rule.calculate(valor, rates, 12);

        // 10000 * (1 + 0,000443)^252 ~= 10000 * 1,117 = 11170,00
        assertThat(resultado).isGreaterThan(new BigDecimal("11100.00"));
        assertThat(resultado).isLessThan(new BigDecimal("11300.00"));
    }

    // Cenario: Tesouro Selic deve render mais que Poupanca com mesma Selic
    @Test
    @DisplayName("Tesouro Selic deve render mais que Poupanca")
    void tesouroSelicDeveRenderMaisQuePoupanca() {
        BigDecimal selicDiaria = new BigDecimal("0.000443"); // ~12% a.a.
        List<DailyRate> rates = criarTaxas(selicDiaria, 252);
        BigDecimal valor = new BigDecimal("10000.00");

        RegraPoupanca poupanca = new RegraPoupanca();
        BigDecimal resultadoPoupanca = poupanca.calculate(valor, rates, 12);
        BigDecimal resultadoSelic = rule.calculate(valor, rates, 12);

        // Com Selic > 8,5%, Poupanca rende 0,5%/mes fixo
        // Tesouro Selic rende a Selic integral (~12% a.a.)
        assertThat(resultadoSelic).isGreaterThan(resultadoPoupanca);
    }

    // Helper: cria lista de taxas diarias iguais
    private List<DailyRate> criarTaxas(BigDecimal taxa, int dias) {
        return java.util.stream.IntStream.range(0, dias)
            .mapToObj(i -> new DailyRate(
                LocalDate.now().minusDays(dias - i), taxa))
            .toList();
    }
}
