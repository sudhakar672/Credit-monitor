package com.bank.creditmonitor.service;

import com.bank.creditmonitor.dto.*;
import com.bank.creditmonitor.model.*;
import com.bank.creditmonitor.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardService {

    private final PortfolioSummaryRepository portfolioSummaryRepository;
    private final CreditAlertRepository alertRepository;
    private final NpaTrendRepository npaTrendRepository;
    private final BorrowerRepository borrowerRepository;
    private final CibilScoreRepository cibilScoreRepository;
    private final CreditAccountRepository creditAccountRepository;

    public DashboardDTO getDashboard() {
        PortfolioSummary summary = portfolioSummaryRepository
                .findTopByOrderBySnapshotDateDesc()
                .orElse(getDefaultSummary());

        List<AlertDTO> alerts = alertRepository
                .findByIsResolvedFalseOrderByAlertTimeDesc()
                .stream().map(this::toAlertDTO).collect(Collectors.toList());

        List<NpaTrendDTO> trend = npaTrendRepository
                .findAllByOrderByRecordedAtAsc()
                .stream().map(this::toNpaTrendDTO).collect(Collectors.toList());

        List<BorrowerSummaryDTO> watchList = buildWatchList();

        return DashboardDTO.builder()
                .totalExposure(summary.getTotalExposure())
                .avgCibilScore(summary.getAvgCibil())
                .npaRatio(summary.getNpaRatio())
                .watchlistCount(summary.getWatchlistCount())
                .pcr(summary.getPcr())
                .crar(summary.getCrar())
                .ldr(summary.getLdr())
                .recentAlerts(alerts)
                .npaTrend(trend)
                .watchList(watchList)
                .build();
    }

    private List<BorrowerSummaryDTO> buildWatchList() {
        return borrowerRepository.findAll().stream().map(b -> {
            CibilScore latest = cibilScoreRepository
                    .findLatestByBorrowerId(b.getId()).orElse(null);
            List<CreditAccount> accounts = creditAccountRepository.findByBorrowerId(b.getId());

            BigDecimal totalOutstanding = accounts.stream()
                    .map(CreditAccount::getOutstanding)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalLimit = accounts.stream()
                    .map(CreditAccount::getSanctionedLimit)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal utilPct = totalLimit.compareTo(BigDecimal.ZERO) > 0
                    ? totalOutstanding.multiply(BigDecimal.valueOf(100)).divide(totalLimit, 2, java.math.RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            int score = latest != null ? latest.getScore() : 0;
            String riskBand = latest != null ? latest.getRiskBand() : "Unknown";
            int maxDpd = accounts.stream().mapToInt(a -> a.getDpd() != null ? a.getDpd() : 0).max().orElse(0);
            String worstStatus = accounts.stream()
                    .map(CreditAccount::getStatus)
                    .reduce("ACTIVE", (a, s) -> rankStatus(a) > rankStatus(s) ? a : s);

            return BorrowerSummaryDTO.builder()
                    .id(b.getId())
                    .borrowerCode(b.getBorrowerCode())
                    .name(b.getName())
                    .segment(b.getSegment())
                    .city(b.getCity())
                    .state(b.getState())
                    .latestCibilScore(score)
                    .riskBand(riskBand)
                    .totalExposure(totalOutstanding)
                    .totalLimit(totalLimit)
                    .utilizationPct(utilPct)
                    .accountStatus(worstStatus)
                    .dpd(maxDpd)
                    .riskLevel(deriveRiskLevel(score, maxDpd, worstStatus))
                    .build();
        }).collect(Collectors.toList());
    }

    private int rankStatus(String status) {
        return switch (status) {
            case "NPA"    -> 3;
            case "WATCH"  -> 2;
            case "ACTIVE" -> 1;
            default       -> 0;
        };
    }

    public String deriveRiskLevel(int score, int dpd, String status) {
        if ("NPA".equals(status) || score < 550 || dpd >= 90)  return "High";
        if ("WATCH".equals(status) || score < 650 || dpd >= 30) return "Medium";
        if (score < 750 || dpd > 0)                             return "Watch";
        return "Low";
    }

    public AlertDTO toAlertDTO(CreditAlert a) {
        return AlertDTO.builder()
                .id(a.getId())
                .borrowerName(a.getBorrower().getName())
                .alertType(a.getAlertType())
                .severity(a.getSeverity())
                .message(a.getMessage())
                .isResolved(a.getIsResolved())
                .alertTime(a.getAlertTime())
                .build();
    }

    public NpaTrendDTO toNpaTrendDTO(NpaTrend n) {
        return NpaTrendDTO.builder()
                .monthYear(n.getMonthYear())
                .npaRatio(n.getNpaRatio())
                .totalAdvances(n.getTotalAdvances())
                .npaAmount(n.getNpaAmount())
                .build();
    }

    private PortfolioSummary getDefaultSummary() {
        PortfolioSummary s = new PortfolioSummary();
        s.setTotalExposure(BigDecimal.ZERO);
        s.setAvgCibil(0);
        s.setNpaRatio(BigDecimal.ZERO);
        s.setWatchlistCount(0);
        s.setPcr(BigDecimal.ZERO);
        s.setCrar(BigDecimal.ZERO);
        s.setLdr(BigDecimal.ZERO);
        return s;
    }
}
