package com.debt.debt.domain.debtsSimulation.dto;

import com.debt.debt.domain.debtsSimulation.entity.RepaymentType;
import lombok.Data;

@Data
public class DebtRequestDto {
    private String name;
    private Long principal;
    private Double annualRate;
    private Integer periodMonths;
    private RepaymentType repaymentType;
}
