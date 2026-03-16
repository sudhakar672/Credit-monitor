package com.bank.creditmonitor.dto;

import lombok.*;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class BorrowerDetailDTO {
    private Long id;
    private String borrowerCode;
    private String name;
    private String segment;
    private String city;
    private String state;
    private String contactEmail;
    private String panNumber;
    private String gstin;
    private Integer latestCibilScore;
    private String riskBand;
    private String riskLevel;
    private List<CreditAccountDTO> accounts;
    private List<CibilScoreHistoryDTO> scoreHistory;
    private List<AlertDTO> alerts;
}
