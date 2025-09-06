package com.debt.debt.domain.ledger.dto;

import lombok.*;

import java.time.LocalDate;

public class LedgerDto {

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LedgerRequest {
        private String email;
        private LocalDate date;
        private Integer amount;
        private String category;
        private String description;
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LedgerResponse{
        private Long id;
        private LocalDate date;
        private Integer amount;
        private String category;
        private String description;
    }
}
