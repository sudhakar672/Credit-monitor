package com.bank.creditmonitor.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class AlertDTO {
    private Long id;
    private String borrowerName;
    private String alertType;
    private String severity;
    private String message;
    private Boolean isResolved;
    private LocalDateTime alertTime;
}
