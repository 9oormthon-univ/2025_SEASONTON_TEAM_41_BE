package com.debt.debt.domain.ledger.repository;

import com.debt.debt.domain.ledger.entity.Ledger;
import com.debt.debt.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LedgerRepository extends JpaRepository<Ledger, Long> {
    List<Ledger> findByUser(User user);
    List<Ledger> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
}
