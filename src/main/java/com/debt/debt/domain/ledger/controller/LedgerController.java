package com.debt.debt.domain.ledger.controller;

import com.debt.debt.domain.ledger.dto.ApiResponse;
import com.debt.debt.domain.ledger.dto.LedgerDto;
import com.debt.debt.domain.ledger.service.LedgerService;
import com.debt.debt.domain.user.entity.User;
import com.debt.debt.domain.user.repository.UserRepository;
import com.debt.debt.global.exception.CustomException;
import com.debt.debt.global.exception.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
public class LedgerController {
    private final LedgerService ledgerService;
    private final UserRepository userRepository;

    @PostMapping
    public ApiResponse addLedger(@RequestBody LedgerDto.LedgerRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

        return ledgerService.addLedger(request, user);
    }

    @GetMapping("/{email}")
    public List<LedgerDto.LedgerResponse> getUserLedger(@PathVariable String email) {
        return ledgerService.getUserLedger(email);
    }

//    @GetMapping("/{userId}/monthly")
//    public List<LedgerDto.LedgerResponse> getMonthlyLedger(@PathVariable String userId,
//                                                           @RequestParam int year,
//                                                           @RequestParam int month) {
//        return ledgerService.getMonthlyLedger(userId, year, month);
//    }

    @GetMapping("/{email}/monthly/income-expense")
    public ApiResponse getMonthlyIncomeAndExpense(@PathVariable String email,
                                                  @RequestParam int year,
                                                  @RequestParam int month) {
        return ledgerService.getMonthlyIncomeAndExpense(email, year, month);
    }

    @PutMapping("/{ledgerId}")
    public ApiResponse updateLedger(@PathVariable Long ledgerId,
                                                 @RequestBody LedgerDto.LedgerRequest request) {
        return ledgerService.updatedLedger(ledgerId, request);
    }

    @DeleteMapping("/{ledgerId}")
    public ApiResponse deleteLedger(@PathVariable Long ledgerId) {
        return ledgerService.deleteLedger(ledgerId);
    }
}
