package com.bank.creditmonitor.service;

import com.bank.creditmonitor.dto.*;
import com.bank.creditmonitor.model.*;
import com.bank.creditmonitor.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BorrowerService {

    private final BorrowerRepository borrowerRepository;
    private final CreditAccountRepository accountRepository;
    private final CibilScoreRepository cibilScoreRepository;
    private final CreditAlertRepository alertRepository;
    private final DashboardService dashboardService;

    public List<BorrowerSummaryDTO> getAllBorrowers() {
        return borrowerRepository.findAll()
                .stream().map(this::mapToSummary).collect(Collectors.toList());
    }

    public List<BorrowerSummaryDTO> getBySegment(String segment) {
        return borrowerRepository.findBySegment(segment)
                .stream().map(this::mapToSummary).collect(Collectors.toList());
    }

    public List<BorrowerSummaryDTO> searchByName(String name) {
        return borrowerRepository.searchByName(name)
                .stream().map(this::mapToSummary).collect(Collectors.toList());
    }

    public BorrowerDetailDTO getBorrowerDetail(Long id) {
        Borrower b = borrowerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borrower not found: " + id));

        CibilScore latest  = cibilScoreRepository.findLatestByBorrowerId(id).orElse(null);
        List<CreditAccount> accounts = accountRepository.findByBorrowerId(id);
        List<CibilScore>    history  = cibilScoreRepository.findByBorrowerIdOrderByScoreDateDesc(id);
        List<CreditAlert>   alerts   = alertRepository.findByBorrowerIdOrderByAlertTimeDesc(id);

        int maxDpd = accounts.stream().mapToInt(a -> a.getDpd() != null ? a.getDpd() : 0).max().orElse(0);
        String worstStatus = accounts.stream()
                .map(CreditAccount::getStatus)
                .reduce("ACTIVE", (a, s) -> rankStatus(a) > rankStatus(s) ? a : s);

        return BorrowerDetailDTO.builder()
                .id(b.getId())
                .borrowerCode(b.getBorrowerCode())
                .name(b.getName())
                .segment(b.getSegment())
                .city(b.getCity())
                .state(b.getState())
                .contactEmail(b.getContactEmail())
                .panNumber(b.getPanNumber())
                .gstin(b.getGstin())
                .latestCibilScore(latest != null ? latest.getScore() : null)
                .riskBand(latest != null ? latest.getRiskBand() : null)
                .riskLevel(dashboardService.deriveRiskLevel(
                        latest != null ? latest.getScore() : 0, maxDpd, worstStatus))
                .accounts(accounts.stream().map(this::toAccountDTO).collect(Collectors.toList()))
                .scoreHistory(history.stream().map(this::toScoreHistoryDTO).collect(Collectors.toList()))
                .alerts(alerts.stream().map(dashboardService::toAlertDTO).collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public Borrower saveBorrower(Borrower borrower) {
        return borrowerRepository.save(borrower);
    }

    @Transactional
    public void deleteBorrower(Long id) {
        borrowerRepository.deleteById(id);
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private BorrowerSummaryDTO mapToSummary(Borrower b) {
        CibilScore latest  = cibilScoreRepository.findLatestByBorrowerId(b.getId()).orElse(null);
        List<CreditAccount> accounts = accountRepository.findByBorrowerId(b.getId());

        BigDecimal totalOutstanding = accounts.stream()
                .map(CreditAccount::getOutstanding)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalLimit = accounts.stream()
                .map(CreditAccount::getSanctionedLimit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal utilPct = totalLimit.compareTo(BigDecimal.ZERO) > 0
                ? totalOutstanding.multiply(BigDecimal.valueOf(100))
                        .divide(totalLimit, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        int score     = latest != null ? latest.getScore() : 0;
        int maxDpd    = accounts.stream().mapToInt(a -> a.getDpd() != null ? a.getDpd() : 0).max().orElse(0);
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
                .riskBand(latest != null ? latest.getRiskBand() : "Unknown")
                .totalExposure(totalOutstanding)
                .totalLimit(totalLimit)
                .utilizationPct(utilPct)
                .accountStatus(worstStatus)
                .dpd(maxDpd)
                .riskLevel(dashboardService.deriveRiskLevel(score, maxDpd, worstStatus))
                .build();
    }

    private int rankStatus(String status) {
        return switch (status) {
            case "NPA"    -> 3;
            case "WATCH"  -> 2;
            case "ACTIVE" -> 1;
            default       -> 0;
        };
    }

    private CreditAccountDTO toAccountDTO(CreditAccount ca) {
        return CreditAccountDTO.builder()
                .id(ca.getId())
                .accountNumber(ca.getAccountNumber())
                .accountType(ca.getAccountType())
                .sanctionedLimit(ca.getSanctionedLimit())
                .outstanding(ca.getOutstanding())
                .utilizationPct(ca.getUtilizationPct())
                .interestRate(ca.getInterestRate())
                .disbursementDate(ca.getDisbursementDate())
                .maturityDate(ca.getMaturityDate())
                .status(ca.getStatus())
                .dpd(ca.getDpd())
                .build();
    }

    private CibilScoreHistoryDTO toScoreHistoryDTO(CibilScore cs) {
        return CibilScoreHistoryDTO.builder()
                .score(cs.getScore())
                .scoreDate(cs.getScoreDate())
                .riskBand(cs.getRiskBand())
                .remarks(cs.getRemarks())
                .build();
    }
}
