package com.bank.creditmonitor.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class BorrowerSummaryDTO {
    private Long id;
    private String borrowerCode;
    private String name;
    private String segment;
    private String city;
    private String state;
    private Integer latestCibilScore;
    private String riskBand;
    private BigDecimal totalExposure;
    private BigDecimal totalLimit;
    private BigDecimal utilizationPct;
    private String accountStatus;
    private Integer dpd;
    private String riskLevel;
}
