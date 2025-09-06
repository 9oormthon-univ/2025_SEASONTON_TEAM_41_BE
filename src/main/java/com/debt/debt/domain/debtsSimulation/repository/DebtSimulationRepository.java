package com.debt.debt.domain.debtsSimulation.repository;

import com.debt.debt.domain.debtsSimulation.entity.DebtSimulation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DebtSimulationRepository extends JpaRepository<DebtSimulation, Long> {
}