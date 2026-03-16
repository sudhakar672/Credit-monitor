package com.bank.creditmonitor.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class CreditAccountDTO {
    private Long id;
    private String accountNumber;
    private String accountType;
    private BigDecimal sanctionedLimit;
    private BigDecimal outstanding;
    private BigDecimal utilizationPct;
    private BigDecimal interestRate;
    private LocalDate disbursementDate;
    private LocalDate maturityDate;
    private String status;
    private Integer dpd;
}
