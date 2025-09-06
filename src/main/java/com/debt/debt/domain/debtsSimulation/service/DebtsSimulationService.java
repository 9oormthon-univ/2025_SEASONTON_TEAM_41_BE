package com.debt.debt.domain.debtsSimulation.service;

import com.debt.debt.domain.debtsSimulation.dto.SimulationRequest;
import com.debt.debt.domain.debtsSimulation.dto.SimulationResponse;
import com.debt.debt.domain.debtsSimulation.entity.DebtSimulation;
import com.debt.debt.domain.debtsSimulation.entity.SimulationType;
import com.debt.debt.domain.debtsSimulation.repository.DebtSimulationRepository;
import com.debt.debt.domain.user.entity.User;
import com.debt.debt.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.mapping.MappingHelper;
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

    //원리금 균등 공식 적용했습니다!!
    //월 상환금 = 원금 × (월이자율 × (1+월이자율)^기간) / ((1+월이자율)^기간 - 1)

    /**
     * principal : 원금
     * monthlyRate : 월 이자율 (연이자율/12/100)
     * months : 상환 개월 수
     * return 월 상환 금액
     * 이런 형태로 코드 구성했습니다!
     */

    //월 상환액 계산
    private double calculateEqualPayment(double principal, double monthlyRate, int months) {
        if (months <= 0) throw new IllegalArgumentException("기간(개월)은 1 이상이어야 합니다.");
        if (monthlyRate == 0) return principal / months;

        double a = Math.pow(1 + monthlyRate, months);
        return principal * (monthlyRate * a) / (a - 1);
    }

    //월 상환액이 주어졌을 때 필요한 개월 수
    private int calculateMonthsForPayment(double principal, double monthlyRate, double monthlyPayment) {
        if (monthlyPayment <= 0) throw new IllegalArgumentException("월 상환액은 1원 이상이어야 합니다.");
        if (principal == 0) return 0;

        double interestOnly = principal * monthlyRate;
        if (monthlyPayment <= interestOnly) {
            throw new IllegalArgumentException("월 상환액이 월 이자(" + (long)interestOnly + "원) 이하라 상환이 불가능합니다.");
        }
        double n = Math.log(monthlyPayment / (monthlyPayment - monthlyRate * principal)) / Math.log(1 + monthlyRate);
        return (int) Math.ceil(n);
    }

    private void validateOneInputOnly(SimulationRequest req) {
        int cnt = 0;
        if (req.getMonthlyPayment() != null) cnt++;
        if (req.getTargetPeriodMonths() != null) cnt++;
        if (req.getTargetEndDate() != null) cnt++;
        if (cnt == 0) {
            throw new IllegalArgumentException("월 상환액, 목표 상환 기간(개월), 목표 상환 완료일 중 하나는 반드시 입력해야 합니다. ");
        }
        if (cnt > 1) {
            throw new IllegalArgumentException("월 상환액, 목표 상황 기간(개월), 목표 상환 완료일 중 하나만 입력해야 합니다.");
        }
    }

    public SimulationResponse runSimulation(SimulationRequest request) {
        validateOneInputOnly(request);

        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 사용자"));

        double principal = (user.getDebtAmount() != null ? user.getDebtAmount() : 0) * 10_000;
        double monthlyRate = request.getInterestRate() / 12.0 / 100.0; //월 이자율 계산

        Integer requiredMonthlyPayment = null;
        Integer expectedPeriodMonths = null;
        LocalDate targetEndDate = null;

        //월 상환액 입력 후 소요 기간 계산!!
        if (request.getMonthlyPayment() != null) {
            int n = calculateMonthsForPayment(principal, monthlyRate, request.getMonthlyPayment());

            expectedPeriodMonths = n;
            targetEndDate = LocalDate.now().plusMonths(n);
        }

        //목표 기간 입력 후 월 상환액 계산!!
        if (request.getTargetPeriodMonths() != null) {
            double m = calculateEqualPayment(principal, monthlyRate, request.getTargetPeriodMonths());
            requiredMonthlyPayment = (int) Math.ceil(m);
            targetEndDate = LocalDate.now().plusMonths(request.getTargetPeriodMonths());
        }

        //특정 목표 날짜 입력 후 월 상환액 계산
        if (request.getTargetEndDate() != null) {
            int months = (int) ChronoUnit.MONTHS.between(LocalDate.now(), request.getTargetEndDate());

            if (months <= 0) {
                throw new IllegalArgumentException("목표 종료일은 현재 날짜보다 미래여야 합니다.");
            }

            double m = calculateEqualPayment(principal, monthlyRate, months);
            requiredMonthlyPayment = (int) Math.ceil(m);
            targetEndDate = request.getTargetEndDate();
        }

        DebtSimulation simulation = DebtSimulation.builder()
                .user(user)
                .type(request.getMonthlyPayment() != null ? SimulationType.AMOUNT_BASED : SimulationType.PERIOD_BASED)
                .monthlyPayment(request.getMonthlyPayment())
                .targetPeriodMonths(request.getTargetPeriodMonths())
                .interestRate(request.getInterestRate())
                .requiredMonthlyPayment(requiredMonthlyPayment)
                .expectedPeriodMonths(expectedPeriodMonths)
                .targetEndDate(targetEndDate)
                .createdAt(LocalDate.now())
                .build();

        simulationRepository.save(simulation);

        Integer expectedPeriodYears = null;
        Integer expectedPeriodRemainingMonths = null;
        if (expectedPeriodMonths != null) {
            expectedPeriodYears = expectedPeriodMonths / 12;
            expectedPeriodRemainingMonths = expectedPeriodMonths % 12;
        }

        return SimulationResponse.builder()
                .requiredMonthlyPayment(requiredMonthlyPayment)
                .expectedPeriodMonths(expectedPeriodMonths)
                .expectedPeriodYears(expectedPeriodYears)
                .expectedPeriodRemainingMonths(expectedPeriodRemainingMonths)
                .targetEndDate(targetEndDate)
                .build();
    }

    //다양한 입력 조건 비교!!
    public List<SimulationResponse> runSimulationBatch(List<SimulationRequest> requests) {
        List<SimulationResponse> responses = new ArrayList<>();
        for (SimulationRequest r : requests) {
            responses.add(runSimulation(r));
        }
        return responses;
    }
}
