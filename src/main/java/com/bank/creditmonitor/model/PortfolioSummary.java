package com.bank.creditmonitor.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "portfolio_summary")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PortfolioSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "snapshot_date", nullable = false, unique = true)
    private LocalDate snapshotDate;

    @Column(name = "total_exposure", precision = 15, scale = 2)
    private BigDecimal totalExposure;

    @Column(name = "avg_cibil")
    private Integer avgCibil;

    @Column(name = "npa_ratio", precision = 5, scale = 2)
    private BigDecimal npaRatio;

    @Column(name = "watchlist_count")
    private Integer watchlistCount;

    @Column(name = "pcr", precision = 5, scale = 2)
    private BigDecimal pcr;

    @Column(name = "crar", precision = 5, scale = 2)
    private BigDecimal crar;

    @Column(name = "ldr", precision = 5, scale = 2)
    private BigDecimal ldr;
}
