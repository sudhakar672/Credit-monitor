package com.bank.creditmonitor.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

// ── Dashboard Summary ──────────────────────────────────────────────────────
@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class DashboardDTO {
    private BigDecimal totalExposure;
    private Integer avgCibilScore;
    private BigDecimal npaRatio;
    private Integer watchlistCount;
    private BigDecimal pcr;
    private BigDecimal crar;
    private BigDecimal ldr;
    private List<AlertDTO> recentAlerts;
    private List<NpaTrendDTO> npaTrend;
    private List<BorrowerSummaryDTO> watchList;
}
