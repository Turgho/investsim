package com.turgho.investsim.domain.investment;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.turgho.investsim.domain.rate.DailyRate;

class RegraCdbTest {

    // Cenario: CDB 100% do CDI com CDI constante
    @Test
    @DisplayName("CDB 100% CDI deve acumular taxa diaria")
    void cdb100DeveAcumularCdi() {
        // CDI diario constante: ~10% a.a. ~= 0,039%
        BigDecimal cdiDiaria = new BigDecimal("0.00039");
        BigDecimal percentual = new BigDecimal("1.00"); // 100% do CDI
        List<DailyRate> rates = criarTaxas(cdiDiaria, 252); // 1 ano

        RegraCdb rule = new RegraCdb(percentual);
        BigDecimal valor = new BigDecimal("10000.00");
        BigDecimal resultado = rule.calculate(valor, rates, 12);

        // 10000 * (1 + 0,00039)^252 ~= 10000 * 1,104 = 11040,00
        assertThat(resultado).isGreaterThan(new BigDecimal("11000.00"));
        assertThat(resultado).isLessThan(new BigDecimal("11100.00"));
    }

    // Cenario: CDB 110% do CDI
    @Test
    @DisplayName("CDB 110% CDI deve render mais que 100%")
    void cdb110DeveRenderMais() {
        BigDecimal cdiDiaria = new BigDecimal("0.00039");
        BigDecimal percentual = new BigDecimal("1.10"); // 110% do CDI
        List<DailyRate> rates = criarTaxas(cdiDiaria, 252);

        RegraCdb rule100 = new RegraCdb(new BigDecimal("1.00"));
        RegraCdb rule110 = new RegraCdb(percentual);
        BigDecimal valor = new BigDecimal("10000.00");

        BigDecimal resultado100 = rule100.calculate(valor, rates, 12);
        BigDecimal resultado110 = rule110.calculate(valor, rates, 12);

        assertThat(resultado110).isGreaterThan(resultado100);
    }

    // Helper: cria lista de taxas diarias iguais
    private List<DailyRate> criarTaxas(BigDecimal taxa, int dias) {
        return java.util.stream.IntStream.range(0, dias)
            .mapToObj(i -> new DailyRate(
                LocalDate.now().minusDays(dias - i), taxa))
            .toList();
    }
}
