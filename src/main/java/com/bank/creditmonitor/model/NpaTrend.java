package com.bank.creditmonitor.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "npa_trend")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class NpaTrend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "month_year", nullable = false, unique = true, length = 10)
    private String monthYear;

    @Column(name = "npa_ratio", nullable = false, precision = 5, scale = 2)
    private BigDecimal npaRatio;

    @Column(name = "total_advances", precision = 15, scale = 2)
    private BigDecimal totalAdvances;

    @Column(name = "npa_amount", precision = 15, scale = 2)
    private BigDecimal npaAmount;

    @Column(name = "recorded_at")
    private LocalDate recordedAt;
}
