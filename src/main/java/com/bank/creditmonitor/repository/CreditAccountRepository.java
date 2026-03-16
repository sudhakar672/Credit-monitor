package com.bank.creditmonitor.repository;

import com.bank.creditmonitor.model.CreditAccount;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CreditAccountRepository extends JpaRepository<CreditAccount, Long> {
    Optional<CreditAccount> findByAccountNumber(String accountNumber);
    List<CreditAccount> findByBorrowerId(Long borrowerId);
    List<CreditAccount> findByStatus(String status);

    @Query("SELECT ca FROM CreditAccount ca WHERE ca.utilizationPct >= :threshold")
    List<CreditAccount> findHighUtilization(@Param("threshold") Double threshold);

    @Query("SELECT ca FROM CreditAccount ca WHERE ca.dpd > 0 ORDER BY ca.dpd DESC")
    List<CreditAccount> findDelinquentAccounts();
}
