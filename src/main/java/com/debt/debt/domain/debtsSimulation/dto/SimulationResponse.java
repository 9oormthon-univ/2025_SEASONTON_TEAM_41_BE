package com.debt.debt.domain.debtsSimulation.dto;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimulationResponse {
    private Integer requiredMonthlyPayment; //기간 기반 계산 결과
    private Integer expectedPeriodMonths; //금액 기반 계산 결과
    private LocalDate targetEndDate; //목표 종료일!
}
