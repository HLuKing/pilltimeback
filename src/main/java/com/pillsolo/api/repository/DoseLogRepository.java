package com.pillsolo.api.repository;

import com.pillsolo.api.domain.DoseLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DoseLogRepository extends JpaRepository<DoseLog, Long> {

    Optional<DoseLog> findByPillIdAndDoseDate(Long pillId, LocalDate doseDate);

    int countByPillId(Long pillId);

    // ✅ 추가
    boolean existsByPillIdAndDoseDate(Long pillId, LocalDate doseDate);
    List<DoseLog> findByPillId(Long pillId);

}
