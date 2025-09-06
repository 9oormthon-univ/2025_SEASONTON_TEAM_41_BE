package com.debt.debt.domain.debtsSimulation.controller;

import com.debt.debt.domain.debtsSimulation.dto.DebtRequestDto;
import com.debt.debt.domain.debtsSimulation.dto.DebtResponseDto;
import com.debt.debt.domain.debtsSimulation.service.DebtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/debts")
@RequiredArgsConstructor
public class DebtController {

    private final DebtService debtService;

    @PostMapping("/add")
    public DebtResponseDto addDebt(@RequestParam String email, @RequestBody DebtRequestDto request) {
        return debtService.addDebt(email, request);
    }

    @GetMapping
    public List<DebtResponseDto> getDebts(@RequestParam String email) {
        return debtService.getUserDebts(email);
    }

    @GetMapping("/total")
    public Long getTotalDebt(@RequestParam String email) {
        return debtService.getTotalDebt(email);
    }
}
