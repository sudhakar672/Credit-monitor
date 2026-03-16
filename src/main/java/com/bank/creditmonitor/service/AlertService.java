package com.bank.creditmonitor.service;

import com.bank.creditmonitor.dto.AlertDTO;
import com.bank.creditmonitor.model.CreditAlert;
import com.bank.creditmonitor.repository.CreditAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertService {

    private final CreditAlertRepository alertRepository;
    private final DashboardService dashboardService;

    public List<AlertDTO> getOpenAlerts() {
        return alertRepository.findByIsResolvedFalseOrderByAlertTimeDesc()
                .stream().map(dashboardService::toAlertDTO).collect(Collectors.toList());
    }

    public List<AlertDTO> getAlertsBySeverity(String severity) {
        return alertRepository.findBySeverityAndIsResolvedFalse(severity)
                .stream().map(dashboardService::toAlertDTO).collect(Collectors.toList());
    }

    public long countOpenAlerts() {
        return alertRepository.countByIsResolvedFalse();
    }

    public long countCriticalAlerts() {
        return alertRepository.countBySeverityAndIsResolvedFalse("CRITICAL");
    }

    @Transactional
    public AlertDTO resolveAlert(Long alertId) {
        CreditAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found: " + alertId));
        alert.setIsResolved(true);
        alert.setResolvedAt(LocalDateTime.now());
        return dashboardService.toAlertDTO(alertRepository.save(alert));
    }

    @Transactional
    public AlertDTO createAlert(CreditAlert alert) {
        alert.setAlertTime(LocalDateTime.now());
        alert.setIsResolved(false);
        return dashboardService.toAlertDTO(alertRepository.save(alert));
    }
}
