package com.debt.debt.domain.debtsSimulation.dto;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimulationResponse {
    private String repaymentType; //사용자가 선택한 상환 방식
    private Integer requiredMonthlyPayment; //기간 기반 계산 결과 - 대표 월 납입
    private Integer expectedPeriodMonths; //금액 기반 계산 결과
    private LocalDate targetEndDate; //목표 종료일!

    private Integer expectedPeriodYears; //총 개월 수를 연 단위로 변환하기
    private Integer expectedPeriodRemainingMonths; //연 단위 제외 후 남은 개월!!

    private Long finalLumpPayment; //만기일시 방식에서 마지막에 지급할 일시금
}