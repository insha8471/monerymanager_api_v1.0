package com.insha.moneymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {
    private Long id;
    private Long profileId;
    private String name;  //
    private String icon;
    private String type; // e.g., "income" or "expense"
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
