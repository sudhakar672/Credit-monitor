package com.bank.creditmonitor.repository;

import com.bank.creditmonitor.model.CibilScore;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CibilScoreRepository extends JpaRepository<CibilScore, Long> {

    List<CibilScore> findByBorrowerIdOrderByScoreDateDesc(Long borrowerId);

    @Query("SELECT cs FROM CibilScore cs WHERE cs.borrower.id = :borrowerId ORDER BY cs.scoreDate DESC LIMIT 1")
    Optional<CibilScore> findLatestByBorrowerId(@Param("borrowerId") Long borrowerId);

    @Query("SELECT cs FROM CibilScore cs WHERE cs.score < :threshold ORDER BY cs.score ASC")
    List<CibilScore> findBelowScore(@Param("threshold") int threshold);

    @Query("SELECT AVG(cs.score) FROM CibilScore cs WHERE cs.scoreDate = (SELECT MAX(cs2.scoreDate) FROM CibilScore cs2 WHERE cs2.borrower = cs.borrower)")
    Double findAverageCibilScore();
}
