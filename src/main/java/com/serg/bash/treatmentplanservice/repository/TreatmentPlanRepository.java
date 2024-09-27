package com.serg.bash.treatmentplanservice.repository;

import com.serg.bash.treatmentplanservice.entity.TreatmentPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TreatmentPlanRepository extends JpaRepository<TreatmentPlan, Long> {

    List<TreatmentPlan> findAllByEndTimeIsNullOrEndTimeIsBefore(LocalDateTime now);

    default List<TreatmentPlan> findAllActivePlans() {
        return findAllByEndTimeIsNullOrEndTimeIsBefore(LocalDateTime.now());
    }
}

