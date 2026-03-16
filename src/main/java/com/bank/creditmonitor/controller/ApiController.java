package com.bank.creditmonitor.controller;

import com.bank.creditmonitor.dto.*;
import com.bank.creditmonitor.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ApiController {

    private final DashboardService dashboardService;
    private final BorrowerService borrowerService;
    private final AlertService alertService;

    // ── Dashboard ──────────────────────────────────────────────
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboard());
    }

    // ── Borrowers ──────────────────────────────────────────────
    @GetMapping("/borrowers")
    public ResponseEntity<List<BorrowerSummaryDTO>> getBorrowers(
            @RequestParam(required = false) String segment,
            @RequestParam(required = false) String search) {

        if (search != null && !search.isBlank())
            return ResponseEntity.ok(borrowerService.searchByName(search));
        if (segment != null && !segment.isBlank())
            return ResponseEntity.ok(borrowerService.getBySegment(segment));
        return ResponseEntity.ok(borrowerService.getAllBorrowers());
    }

    @GetMapping("/borrowers/{id}")
    public ResponseEntity<BorrowerDetailDTO> getBorrowerDetail(@PathVariable Long id) {
        return ResponseEntity.ok(borrowerService.getBorrowerDetail(id));
    }

    // ── Alerts ─────────────────────────────────────────────────
    @GetMapping("/alerts")
    public ResponseEntity<List<AlertDTO>> getAlerts(
            @RequestParam(required = false) String severity) {

        if (severity != null && !severity.isBlank())
            return ResponseEntity.ok(alertService.getAlertsBySeverity(severity));
        return ResponseEntity.ok(alertService.getOpenAlerts());
    }

    @PutMapping("/alerts/{id}/resolve")
    public ResponseEntity<AlertDTO> resolveAlert(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.resolveAlert(id));
    }

    // ── NPA Trend ──────────────────────────────────────────────
    @GetMapping("/npa-trend")
    public ResponseEntity<List<NpaTrendDTO>> getNpaTrend() {
        return ResponseEntity.ok(dashboardService.getDashboard().getNpaTrend());
    }
}
