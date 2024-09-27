package com.serg.bash.treatmentplanservice.service;

import com.serg.bash.treatmentplanservice.entity.TaskStatus;
import com.serg.bash.treatmentplanservice.entity.TreatmentPlan;
import com.serg.bash.treatmentplanservice.entity.TreatmentTask;
import com.serg.bash.treatmentplanservice.repository.TreatmentPlanRepository;
import com.serg.bash.treatmentplanservice.repository.TreatmentTaskRepository;
import com.serg.bash.treatmentplanservice.util.RecurrencePatternParserUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
public class TreatmentSchedulerService {


    private final TreatmentPlanRepository treatmentPlanRepository;
    private final TreatmentTaskRepository treatmentTaskRepository;

    public TreatmentSchedulerService(TreatmentPlanRepository treatmentPlanRepository,
                                     TreatmentTaskRepository treatmentTaskRepository) {
        this.treatmentPlanRepository = treatmentPlanRepository;
        this.treatmentTaskRepository = treatmentTaskRepository;
    }

    @Scheduled(fixedRate = 60000)
    public void scheduleTreatmentTasks() {
        List<TreatmentPlan> activePlans = findAllActivePlans();

        for (TreatmentPlan plan : activePlans) {
            if (parseRecurrencePattern(plan.getRecurrencePattern()) != null) {
                parseRecurrencePattern(plan.getRecurrencePattern())
                        .forEach(pattern -> scheduleTask(plan, pattern));
            }
        }
    }

    public List<TreatmentPlan> findAllActivePlans() {
        return treatmentPlanRepository.findAllActivePlans();
    }

    private Stream<LocalDateTime> parseRecurrencePattern(String recurrencePattern) {
        return RecurrencePatternParserUtils.parse(recurrencePattern);
    }

    private void scheduleTask(TreatmentPlan plan, LocalDateTime scheduledTime) {
        TreatmentTask task = new TreatmentTask();
        task.setAction(plan.getAction());
        task.setPatientId(plan.getPatientId());
        task.setStartTime(scheduledTime);
        task.setStatus(TaskStatus.ACTIVE);

        treatmentTaskRepository.save(task);
    }
}
