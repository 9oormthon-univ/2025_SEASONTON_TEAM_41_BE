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


/**
 * 상환 방식
 * (1) EQUAL_PAYMENT 원리금 균등
 * (2) EQUAL_PARINCIPAL 원금 균등
 * (3) BULLET 만기일시
 */

@Service
@RequiredArgsConstructor
public class DebtsSimulationService {
    private final DebtSimulationRepository simulationRepository;
    private final UserRepository userRepository;

    //원리금 균등 : 월 상환액
    private double calculateEqualPayment(double principal, double monthlyRate, int months) {
        if (months <= 0) throw new IllegalArgumentException("기간(개월)은 1 이상이어야 합니다.");
        if (monthlyRate == 0) return principal / months;

        double a = Math.pow(1 + monthlyRate, months);
        return principal * (monthlyRate * a) / (a - 1);
    }

    //원리금 균등: 월납입이 주어졌을 때 필요한 개월 수 (공식 이용)
    private int calculateMonthsForPayment(double principal, double monthlyRate, double monthlyPayment) {
        if (monthlyPayment <= 0) throw new IllegalArgumentException("월 상환액은 1원 이상이어야 합니다.");
        if (principal == 0) return 0;

        if(monthlyRate == 0) {
            return (int) Math.ceil(principal / monthlyPayment);
        }

        double interestOnly = principal * monthlyRate;
        if (monthlyPayment <= interestOnly) {
            throw new IllegalArgumentException("월 상환액이 월 이자(" + (long)interestOnly + "원) 이하라 상환이 불가능합니다.");
        }
        double n = Math.log(monthlyPayment / (monthlyPayment - monthlyRate * principal)) / Math.log(1 + monthlyRate);
        return (int) Math.ceil(n);
    }

    //일반 시뮬레이션 (1) 사용자가 고정 월납입 할 경우
    //매월 이자 먼저 차감하고 남는 금액은 원금 감액
    private int simulateMonthsWithFixedPayment(double principal, double monthlyRate, double monthlyPayment, int maxMonths) {
        if (monthlyPayment <= 0) throw new IllegalArgumentException("월 상환액은 1원 이상이어야 합니다.");
        if (principal <= 0) return 0;

        double balance = principal;
        int months = 0;
        while (balance > 0 && months < maxMonths) {
            double interest = balance * monthlyRate;
            double principalPayment = monthlyPayment - interest;
            if (principalPayment <= 0) {
                throw new IllegalArgumentException("월 상환액이 월 이자보다 작거나 같아 상환이 불가능합니다.");
            }
            balance -= principalPayment;
            months++;
        }
        if (balance > 0) {
            //무한 루프 방지
            return maxMonths;
        }
        return months;
    }

    //원금 균등 방식의 첫 달 상환액 (가장 큰 월 납입금)
    private double firstPaymentEqualPrincipal(double principal, double monthlyRate, int months) {
        if (months <= 0) throw new IllegalArgumentException("기간(개월)은 1 이상이어야 합니다.");
        double principalPart = principal / months;
        double firstMonthInterest = principal * monthlyRate;
        return principalPart + firstMonthInterest;
    }

    //만기일시 방식 : 월 이자(매월), 만기 시 원금 + 이자 일시 상환
    private long monthlyInterestForBullet(double principal, double monthlyRate) {
        return (long) Math.ceil(principal * monthlyRate);
    }

    private long finalLumpForBullet(double principal, double monthlyRate, int months) {
        //마지막 달에는 원금 + 마지막 달 이자
        double lastInterest = principal * monthlyRate;
        return (long) Math.ceil(principal * monthlyRate);
    }

    //입력 체크
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

        //상환방식 기본값 : 원리금균등
        String repaymentType = request.getRepaymentType() != null ? request.getRepaymentType() : "EQUAL_PAYMENT";

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 사용자"));

        double principal = (user.getDebtAmount() != null ? user.getDebtAmount() : 0) * 10_000.0;
        double monthlyRate = (request.getInterestRate() != null ? request.getInterestRate() : 0.0) / 12.0 / 100.0; //월 이자율 계산

        Integer requiredMonthlyPayment = null;
        Integer expectedPeriodMonths = null;
        LocalDate targetEndDate = null;
        Long finalLumpPayment = null;


        //1. 사용자가 월 상환액(monthlyPayment) 입력한 경우
        //>> 고정 월납입으로 시뮬레이션 -> 개월 수 계산
        if (request.getMonthlyPayment() != null) {
            int months = simulateMonthsWithFixedPayment(principal, monthlyRate, request.getMonthlyPayment(), 600);
            expectedPeriodMonths = months;
            targetEndDate = LocalDate.now().plusMonths(months);
        }

        //2. 목표 기간 (targetPeriodMonths) 입력한 경우
        //>> 방식에 따라 월 납입(대표값) 계산
        if (request.getTargetPeriodMonths() != null) {

            int months = request.getTargetPeriodMonths();

            switch (repaymentType) {
                case "EQUAL_PAYMENT": //(1) 원리금 균등
                    double monthly = calculateEqualPayment(principal, monthlyRate, months);
                    requiredMonthlyPayment = (int) Math.ceil(monthly);
                    break;

                case "EQUAL_PRINCIPAL": //원금 균등 -> 반환값으로 첫 달 상환액 제공 (가장 높은 월 납입)
                    double first = firstPaymentEqualPrincipal(principal, monthlyRate, months);
                    requiredMonthlyPayment = (int) Math.ceil(first);
                    break;

                case "BULLET": //만기일시 -> 매달 이자만 내고 마지막에 원금 + 이자 일시 상환
                    long monthlyInterest = monthlyInterestForBullet((long) principal, monthlyRate);
                    requiredMonthlyPayment = (int) monthlyInterest;
                    finalLumpPayment = finalLumpForBullet((long) principal, monthlyRate, months);
                    break;

                default:
                    throw new IllegalArgumentException("지원하지 않는 상환 방식: " + repaymentType);
            }
            targetEndDate = LocalDate.now().plusMonths(months);
        }

        //3. 목표 종료일 입력한 경우
        //>> 기간으로 변환 후 동일 로직 적용!!
        if (request.getTargetEndDate() != null) {
            int months = (int) ChronoUnit.MONTHS.between(LocalDate.now(), request.getTargetEndDate());
            if (months <= 0) throw new IllegalArgumentException("목표 종료일은 현재 날짜보다 미래여야 합니다.");

            switch (repaymentType) {
                case "EQUAL_PAYMENT": //(1) 원리금 균등
                    double monthly = calculateEqualPayment(principal, monthlyRate, months);
                    requiredMonthlyPayment = (int) Math.ceil(monthly);
                    break;

                case "EQUAL_PRINCIPAL": //원금 균등 -> 반환값으로 첫 달 상환액 제공 (가장 높은 월 납입)
                    double first = firstPaymentEqualPrincipal(principal, monthlyRate, months);
                    requiredMonthlyPayment = (int) Math.ceil(first);
                    break;

                case "BULLET": //만기일시 -> 매달 이자만 내고 마지막에 원금 + 이자 일시 상환
                    long monthlyInterest = monthlyInterestForBullet((long) principal, monthlyRate);
                    requiredMonthlyPayment = (int) monthlyInterest;
                    finalLumpPayment = finalLumpForBullet((long) principal, monthlyRate, months);
                    break;

                default:
                    throw new IllegalArgumentException("지원하지 않는 상환 방식: " + repaymentType);
            }
            targetEndDate = request.getTargetEndDate();
        }

        DebtSimulation simulation = DebtSimulation.builder()
                .user(user)
                .type(request.getMonthlyPayment() != null ? SimulationType.AMOUNT_BASED : SimulationType.PERIOD_BASED)
                .monthlyPayment(request.getMonthlyPayment())
                .targetPeriodMonths(request.getTargetPeriodMonths())
                .interestRate(request.getInterestRate())
                .repaymentType(repaymentType)
                .requiredMonthlyPayment(requiredMonthlyPayment)
                .expectedPeriodMonths(expectedPeriodMonths)
                .targetEndDate(targetEndDate)
                .finalLumpPayment(finalLumpPayment)
                .createdAt(LocalDate.now())
                .build();

        simulationRepository.save(simulation);

        Integer expectedPeriodYears = null;
        Integer expectedPeriodRemainingMonths = null;
        if (expectedPeriodMonths != null) {
            expectedPeriodYears = expectedPeriodMonths / 12;
            expectedPeriodRemainingMonths = expectedPeriodMonths % 12;
        }

        SimulationResponse response =  SimulationResponse.builder()
                .repaymentType(repaymentType)
                .requiredMonthlyPayment(requiredMonthlyPayment)
                .expectedPeriodMonths(expectedPeriodMonths)
                .expectedPeriodYears(expectedPeriodYears)
                .expectedPeriodRemainingMonths(expectedPeriodRemainingMonths)
                .targetEndDate(targetEndDate)
                .finalLumpPayment(finalLumpPayment)
                .build();

        return response;
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
