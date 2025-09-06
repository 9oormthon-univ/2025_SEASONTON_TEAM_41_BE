package com.debt.debt.domain.debtsSimulation.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlySchedule {
    private int monthIndex; //회차
    private long payment; //총 납부액
    private long principalPayment; //원금 상환액
    private long interestPayment; //이자 상환액
    private long remainPrincipal; //상환 후 남은 원금
}
