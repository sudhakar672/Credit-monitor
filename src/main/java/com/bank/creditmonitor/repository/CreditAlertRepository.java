package com.bank.creditmonitor.repository;

import com.bank.creditmonitor.model.CreditAlert;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CreditAlertRepository extends JpaRepository<CreditAlert, Long> {
    List<CreditAlert> findByIsResolvedFalseOrderByAlertTimeDesc();
    List<CreditAlert> findBySeverityAndIsResolvedFalse(String severity);
    List<CreditAlert> findByBorrowerIdOrderByAlertTimeDesc(Long borrowerId);
    long countByIsResolvedFalse();
    long countBySeverityAndIsResolvedFalse(String severity);
}
