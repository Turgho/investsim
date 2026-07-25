package com.turgho.investsim.infrastructure.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.turgho.investsim.domain.exception.TaxaIndisponivelException;
import com.turgho.investsim.domain.rate.DailyRate;

// Adaptacao DTO -> DailyRate acontece aqui, mantendo o dominio puro
@Component
public class BacenSgsClient {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String SERIE_SELIC = "11";
    private static final String SERIE_CDI = "12";
    private static final String SERIE_IPCA = "433";

    private final RestClient restClient;

    public BacenSgsClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Cacheable(cacheNames = "taxaSelic", key = "#dataInicial.toString() + '_' + #dataFinal.toString()")
    public List<DailyRate> buscarSelic(LocalDate dataInicial, LocalDate dataFinal) {
        return buscarTaxas(SERIE_SELIC, dataInicial, dataFinal);
    }

    @Cacheable(cacheNames = "taxaCdi", key = "#dataInicial.toString() + '_' + #dataFinal.toString()")
    public List<DailyRate> buscarCdi(LocalDate dataInicial, LocalDate dataFinal) {
        return buscarTaxas(SERIE_CDI, dataInicial, dataFinal);
    }

    @Cacheable(cacheNames = "taxaIpca", key = "#dataInicial.toString() + '_' + #dataFinal.toString()")
    public List<DailyRate> buscarIpca(LocalDate dataInicial, LocalDate dataFinal) {
        return buscarTaxas(SERIE_IPCA, dataInicial, dataFinal);
    }

    // @Cacheable so funciona em chamadas externas (proxy do Spring) — chamada interna ignora o cache
    private List<DailyRate> buscarTaxas(String codigoSerie, LocalDate dataInicial, LocalDate dataFinal) {
        try {
            String uri = "/bcdata.sgs.{codigo}/dados?formato=json&dataInicial={dataInicial}&dataFinal={dataFinal}";

            List<TaxaSgsDto> resposta = restClient.get()
                .uri(uri, codigoSerie,
                     dataInicial.format(FORMATO_DATA),
                     dataFinal.format(FORMATO_DATA))
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<List<TaxaSgsDto>>() {});

            if (resposta == null || resposta.isEmpty()) {
                throw new TaxaIndisponivelException(codigoSerie);
            }

            return resposta.stream()
                .map(this::toDailyRate)
                .toList();

        } catch (TaxaIndisponivelException e) {
            throw e;
        } catch (RestClientResponseException e) {
            throw new RuntimeException(
                "Erro ao consultar API SGS (serie " + codigoSerie + "): " + e.getStatusCode(), e);
        }
    }

    private DailyRate toDailyRate(TaxaSgsDto dto) {
        LocalDate data = LocalDate.parse(dto.data(), FORMATO_DATA);
        BigDecimal valor = new BigDecimal(dto.valor().replace(",", "."));
        return new DailyRate(data, valor);
    }
}
