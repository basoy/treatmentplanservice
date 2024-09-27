package com.serg.bash.treatmentplanservice.repository;

import com.serg.bash.treatmentplanservice.entity.TaskStatus;
import com.serg.bash.treatmentplanservice.entity.TreatmentTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TreatmentTaskRepository extends JpaRepository<TreatmentTask, Long> {

    List<TreatmentTask> findAllByPatientIdAndStatus(String patientId, TaskStatus status);
}
