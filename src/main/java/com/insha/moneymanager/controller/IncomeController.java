package com.insha.moneymanager.controller;

import com.insha.moneymanager.dto.ExpenseDTO;
import com.insha.moneymanager.dto.IncomeDTO;
import com.insha.moneymanager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/incomes")
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<IncomeDTO> addExpense(@RequestBody IncomeDTO incomeDTO) {
        IncomeDTO savedIncome = incomeService.addIncome(incomeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedIncome);
    }

    // Retrieve all incomes for the current user sorted by date in descending order
    @GetMapping("/all")
    public ResponseEntity<List<IncomeDTO>> getALlExpensesForCurrentUser() {
        List<IncomeDTO> incomes = incomeService.getAllIncomesForCurrentUser();
        return ResponseEntity.ok().body(incomes);
    }

    // Retrieve all income for the current month/based on the start date and end date
    @GetMapping
    public ResponseEntity<List<IncomeDTO>> getCurrentMonthIncomesforCurrentUser() {
        List<IncomeDTO> incomes = incomeService.getCurrentMonthIncomesforCurrentUser();
        return ResponseEntity.ok().body(incomes);
    }

    // delete an income by id for the current user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncomeById(@PathVariable Long id) {
        incomeService.deleteIncomeById(id);

        return ResponseEntity.noContent().build();
    }
}
