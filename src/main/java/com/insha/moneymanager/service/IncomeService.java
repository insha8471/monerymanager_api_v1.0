package com.insha.moneymanager.service;

import com.insha.moneymanager.dto.ExpenseDTO;
import com.insha.moneymanager.dto.IncomeDTO;
import com.insha.moneymanager.entity.CategoryEntity;
import com.insha.moneymanager.entity.ExpenseEntity;
import com.insha.moneymanager.entity.IncomeEntity;
import com.insha.moneymanager.entity.ProfileEntity;
import com.insha.moneymanager.repository.CategoryRepository;
import com.insha.moneymanager.repository.IncomeRepository;
import lombok.*;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@RequiredArgsConstructor
@Service
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;

    // this method will add income for the current profile
    public IncomeDTO addIncome(IncomeDTO incomeDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();

        CategoryEntity category = categoryRepository.findById(incomeDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        IncomeEntity newIncome = toEnitity(incomeDTO, profile, category);
        newIncome = incomeRepository.save(newIncome);
        return toDTO(newIncome);
    }

    // get all incomes for the current user sorted by date in descending order
    public List<IncomeDTO> getAllIncomesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();

        List<IncomeEntity> list = incomeRepository.findByProfileIdOrderByDateDesc(profile.getId());

        return list.stream().map(this::toDTO).toList();
    }

    // Retrieve all income for the current month/based on the staet date and end date
    public List<IncomeDTO> getCurrentMonthIncomesforCurrentUser(){
        ProfileEntity profile =  profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

        List<IncomeEntity> list = incomeRepository.findByProfileIdAndDateBetween(profile.getId() ,startDate, endDate);

        return list.stream().map(this::toDTO).toList();
    }

    // Delete an expense by id for the current user
    public void deleteIncomeById(Long expenseID) {
        ProfileEntity profile = profileService.getCurrentProfile();

        IncomeEntity entity = incomeRepository.findById(profile.getId())
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if(entity.getProfile().getId() != profile.getId()) {
            throw new RuntimeException("Unauthorized to delete this income");
        }

        incomeRepository.delete(entity);
    }

    // get Latest 5 expenses for the current user
    public List<IncomeDTO> getLatest5IncomesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();

        List<IncomeEntity> list = incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());

        return list.stream().map(this::toDTO).toList();
    }


    // This method will calculate total expenses for the current user
    public BigDecimal getTotalIncomesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();

        BigDecimal totalIncome = incomeRepository.findTotalIncomeAmountByProfileId(profile.getId());
        return totalIncome != null ? totalIncome : BigDecimal.ZERO;
    }

    // filter income by date range and keyword
    public List<IncomeDTO> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        ProfileEntity profile = profileService.getCurrentProfile();

        List<IncomeEntity> list = incomeRepository.
                findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate, endDate, keyword, sort);

        return list.stream().map(this::toDTO).toList();
    }

    //helper method to convert DTO to Entity
    private IncomeEntity toEnitity(IncomeDTO incomeDTO, ProfileEntity profile, CategoryEntity categoryEntity) {
        return IncomeEntity.builder()
                .name(incomeDTO.getName())
                .icon(incomeDTO.getIcon())
                .amount(incomeDTO.getAmount())
                .date(incomeDTO.getDate())
                .profile(profile)
                .category(categoryEntity)
                .build();
    }

    //helper method to convert Entity to DTO
    private IncomeDTO toDTO(IncomeEntity incomeEntity) {
        return IncomeDTO.builder()
                .id(incomeEntity.getId())
                .name(incomeEntity.getName())
                .icon(incomeEntity.getIcon())
                .amount(incomeEntity.getAmount())
                .date(incomeEntity.getDate())
                .createdAt(incomeEntity.getCreatedAt())
                .updatedAt(incomeEntity.getUpdatedAt())
                .categoryId(incomeEntity.getCategory() != null ? incomeEntity.getCategory().getId() : null)
                .categoryName(incomeEntity.getCategory() != null ? incomeEntity.getCategory().getName() : "N/A")
                .build();

    }
}
