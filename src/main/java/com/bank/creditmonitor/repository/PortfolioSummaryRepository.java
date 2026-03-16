package com.bank.creditmonitor.repository;

import com.bank.creditmonitor.model.PortfolioSummary;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PortfolioSummaryRepository extends JpaRepository<PortfolioSummary, Long> {
    Optional<PortfolioSummary> findTopByOrderBySnapshotDateDesc();
}
