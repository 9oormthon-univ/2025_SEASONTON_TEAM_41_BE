package com.debt.debt.domain.debtsSimulation.entity;

import com.debt.debt.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Debt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; //부채 이름
    private Long principal; //대출 금액
    private Double annualRate; //연 금리(%)
    private Integer periodMonths; //대출 기간(개월)

    @Enumerated(EnumType.STRING)
    private RepaymentType repaymentType;

    private LocalDate createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDate.now();
        }
    }
}
