package com.debt.debt.domain.ledger.service;

import com.debt.debt.domain.ledger.dto.ApiResponse;
import com.debt.debt.domain.ledger.dto.LedgerDto;
import com.debt.debt.domain.ledger.entity.Ledger;
import com.debt.debt.domain.ledger.repository.LedgerRepository;
import com.debt.debt.domain.user.entity.User;
import com.debt.debt.domain.user.repository.UserRepository;
import com.debt.debt.global.exception.CustomException;
import com.debt.debt.global.exception.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LedgerService {
    private final LedgerRepository ledgerRepository;
    private final UserRepository userRepository;

    public ApiResponse addLedger(LedgerDto.LedgerRequest request, User user) {
        log.info("addLedger() 호출, email: {}", request.getEmail());

//        User user = userRepository.findByUserId(request.getUserId())
//                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

        Ledger ledger = Ledger.builder()
                .user(user)
                .date(request.getDate())
                .amount(request.getAmount())
                .category(request.getCategory())
                .description(request.getDescription())
                .build();

        ledgerRepository.save(ledger);

        log.info("내역 추가 완료, ledgerId: {}", ledger.getId());
        return ApiResponse.success(toDto(ledger));
    }

    //특정 유저 내역 조회
    public List<LedgerDto.LedgerResponse> getUserLedger(String email) {
        log.info("getUserLedger() 호출, email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

        log.info("사용자 내역 조회 완료, userId: {}", email);
        return ledgerRepository.findByUser(user).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    //월별 내역 조회
//    public List<LedgerDto.LedgerResponse> getMonthlyLedger(String userId, int year, int month) {
//        User user = userRepository.findByUserId(userId)
//                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
//        YearMonth ym = YearMonth.of(year, month);
//        LocalDate startDate = ym.atDay(1);
//        LocalDate endDate = ym.atEndOfMonth();
//
//        return ledgerRepository.findByUserAndDateBetween(user, startDate, endDate)
//                .stream()
//                .map(this::toDto)
//                .collect(Collectors.toList());
//    }

    //월별 지출/수입 내역 조회
    public ApiResponse getMonthlyIncomeAndExpense(String email, int year, int month) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

        YearMonth ym = YearMonth.of(year, month);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();

        List<Ledger> ledgerList = ledgerRepository.findByUserAndDateBetween(user, startDate, endDate);

        int totalIncome = ledgerList.stream()
                .filter(ledger->ledger.getAmount()>0)
                .mapToInt(Ledger::getAmount)
                .sum();

        int totalExpense = ledgerList.stream()
                .filter(ledger->ledger.getAmount()<0)
                .mapToInt(Ledger::getAmount)
                .sum();

        return ApiResponse.success(new MonthlyIncomeExpenseResponse(totalIncome, totalExpense));
    }

    //내역 수정
    public ApiResponse updatedLedger(Long ledgerId, LedgerDto.LedgerRequest request) {
        Ledger ledger = ledgerRepository.findById(ledgerId)
                .orElseThrow(()-> new CustomException(ErrorCode.LEDGER_NOT_FOUND));

        ledger.setDate(request.getDate());
        ledger.setAmount(request.getAmount());
        ledger.setCategory(request.getCategory());
        ledger.setDescription(request.getDescription());

        ledgerRepository.save(ledger);
        return ApiResponse.success(toDto(ledger));
    }

    //내역 삭제
    public ApiResponse deleteLedger(Long ledgerId) {
        Ledger ledger = ledgerRepository.findById(ledgerId)
                .orElseThrow(()-> new CustomException(ErrorCode.LEDGER_NOT_FOUND));

        ledgerRepository.delete(ledger);

        return ApiResponse.success("삭제가 완료되었습니다.");
    }

    private LedgerDto.LedgerResponse toDto(Ledger ledger) {
        return LedgerDto.LedgerResponse.builder()
                .id(ledger.getId())
                .date(ledger.getDate())
                .amount(ledger.getAmount())
                .category(ledger.getCategory())
                .description(ledger.getDescription())
                .build();
    }
    
    @Getter
    @AllArgsConstructor
    public static class MonthlyIncomeExpenseResponse {
        private int totalIncome;
        private int totalExpense;
    }

}
