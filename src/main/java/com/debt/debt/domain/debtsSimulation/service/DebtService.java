package com.debt.debt.domain.debtsSimulation.service;

import com.debt.debt.domain.debtsSimulation.dto.DebtRequestDto;
import com.debt.debt.domain.debtsSimulation.dto.DebtResponseDto;
import com.debt.debt.domain.debtsSimulation.entity.Debt;
import com.debt.debt.domain.debtsSimulation.repository.DebtRepository;
import com.debt.debt.domain.user.entity.User;
import com.debt.debt.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DebtService {

    private final DebtRepository debtRepository;
    private final UserRepository userRepository;

    public DebtResponseDto addDebt(String email, DebtRequestDto request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Debt debt = Debt.builder()
                .name(request.getName())
                .principal(request.getPrincipal())
                .annualRate(request.getAnnualRate())
                .periodMonths(request.getPeriodMonths())
                .repaymentType(request.getRepaymentType())
                .createdAt(LocalDate.now())
                .user(user)
                .build();
        debtRepository.save(debt);

        return DebtResponseDto.builder()
                .name(debt.getName())
                .repaymentType(debt.getRepaymentType())
                .principal(debt.getPrincipal())
                .remainingPeriod(calculateRemainingPeriod(debt.getCreatedAt(), debt.getPeriodMonths()))
                .build();
        }

    public List<DebtResponseDto> getUserDebts(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        List<Debt> debts = debtRepository.findByUser(user);

        return debts.stream()
                .map(d -> DebtResponseDto.builder()
                        .name(d.getName())
                        .repaymentType(d.getRepaymentType())
                        .principal(d.getPrincipal())
                        .remainingPeriod(calculateRemainingPeriod(d.getCreatedAt(), d.getPeriodMonths()))
                        .createdAt(d.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    private String calculateRemainingPeriod(LocalDate startDate, int periodMonths) {
        LocalDate endDate = startDate.plusMonths(periodMonths);
        Period period = Period.between(LocalDate.now(), endDate);
        int years = Math.max(period.getYears(), 0);
        int months = Math.max(period.getMonths(), 0);
        int days = Math.max(period.getDays(), 0);
        return String.format("%d년 %d개월 %d일", years, months, days);
    }

    public Long getTotalDebt(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return debtRepository.findByUser(user).stream()
                .mapToLong(Debt::getPrincipal)
                .sum();
    }
}
