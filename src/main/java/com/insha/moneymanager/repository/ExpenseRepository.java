package com.insha.moneymanager.repository;

import com.insha.moneymanager.entity.ExpenseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public interface ExpenseRepository  extends JpaRepository<ExpenseEntity, Long> {

    // this method is used to get all expenses by profileId sorted by date in descending order
    // select * from tbl_expense where profile_id = ? order by date desc
    List<ExpenseEntity> findByProfileIdOrderByDateDesc(Long profileId);

    // this method is used to get latest 5 expenses by profileId
    // select * from tbl_expense where profile_id = ? order by date desc limit 5
    List<ExpenseEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    // this method is used to calculate total expense amount by profileId
    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE e.profile.id = :profileId")
    BigDecimal findToatalExpenseAmountByProfileId(@Param("profileId") Long profileId);


    // this method is used to search expenses by profileId, date range and keyword in name with sorting
    // select * from tbl_expense where profile_id = ? and date between ? and ? and name like %?%;
    List<ExpenseEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort
    );

    // this method is used to search expenses by profileId and date range with sorting
    // select * from tbl_expense where profile_id = ? and date between ? and ?;
    List<ExpenseEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);

    // this method is used to find expenses by profileId and exact date
    // select * from tbl_expense where profile_id = ? and date = ?;s
    List<ExpenseEntity> findByProfileIdAndDate(Long profileId, LocalDate date);
}
