package com.insha.moneymanager.service;

import com.insha.moneymanager.dto.ExpenseDTO;
import com.insha.moneymanager.dto.IncomeDTO;
import com.insha.moneymanager.entity.CategoryEntity;
import com.insha.moneymanager.entity.ExpenseEntity;
import com.insha.moneymanager.entity.IncomeEntity;
import com.insha.moneymanager.entity.ProfileEntity;
import com.insha.moneymanager.repository.CategoryRepository;
import com.insha.moneymanager.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;

    // this method will add expense to database for the current profile
    public ExpenseDTO addExpense(ExpenseDTO expenseDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();

        CategoryEntity category = categoryRepository.findById(expenseDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        ExpenseEntity newExpense = toEnitity(expenseDTO, profile, category);
        newExpense = expenseRepository.save(newExpense);
        return toDTO(newExpense);
    }

    // get all expenses for the current user sorted by date in descending order
    public List<ExpenseDTO> getAllExpensesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();

        List<ExpenseEntity> list = expenseRepository.findByProfileIdOrderByDateDesc(profile.getId());

        return list.stream().map(this::toDTO).toList();
    }

    // Retrieve all expenses for the current month/based on the start date and end date
    public List<ExpenseDTO> getCurrentMonthExpensesforCurrentUser(){
       ProfileEntity profile =  profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

        List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDateBetween(profile.getId() ,startDate, endDate);

        return list.stream().map(this::toDTO).toList();
    }

    // Delete an expense by id for the current user
    public void deleteExpenseById(Long expenseID) {
        ProfileEntity profile = profileService.getCurrentProfile();

        ExpenseEntity entity = expenseRepository.findById(profile.getId())
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if(entity.getProfile().getId() != profile.getId()) {
            throw new RuntimeException("Unauthorized to delete this expense");
        }

        expenseRepository.delete(entity);
    }

    // get Latest 5 expenses for the current user
    public List<ExpenseDTO> getLatest5ExpensesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();

        List<ExpenseEntity> list = expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());

        return list.stream().map(this::toDTO).toList();
    }


    // This method will calculate total expenses for the current user
    public BigDecimal getTotalExpensesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();

        BigDecimal totalExpenses = expenseRepository.findToatalExpenseAmountByProfileId(profile.getId());
        return totalExpenses != null ? totalExpenses : BigDecimal.ZERO;
    }

    // filter expense by date range and keyword
    public List<ExpenseDTO> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        ProfileEntity profile = profileService.getCurrentProfile();

        List<ExpenseEntity> list = expenseRepository.
                findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate, endDate, keyword, sort);

        return list.stream().map(this::toDTO).toList();
    }

    // notification or reminder for expense exceeding certain limit can be added here
    public List<ExpenseDTO> getExpensesForUserOnDate(Long profileId, LocalDate date) {

        List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDate(profileId, date);

        return list.stream().map(this::toDTO).toList();
    }

    //helper method to convert DTO to Entity
    private ExpenseEntity toEnitity(ExpenseDTO expenseDTO, ProfileEntity profile, CategoryEntity categoryEntity) {
        return ExpenseEntity.builder()
                .name(expenseDTO.getName())
                .icon(expenseDTO.getIcon())
                .amount(expenseDTO.getAmount())
                .date(expenseDTO.getDate())
                .profile(profile)
                .category(categoryEntity)
                .build();
    }

    //helper method to convert Entity to DTO
    private ExpenseDTO toDTO(ExpenseEntity expenseEntity) {
        return ExpenseDTO.builder()
                .id(expenseEntity.getId())
                .name(expenseEntity.getName())
                .icon(expenseEntity.getIcon())
                .amount(expenseEntity.getAmount())
                .date(expenseEntity.getDate())
                .createdAt(expenseEntity.getCreatedAt())
                .updatedAt(expenseEntity.getUpdatedAt())
                .categoryId(expenseEntity.getCategory() != null ? expenseEntity.getCategory().getId() : null)
                .categoryName(expenseEntity.getCategory() != null ? expenseEntity.getCategory().getName() : "N/A")
                .build();

    }
}
