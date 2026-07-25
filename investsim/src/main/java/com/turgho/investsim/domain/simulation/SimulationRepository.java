package com.turgho.investsim.domain.simulation;

import java.util.Optional;

// Porta de persistencia — dominio define o contrato, infrastructure/ implementa
public interface SimulationRepository {

    Simulation save(Simulation simulation);

    Optional<Simulation> findById(Long id);
}
