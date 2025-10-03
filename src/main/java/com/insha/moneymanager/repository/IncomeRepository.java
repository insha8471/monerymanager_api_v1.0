package com.insha.moneymanager.repository;

import com.insha.moneymanager.entity.ExpenseEntity;
import com.insha.moneymanager.entity.IncomeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<IncomeEntity, Long> {

    // this method is used to get all expenses by profileId sorted by date in descending order
    // select * from tbl_income where profile_id = ? order by date desc
    List<IncomeEntity> findByProfileIdOrderByDateDesc(Long profileId);

    // this method is used to get latest 5 expenses by profileId
    // select * from tbl_expense where profile_id = ? order by date desc limit 5
    List<IncomeEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    // this method is used to calculate total income amount by profileId
    @Query("SELECT SUM(i.amount) FROM IncomeEntity i WHERE i.profile.id = :profileId")
    BigDecimal findTotalIncomeAmountByProfileId(@Param("profileId") Long profileId);


    // this method is used to search income by profileId, date range and keyword in name with sorting
    // select * from tbl_income where profile_id = ? and date between ? and ? and name like %?%;
    List<IncomeEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort
    );

    // this method is used to search income by profileId and date range with sorting
    // select * from tbl_income where profile_id = ? and date between ? and ?;
    List<IncomeEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);

}
