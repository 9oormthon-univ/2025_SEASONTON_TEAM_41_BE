package com.debt.debt.domain.debtsSimulation.service;

import com.debt.debt.domain.debtsSimulation.dto.MonthlySchedule;
import com.debt.debt.domain.debtsSimulation.dto.SimulationRequest;
import com.debt.debt.domain.debtsSimulation.dto.SimulationResponse;
import com.debt.debt.domain.debtsSimulation.entity.DebtSimulation;
import com.debt.debt.domain.debtsSimulation.entity.SimulationType;
import com.debt.debt.domain.debtsSimulation.repository.DebtSimulationRepository;
import com.debt.debt.domain.user.entity.User;
import com.debt.debt.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DebtsSimulationService {
    private final DebtSimulationRepository simulationRepository;
    private final UserRepository userRepository;

    // 원리금 균등 : 월 상환액 계산
    private double calculateEqualPayment(double principal, double monthlyRate, int months) {
        if (months <= 0) throw new IllegalArgumentException("기간(개월)은 1 이상이어야 합니다.");
        if (monthlyRate == 0) return principal / months;
        double a = Math.pow(1 + monthlyRate, months);
        return principal * (monthlyRate * a) / (a - 1);
    }

    // 원리금 균등 : 월납입이 주어졌을 때 필요한 개월 수
    private int calculateMonthsForPayment(double principal, double monthlyRate, double monthlyPayment) {
        if (monthlyPayment <= 0) throw new IllegalArgumentException("월 상환액은 1원 이상이어야 합니다.");
        if (principal == 0) return 0;
        if(monthlyRate == 0) return (int) Math.ceil(principal / monthlyPayment);

        double interestOnly = principal * monthlyRate;
        if (monthlyPayment <= interestOnly) {
            throw new IllegalArgumentException("월 상환액이 월 이자(" + (long)interestOnly + "원) 이하라 상환이 불가능합니다.");
        }
        double n = Math.log(monthlyPayment / (monthlyPayment - monthlyRate * principal)) / Math.log(1 + monthlyRate);
        return (int) Math.ceil(n);
    }

    // 고정 월납입 시 시뮬레이션 (수정: monthlyRate 추가)
    private List<MonthlySchedule> simulateScheduleWithFixedPayment(double principal, double monthlyRate, double monthlyPayment, int monthsCap) {
        List<MonthlySchedule> schedule = new ArrayList<>();
        if (monthlyPayment <= 0 || principal <= 0) return schedule;

        double balance = principal;
        int month = 0;

        while (balance > 0 && month < monthsCap) {
            month++;
            double interest = balance * monthlyRate;
            double principalPayment = monthlyPayment - interest;

            if (principalPayment <= 0) {
                throw new IllegalArgumentException("월 상환액이 월 이자보다 작거나 같아 상환이 불가능합니다.");
            }

            if (principalPayment >= balance) {
                // 마지막 회차
                double payment = balance + interest;
                schedule.add(MonthlySchedule.builder()
                        .monthIndex(month)
                        .payment(Math.round(Math.ceil(payment)))
                        .principalPayment(Math.round(Math.ceil(balance)))
                        .interestPayment(Math.round(Math.ceil(interest)))
                        .remainPrincipal(0L)
                        .build());
                balance = 0;
                break;
            } else {
                balance -= principalPayment;
                schedule.add(MonthlySchedule.builder()
                        .monthIndex(month)
                        .payment(Math.round(Math.ceil(monthlyPayment)))
                        .principalPayment(Math.round(Math.ceil(principalPayment)))
                        .interestPayment(Math.round(Math.ceil(interest)))
                        .remainPrincipal(Math.max(0L, Math.round(Math.floor(balance))))
                        .build());
            }
        }
        return schedule;
    }

    // 원리금 균등 방식
    private List<MonthlySchedule> simulateEqualPaymentSchedule(double principal, double monthlyRate, int months) {
        double monthlyPayment = calculateEqualPayment(principal, monthlyRate, months);
        return simulateScheduleWithFixedPayment(principal, monthlyRate, monthlyPayment, months);
    }

    // 원금 균등 방식
    private List<MonthlySchedule> simulateEqualPrincipalSchedule(double principal, double monthlyRate, int months) {
        List<MonthlySchedule> schedule = new ArrayList<>();
        double principalPart = principal / months;
        double remaining = principal;

        for (int i = 1; i <= months; i++) {
            double interest = remaining * monthlyRate;
            double principalPayment = (i == months) ? remaining : principalPart;
            double payment = principalPayment + interest;
            remaining -= principalPayment;

            schedule.add(MonthlySchedule.builder()
                    .monthIndex(i)
                    .payment(Math.round(Math.ceil(payment)))
                    .principalPayment(Math.round(Math.ceil(principalPayment)))
                    .interestPayment(Math.round(Math.ceil(interest)))
                    .remainPrincipal(Math.max(0L, Math.round(Math.floor(remaining))))
                    .build());
        }
        return schedule;
    }

    // 만기일시 방식
    private List<MonthlySchedule> simulateBulletSchedule(double principal, double monthlyRate, int months) {
        List<MonthlySchedule> schedule = new ArrayList<>();
        double interest = principal * monthlyRate;

        for (int i = 1; i <= months; i++) {
            double principalPayment = (i == months) ? principal : 0;
            double payment = principalPayment + interest;
            double remaining = (i == months) ? 0 : principal;

            schedule.add(MonthlySchedule.builder()
                    .monthIndex(i)
                    .payment(Math.round(Math.ceil(payment)))
                    .principalPayment(Math.round(Math.ceil(principalPayment)))
                    .interestPayment(Math.round(Math.ceil(interest)))
                    .remainPrincipal(Math.round(remaining))
                    .build());
        }
        return schedule;
    }

    private void validateOneInputOnly(SimulationRequest req) {
        int cnt = 0;
        if (req.getMonthlyPayment() != null) cnt++;
        if (req.getTargetPeriodMonths() != null) cnt++;
        if (req.getTargetEndDate() != null) cnt++;
        if (cnt == 0) throw new IllegalArgumentException("월 상환액, 목표 상환 기간, 목표 종료일 중 하나는 입력해야 합니다.");
        if (cnt > 1) throw new IllegalArgumentException("월 상환액, 목표 상환 기간, 목표 종료일 중 하나만 입력해야 합니다.");
    }

    public SimulationResponse runSimulation(SimulationRequest request) {
        validateOneInputOnly(request);

        String repaymentType = request.getRepaymentType() != null ? request.getRepaymentType() : "EQUAL_PAYMENT";

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        double principal = (user.getDebtAmount() != null ? user.getDebtAmount() : 0) * 10_000.0;
        double monthlyRate = (request.getInterestRate() != null ? request.getInterestRate() : 0.0) / 12.0 / 100.0;

        Integer requiredMonthlyPayment = null;
        Integer expectedPeriodMonths = null;
        LocalDate targetEndDate = null;
        Long finalLumpPayment = null;
        List<MonthlySchedule> schedule = new ArrayList<>();

        // 월 상환액 입력 시
        if (request.getMonthlyPayment() != null) {
            int months = simulateMonthsWithFixedPayment(principal, monthlyRate, request.getMonthlyPayment(), 600);
            expectedPeriodMonths = months;
            targetEndDate = LocalDate.now().plusMonths(months);

            switch (repaymentType) {
                case "EQUAL_PAYMENT":
                    schedule = simulateScheduleWithFixedPayment(principal, monthlyRate, request.getMonthlyPayment(), months);
                    break;
                case "EQUAL_PRINCIPAL":
                    schedule = simulateEqualPrincipalSchedule(principal, monthlyRate, months);
                    break;
                case "BULLET":
                    schedule = simulateBulletSchedule(principal, monthlyRate, months);
                    break;
            }
        }

        // 목표 기간 입력 시
        if (request.getTargetPeriodMonths() != null) {
            int months = request.getTargetPeriodMonths();
            expectedPeriodMonths = months;
            targetEndDate = LocalDate.now().plusMonths(months);

            switch (repaymentType) {
                case "EQUAL_PAYMENT":
                    schedule = simulateEqualPaymentSchedule(principal, monthlyRate, months);
                    break;
                case "EQUAL_PRINCIPAL":
                    schedule = simulateEqualPrincipalSchedule(principal, monthlyRate, months);
                    break;
                case "BULLET":
                    schedule = simulateBulletSchedule(principal, monthlyRate, months);
                    break;
            }
        }

        // 목표 종료일 입력 시
        if (request.getTargetEndDate() != null) {
            int months = (int) ChronoUnit.MONTHS.between(LocalDate.now(), request.getTargetEndDate());
            expectedPeriodMonths = months;
            targetEndDate = request.getTargetEndDate();

            switch (repaymentType) {
                case "EQUAL_PAYMENT":
                    schedule = simulateEqualPaymentSchedule(principal, monthlyRate, months);
                    break;
                case "EQUAL_PRINCIPAL":
                    schedule = simulateEqualPrincipalSchedule(principal, monthlyRate, months);
                    break;
                case "BULLET":
                    schedule = simulateBulletSchedule(principal, monthlyRate, months);
                    break;
            }
        }

        DebtSimulation simulation = DebtSimulation.builder()
                .user(user)
                .type(request.getMonthlyPayment() != null ? SimulationType.AMOUNT_BASED : SimulationType.PERIOD_BASED)
                .monthlyPayment(request.getMonthlyPayment())
                .targetPeriodMonths(request.getTargetPeriodMonths())
                .interestRate(request.getInterestRate())
                .repaymentType(repaymentType)
                .requiredMonthlyPayment(!schedule.isEmpty() ? (int)schedule.get(0).getPayment() : null)
                .expectedPeriodMonths(expectedPeriodMonths)
                .targetEndDate(targetEndDate)
                .finalLumpPayment(schedule.isEmpty() ? null : schedule.get(schedule.size()-1).getPayment())
                .createdAt(LocalDate.now())
                .build();

        simulationRepository.save(simulation);

        long totalInterest = schedule.stream().mapToLong(MonthlySchedule::getInterestPayment).sum();
        long totalPayment = schedule.stream().mapToLong(MonthlySchedule::getPayment).sum();

        Integer expectedPeriodYears = null;
        Integer expectedPeriodRemainingMonths = null;
        if (expectedPeriodMonths != null) {
            expectedPeriodYears = expectedPeriodMonths / 12;
            expectedPeriodRemainingMonths = expectedPeriodMonths % 12;
        }

        return SimulationResponse.builder()
                .repaymentType(repaymentType)
                .requiredMonthlyPayment(requiredMonthlyPayment)
                .expectedPeriodMonths(expectedPeriodMonths)
                .expectedPeriodYears(expectedPeriodYears)
                .expectedPeriodRemainingMonths(expectedPeriodRemainingMonths)
                .targetEndDate(targetEndDate)
                .finalLumpPayment(finalLumpPayment)
                .schedule(schedule)
                .totalInterest(totalInterest)
                .totalPayment(totalPayment)
                .build();
    }

    private int simulateMonthsWithFixedPayment(double principal, double monthlyRate, Integer monthlyPayment, int maxMonths) {
        if (monthlyPayment <= 0) throw new IllegalArgumentException("월 상환액은 1원 이상이어야 합니다.");
        if (principal <= 0) return 0;

        double balance = principal;
        int months = 0;

        while (balance > 0 && months < maxMonths) {
            double interest= balance * monthlyRate;
            double principalPayment = monthlyPayment - interest;

            if (principalPayment <= 0) {
                throw new IllegalArgumentException("월 상환액이 월 이자보다 작거나 같아 상환이 불가능합니다.");
            }

            if (principalPayment >= balance) {
                months++;
                balance = 0;
                break;
            }

            balance -= principalPayment;
            months++;
        }
        if (balance > 0) return maxMonths;
        return months;
    }

    public List<SimulationResponse> runSimulationBatch(List<SimulationRequest> requests) {
        List<SimulationResponse> responses = new ArrayList<>();
        for (SimulationRequest r : requests) {
            responses.add(runSimulation(r));
        }
        return responses;
    }
}
