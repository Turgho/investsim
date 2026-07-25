package com.turgho.investsim.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.turgho.investsim.domain.investment.Investment;
import com.turgho.investsim.domain.investment.InvestmentType;
import com.turgho.investsim.domain.rate.DailyRate;
import com.turgho.investsim.domain.simulation.Simulation;
import com.turgho.investsim.domain.simulation.SimulationRepository;
import com.turgho.investsim.domain.simulation.SimulatorService;
import com.turgho.investsim.infrastructure.client.BacenSgsClient;

@Service
public class SimulateUseCase {

    private final SimulatorService simulatorService;
    private final SimulationRepository repository;
    private final BacenSgsClient bacenSgsClient;

    public SimulateUseCase(
            SimulatorService simulatorService,
            SimulationRepository repository,
            BacenSgsClient bacenSgsClient) {
        this.simulatorService = simulatorService;
        this.repository = repository;
        this.bacenSgsClient = bacenSgsClient;
    }

    public Simulation execute(
            BigDecimal initialValue,
            int months,
            InvestmentType type,
            BigDecimal cdiPercentage) {

        Investment investment = new Investment(initialValue, months, type);

        LocalDate hoje = LocalDate.now();
        LocalDate inicio = hoje.minusDays(252);

        List<DailyRate> selicRates = bacenSgsClient.buscarSelic(inicio, hoje);
        List<DailyRate> cdiRates = bacenSgsClient.buscarCdi(inicio, hoje);

        var result = simulatorService.simulate(
            investment, selicRates, cdiRates, cdiPercentage);

        Simulation simulation = new Simulation(
            null, initialValue, months, type.name(), cdiPercentage,
            result.grossAmount(), result.incomeTax(),
            result.netAmount(), result.netReturn(), null);

        return repository.save(simulation);
    }
}
