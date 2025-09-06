package com.debt.debt.domain.debtsSimulation.dto;

import com.debt.debt.domain.debtsSimulation.entity.RepaymentType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DebtResponseDto {
    private String name;
    private RepaymentType repaymentType;
    private Long principal;
    private String remainingPeriod;
    private LocalDate createdAt;
}
