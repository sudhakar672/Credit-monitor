package com.bank.creditmonitor.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class NpaTrendDTO {
    private String monthYear;
    private BigDecimal npaRatio;
    private BigDecimal totalAdvances;
    private BigDecimal npaAmount;
}
