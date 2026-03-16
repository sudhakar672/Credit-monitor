package com.bank.creditmonitor.repository;

import com.bank.creditmonitor.model.NpaTrend;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NpaTrendRepository extends JpaRepository<NpaTrend, Long> {
    List<NpaTrend> findAllByOrderByRecordedAtAsc();
}
