package com.turgho.investsim.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import com.turgho.investsim.domain.exception.NegativeAmountException;
import com.turgho.investsim.domain.exception.TaxaIndisponivelException;

import java.math.BigDecimal;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/simulate");
    }

    @Test
    @DisplayName("DomainException deve retornar 400 com mensagem")
    void domainException() {
        NegativeAmountException ex = new NegativeAmountException(new BigDecimal("-1000"));

        ResponseEntity<ApiErrorResponse> response = handler.handleDomainException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().errors()).contains("Valor nao pode ser negativo: -1000");
        assertThat(response.getBody().path()).isEqualTo("/api/v1/simulate");
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    @DisplayName("TaxaIndisponivelException deve retornar 400 (extende DomainException)")
    void taxaIndisponivelException() {
        TaxaIndisponivelException ex = new TaxaIndisponivelException("11");

        ResponseEntity<ApiErrorResponse> response = handler.handleDomainException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().errors()).contains("Taxa indisponivel para a serie: 11");
    }

    @Test
    @DisplayName("Exception generica deve retornar 500 sem expor detalhes internos")
    void genericException() {
        Exception ex = new Exception("database connection failed");

        ResponseEntity<ApiErrorResponse> response = handler.handleGenericException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().errors()).containsExactly("Erro interno do servidor");
        assertThat(response.getBody().errors().toString()).doesNotContain("database");
    }
}
