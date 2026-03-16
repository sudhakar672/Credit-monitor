package com.bank.creditmonitor.dto;

import lombok.*;
import java.time.LocalDate;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class CibilScoreHistoryDTO {
    private Integer score;
    private LocalDate scoreDate;
    private String riskBand;
    private String remarks;
}
