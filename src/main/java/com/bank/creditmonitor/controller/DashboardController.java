package com.bank.creditmonitor.controller;

import com.bank.creditmonitor.dto.DashboardDTO;
import com.bank.creditmonitor.service.AlertService;
import com.bank.creditmonitor.service.BorrowerService;
import com.bank.creditmonitor.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final BorrowerService borrowerService;
    private final AlertService alertService;

    @GetMapping("/")
    public String dashboard(Model model) {
        DashboardDTO data = dashboardService.getDashboard();
        model.addAttribute("dashboard", data);
        model.addAttribute("criticalCount", alertService.countCriticalAlerts());
        model.addAttribute("openAlertCount", alertService.countOpenAlerts());
        return "dashboard";
    }

    @GetMapping("/borrowers")
    public String borrowers(
            @RequestParam(required = false) String segment,
            @RequestParam(required = false) String search,
            Model model) {

        if (search != null && !search.isBlank()) {
            model.addAttribute("borrowers", borrowerService.searchByName(search));
            model.addAttribute("search", search);
        } else if (segment != null && !segment.isBlank()) {
            model.addAttribute("borrowers", borrowerService.getBySegment(segment));
            model.addAttribute("segment", segment);
        } else {
            model.addAttribute("borrowers", borrowerService.getAllBorrowers());
        }
        return "borrowers";
    }

    @GetMapping("/borrowers/{id}")
    public String borrowerDetail(@PathVariable Long id, Model model) {
        model.addAttribute("borrower", borrowerService.getBorrowerDetail(id));
        return "borrower-detail";
    }

    @GetMapping("/alerts")
    public String alerts(
            @RequestParam(required = false) String severity,
            Model model) {

        if (severity != null && !severity.isBlank()) {
            model.addAttribute("alerts", alertService.getAlertsBySeverity(severity));
            model.addAttribute("severity", severity);
        } else {
            model.addAttribute("alerts", alertService.getOpenAlerts());
        }
        model.addAttribute("openCount", alertService.countOpenAlerts());
        model.addAttribute("criticalCount", alertService.countCriticalAlerts());
        return "alerts";
    }

    @PostMapping("/alerts/{id}/resolve")
    public String resolveAlert(@PathVariable Long id) {
        alertService.resolveAlert(id);
        return "redirect:/alerts";
    }
}
