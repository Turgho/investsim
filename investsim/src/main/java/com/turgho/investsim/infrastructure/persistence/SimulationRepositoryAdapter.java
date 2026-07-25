package com.turgho.investsim.infrastructure.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.turgho.investsim.domain.simulation.Simulation;
import com.turgho.investsim.domain.simulation.SimulationRepository;

// Adaptador: traduz entre domain (Simulation) e JPA (SimulationEntity)
@Component
public class SimulationRepositoryAdapter implements SimulationRepository {

    private final JpaSimulationRepository jpaRepository;

    public SimulationRepositoryAdapter(JpaSimulationRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Simulation save(Simulation simulation) {
        SimulationEntity entity = toEntity(simulation);
        SimulationEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Simulation> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    private SimulationEntity toEntity(Simulation s) {
        return new SimulationEntity(
            s.initialValue(), s.months(), s.investmentType(),
            s.cdiPercentage(), s.grossAmount(), s.incomeTax(),
            s.netAmount(), s.netReturn());
    }

    private Simulation toDomain(SimulationEntity e) {
        return new Simulation(
            e.getId(), e.getInitialValue(), e.getMonths(),
            e.getInvestmentType(), e.getCdiPercentage(),
            e.getGrossAmount(), e.getIncomeTax(),
            e.getNetAmount(), e.getNetReturn(), e.getCreatedAt());
    }
}
