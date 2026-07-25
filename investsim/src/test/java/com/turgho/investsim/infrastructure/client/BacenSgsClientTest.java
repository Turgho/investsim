package com.turgho.investsim.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.turgho.investsim.domain.exception.TaxaIndisponivelException;
import com.turgho.investsim.domain.rate.DailyRate;

// Unit tests — mock do RestClient, sem contexto Spring
@ExtendWith(MockitoExtension.class)
class BacenSgsClientTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private BacenSgsClient client;

    private static final LocalDate DATA_INICIAL = LocalDate.of(2024, 1, 1);
    private static final LocalDate DATA_FINAL = LocalDate.of(2024, 1, 31);

    @BeforeEach
    void setUp() {
        client = new BacenSgsClient(restClient);
        lenient().when(restClient.get()).thenReturn(requestHeadersUriSpec);
        lenient().when(requestHeadersUriSpec.uri(any(String.class), any(), any(), any()))
            .thenReturn(requestHeadersUriSpec);
        lenient().when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    @DisplayName("Resposta com um valor deve retornar DailyRate com data e valor corretos")
    void respostaComUmValor() {
        TaxaSgsDto dto = new TaxaSgsDto("02/01/2024", "0,054057");
        when(responseSpec.body(any(ParameterizedTypeReference.class)))
            .thenReturn(List.of(dto));

        List<DailyRate> resultado = client.buscarSelic(DATA_INICIAL, DATA_FINAL);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).date()).isEqualTo(LocalDate.of(2024, 1, 2));
        assertThat(resultado.get(0).value())
            .isEqualByComparingTo(new BigDecimal("0.054057"));
    }

    @Test
    @DisplayName("Resposta com multiplos valores deve retornar todos como DailyRate")
    void respostaComMultiplosValores() {
        TaxaSgsDto dto1 = new TaxaSgsDto("02/01/2024", "0,054057");
        TaxaSgsDto dto2 = new TaxaSgsDto("03/01/2024", "0,053923");
        TaxaSgsDto dto3 = new TaxaSgsDto("04/01/2024", "0,054102");
        when(responseSpec.body(any(ParameterizedTypeReference.class)))
            .thenReturn(List.of(dto1, dto2, dto3));

        List<DailyRate> resultado = client.buscarCdi(DATA_INICIAL, DATA_FINAL);

        assertThat(resultado).hasSize(3);
        assertThat(resultado.get(0).date()).isEqualTo(LocalDate.of(2024, 1, 2));
        assertThat(resultado.get(2).date()).isEqualTo(LocalDate.of(2024, 1, 4));
        assertThat(resultado.get(2).value())
            .isEqualByComparingTo(new BigDecimal("0.054102"));
    }

    @Test
    @DisplayName("Resposta vazia deve lancar TaxaIndisponivelException")
    void respostaVaziaDeveLancarExcecao() {
        when(responseSpec.body(any(ParameterizedTypeReference.class)))
            .thenReturn(List.of());

        assertThatThrownBy(() -> client.buscarSelic(DATA_INICIAL, DATA_FINAL))
            .isInstanceOf(TaxaIndisponivelException.class)
            .hasMessageContaining("11");
    }

    @Test
    @DisplayName("Resposta nula deve lancar TaxaIndisponivelException")
    void respostaNulaDeveLancarExcecao() {
        when(responseSpec.body(any(ParameterizedTypeReference.class)))
            .thenReturn(null);

        assertThatThrownBy(() -> client.buscarCdi(DATA_INICIAL, DATA_FINAL))
            .isInstanceOf(TaxaIndisponivelException.class)
            .hasMessageContaining("12");
    }

    @Test
    @DisplayName("Erro HTTP deve envolver excecao com status code preservado")
    void erroDeRedeDevePropagarExcecao() {
        RestClientResponseException original = new RestClientResponseException(
            "Service Unavailable", 503, "Service Unavailable",
            null, null, null);
        when(requestHeadersUriSpec.uri(any(String.class), any(), any(), any()))
            .thenThrow(original);

        assertThatThrownBy(() -> client.buscarIpca(DATA_INICIAL, DATA_FINAL))
            .isInstanceOf(RuntimeException.class)
            .hasCauseInstanceOf(RestClientResponseException.class)
            .hasMessageContaining("433")
            .hasMessageContaining("503");
    }

    @Test
    @DisplayName("Valor com virgula brasileira deve ser convertido corretamente no DailyRate")
    void valorComVirgulaBrasileira() {
        TaxaSgsDto dto = new TaxaSgsDto("02/01/2024", "0,123456");
        when(responseSpec.body(any(ParameterizedTypeReference.class)))
            .thenReturn(List.of(dto));

        List<DailyRate> resultado = client.buscarSelic(DATA_INICIAL, DATA_FINAL);

        assertThat(resultado.get(0).value())
            .isEqualByComparingTo(new BigDecimal("0.123456"));
    }
}
