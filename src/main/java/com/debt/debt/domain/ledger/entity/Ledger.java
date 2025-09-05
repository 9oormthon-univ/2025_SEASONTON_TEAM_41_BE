package com.debt.debt.domain.ledger.entity;

import com.debt.debt.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "leger")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ledger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDate date;

    private Integer amount; //금액! 양수: 수입, 음수: 지출
    private String category;
    private String description;
}
