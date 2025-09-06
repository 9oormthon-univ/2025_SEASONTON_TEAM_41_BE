package com.debt.debt.domain.debtsSimulation.dto;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimulationRequest {
    private String email;
    private Double interestRate; //연 이자율 (퍼센트에이지)
    private Integer monthlyPayment; //월 상환 목표 (원)
    private Integer targetPeriodMonths; //목표 기간 (개월)
    private LocalDate targetEndDate; //목표 종료일
    private String repaymentType; //원리금균등/원금균등/만기일시 총 3가지의 상환 방식
}

