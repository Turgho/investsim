package com.turgho.investsim.domain.rate;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CalculoIRTest {

    // Cenario: investimento de 180 dias (aliquota 22,5%)
    @Test
    @DisplayName("Deve aplicar aliquota de 22,5% para ate 180 dias")
    void deveAplicarAliquota225() {
        BigDecimal montante = new BigDecimal("10000.00");
        int dias = 180;

        BigDecimal ir = CalculoIR.apply(montante, dias);

        // 10000 * 0,225 = 2250,00
        assertThat(ir).isEqualByComparingTo(new BigDecimal("2250.00"));
    }

    // Cenario: investimento de 360 dias (aliquota 20%)
    @Test
    @DisplayName("Deve aplicar aliquota de 20% para ate 360 dias")
    void deveAplicarAliquota20() {
        BigDecimal montante = new BigDecimal("10000.00");
        int dias = 360;

        BigDecimal ir = CalculoIR.apply(montante, dias);

        // 10000 * 0,20 = 2000,00
        assertThat(ir).isEqualByComparingTo(new BigDecimal("2000.00"));
    }

    // Cenario: investimento de 720 dias (aliquota 17,5%)
    @Test
    @DisplayName("Deve aplicar aliquota de 17,5% para ate 720 dias")
    void deveAplicarAliquota175() {
        BigDecimal montante = new BigDecimal("10000.00");
        int dias = 720;

        BigDecimal ir = CalculoIR.apply(montante, dias);

        // 10000 * 0,175 = 1750,00
        assertThat(ir).isEqualByComparingTo(new BigDecimal("1750.00"));
    }

    // Cenario: investimento acima de 720 dias (aliquota 15%)
    @Test
    @DisplayName("Deve aplicar aliquota de 15% para mais de 720 dias")
    void deveAplicarAliquota15() {
        BigDecimal montante = new BigDecimal("10000.00");
        int dias = 721;

        BigDecimal ir = CalculoIR.apply(montante, dias);

        // 10000 * 0,15 = 1500,00
        assertThat(ir).isEqualByComparingTo(new BigDecimal("1500.00"));
    }

    // Cenario: dias negativos devem lancar excecao
    @Test
    @DisplayName("Deve lancar InvalidDaysException para dias negativos")
    void deveLancarExcecaoParaDiasNegativos() {
        assertThatThrownBy(() -> CalculoIR.apply(new BigDecimal("10000"), -1))
            .isInstanceOf(Exception.class);
    }

    // Cenario: findRate retorna a aliquota correta
    @Test
    @DisplayName("findRate deve retornar aliquota correta por faixa")
    void findRateDeveRetornarAliquotaCorreta() {
        assertThat(CalculoIR.findRate(180))
            .isEqualByComparingTo(new BigDecimal("0.225"));
        assertThat(CalculoIR.findRate(360))
            .isEqualByComparingTo(new BigDecimal("0.20"));
        assertThat(CalculoIR.findRate(720))
            .isEqualByComparingTo(new BigDecimal("0.175"));
        assertThat(CalculoIR.findRate(721))
            .isEqualByComparingTo(new BigDecimal("0.15"));
    }
}
