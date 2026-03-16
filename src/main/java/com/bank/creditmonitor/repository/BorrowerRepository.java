package com.bank.creditmonitor.repository;

import com.bank.creditmonitor.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowerRepository extends JpaRepository<Borrower, Long> {
    Optional<Borrower> findByBorrowerCode(String code);
    List<Borrower> findBySegment(String segment);

    @Query("SELECT b FROM Borrower b WHERE b.name LIKE %:name%")
    List<Borrower> searchByName(@Param("name") String name);
}
