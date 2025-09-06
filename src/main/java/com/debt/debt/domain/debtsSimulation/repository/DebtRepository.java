package com.debt.debt.domain.debtsSimulation.repository;

import com.debt.debt.domain.debtsSimulation.entity.Debt;
import com.debt.debt.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DebtRepository extends JpaRepository<Debt, Long> {
    List<Debt> findByUser(User user);
}
