package com.turgho.investsim.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Sem @Repository o Spring nao registra o proxy (erro so em runtime)
@Repository
public interface JpaSimulationRepository extends JpaRepository<SimulationEntity, Long> {
}
