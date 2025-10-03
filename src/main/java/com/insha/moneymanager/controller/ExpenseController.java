package com.insha.moneymanager.controller;

import com.insha.moneymanager.dto.ExpenseDTO;
import com.insha.moneymanager.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseDTO> addExpense(@RequestBody ExpenseDTO expenseDTO) {
        ExpenseDTO savedExpense = expenseService.addExpense(expenseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedExpense);
    }

    // Retrieve all expenses for the current user sorted by date in descending order
    @GetMapping("/all")
    public ResponseEntity<List<ExpenseDTO>> getALlExpensesForCurrentUser() {
        List<ExpenseDTO> expenses = expenseService.getAllExpensesForCurrentUser();
        return ResponseEntity.ok().body(expenses);
    }

    // Retrieve all expenses for the current month/based on the start date and end date
    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getCurrentMonthExpensesforCurrentUser(){
        List<ExpenseDTO> expenses = expenseService.getCurrentMonthExpensesforCurrentUser();
        return ResponseEntity.ok().body(expenses);
    }

    // delete an expense by id for the current user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpenseById(@PathVariable Long id) {
        expenseService.deleteExpenseById(id);

        return ResponseEntity.noContent().build();
    }
}
