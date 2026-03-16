package com.bank.creditmonitor;

import com.bank.creditmonitor.model.*;
import com.bank.creditmonitor.repository.*;
import com.bank.creditmonitor.service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DashboardServiceTest {

    @Mock PortfolioSummaryRepository portfolioSummaryRepo;
    @Mock CreditAlertRepository      alertRepo;
    @Mock NpaTrendRepository         npaTrendRepo;
    @Mock BorrowerRepository         borrowerRepo;
    @Mock CibilScoreRepository       cibilScoreRepo;
    @Mock CreditAccountRepository    accountRepo;

    @InjectMocks DashboardService dashboardService;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void deriveRiskLevel_highScore_returnsLow() {
        assertEquals("Low", dashboardService.deriveRiskLevel(780, 0, "ACTIVE"));
    }

    @Test
    void deriveRiskLevel_npaStatus_returnsHigh() {
        assertEquals("High", dashboardService.deriveRiskLevel(700, 0, "NPA"));
    }

    @Test
    void deriveRiskLevel_lowScore_returnsHigh() {
        assertEquals("High", dashboardService.deriveRiskLevel(480, 0, "ACTIVE"));
    }

    @Test
    void deriveRiskLevel_highDpd_returnsHigh() {
        assertEquals("High", dashboardService.deriveRiskLevel(700, 95, "ACTIVE"));
    }

    @Test
    void getDashboard_returnsDefaultWhenNoSummary() {
        when(portfolioSummaryRepo.findTopByOrderBySnapshotDateDesc()).thenReturn(Optional.empty());
        when(alertRepo.findByIsResolvedFalseOrderByAlertTimeDesc()).thenReturn(Collections.emptyList());
        when(npaTrendRepo.findAllByOrderByRecordedAtAsc()).thenReturn(Collections.emptyList());
        when(borrowerRepo.findAll()).thenReturn(Collections.emptyList());

        var dashboard = dashboardService.getDashboard();
        assertNotNull(dashboard);
        assertEquals(BigDecimal.ZERO, dashboard.getTotalExposure());
    }

    @Test
    void getDashboard_returnsSummaryData() {
        PortfolioSummary ps = new PortfolioSummary();
        ps.setTotalExposure(new BigDecimal("3520000000"));
        ps.setAvgCibil(712);
        ps.setNpaRatio(new BigDecimal("2.38"));
        ps.setWatchlistCount(47);
        ps.setPcr(new BigDecimal("68.4"));
        ps.setCrar(new BigDecimal("15.2"));
        ps.setLdr(new BigDecimal("74.8"));

        when(portfolioSummaryRepo.findTopByOrderBySnapshotDateDesc()).thenReturn(Optional.of(ps));
        when(alertRepo.findByIsResolvedFalseOrderByAlertTimeDesc()).thenReturn(Collections.emptyList());
        when(npaTrendRepo.findAllByOrderByRecordedAtAsc()).thenReturn(Collections.emptyList());
        when(borrowerRepo.findAll()).thenReturn(Collections.emptyList());

        var dashboard = dashboardService.getDashboard();
        assertEquals(712, dashboard.getAvgCibilScore());
        assertEquals(new BigDecimal("2.38"), dashboard.getNpaRatio());
        assertEquals(47, dashboard.getWatchlistCount());
    }

    @Test
    void toNpaTrendDTO_mapsCorrectly() {
        NpaTrend nt = new NpaTrend();
        nt.setMonthYear("Mar-26");
        nt.setNpaRatio(new BigDecimal("2.38"));
        nt.setTotalAdvances(new BigDecimal("3520000000"));
        nt.setNpaAmount(new BigDecimal("83776000"));
        nt.setRecordedAt(LocalDate.of(2026, 3, 31));

        var dto = dashboardService.toNpaTrendDTO(nt);
        assertEquals("Mar-26", dto.getMonthYear());
        assertEquals(new BigDecimal("2.38"), dto.getNpaRatio());
    }
}
