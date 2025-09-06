package com.debt.debt.domain.debtsSimulation.entity;

import com.debt.debt.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "debt_simulations")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebtSimulation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //어떤 유저의 시뮬레이션인가?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email")
    private User user;

    //시뮬레이션 타입 (가격 기반 혹은 기간 기반)
    @Enumerated(EnumType.STRING)
    private SimulationType type;

    private Integer monthlyPayment; //월 상환 목표 금액!
    private Integer targetPeriodMonths; //목표 기간 (개월 단위)
    private Double interestRate; //연 이자율 (퍼센트에이지)

    private Integer requiredMonthlyPayment; //기간 기반 입력 시 결과
    private Integer expectedPeriodMonths; //금액 기반 입력 시 결과
    private LocalDate targetEndDate; //예상/목표 종료일

    private LocalDate createdAt;
}
