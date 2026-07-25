package com.turgho.investsim.web;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turgho.investsim.application.SimulateUseCase;
import com.turgho.investsim.domain.investment.InvestmentType;
import com.turgho.investsim.domain.simulation.Simulation;

@RestController
@RequestMapping("/api/v1/simulate")
public class SimulationController {

    private final SimulateUseCase simulateUseCase;

    public SimulationController(SimulateUseCase simulateUseCase) {
        this.simulateUseCase = simulateUseCase;
    }

    @PostMapping
    public ResponseEntity<SimulationResponse> simulate(@RequestBody SimulationRequest request) {
        Simulation simulation = simulateUseCase.execute(
            request.initialValue(),
            request.months(),
            request.type(),
            request.cdiPercentage());

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(toResponse(simulation));
    }

    public record SimulationRequest(
        BigDecimal initialValue,
        int months,
        InvestmentType type,
        BigDecimal cdiPercentage
    ) {}

    public record SimulationResponse(
        Long id,
        BigDecimal initialValue,
        int months,
        String type,
        BigDecimal grossAmount,
        BigDecimal incomeTax,
        BigDecimal netAmount,
        BigDecimal netReturn
    ) {}

    private SimulationResponse toResponse(Simulation s) {
        return new SimulationResponse(
            s.id(), s.initialValue(), s.months(), s.investmentType(),
            s.grossAmount(), s.incomeTax(), s.netAmount(), s.netReturn());
    }
}
