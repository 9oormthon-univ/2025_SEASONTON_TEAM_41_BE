package com.debt.debt.domain.debtsSimulation.dto;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimulationRequest {
    private String email;
    private Double interestRate;
    private Integer monthlyPayment;
    private Integer targetPeriodMonths;
    private LocalDate targetEndDate;
}

