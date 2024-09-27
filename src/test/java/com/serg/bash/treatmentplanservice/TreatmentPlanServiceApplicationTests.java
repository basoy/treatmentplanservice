package com.serg.bash.treatmentplanservice;

import com.serg.bash.treatmentplanservice.entity.TaskStatus;
import com.serg.bash.treatmentplanservice.entity.TreatmentAction;
import com.serg.bash.treatmentplanservice.entity.TreatmentPlan;
import com.serg.bash.treatmentplanservice.entity.TreatmentTask;
import com.serg.bash.treatmentplanservice.repository.TreatmentPlanRepository;
import com.serg.bash.treatmentplanservice.repository.TreatmentTaskRepository;
import com.serg.bash.treatmentplanservice.service.TreatmentSchedulerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static com.serg.bash.treatmentplanservice.entity.TaskStatus.ACTIVE;
import static com.serg.bash.treatmentplanservice.entity.TreatmentAction.ACTION_A;
import static com.serg.bash.treatmentplanservice.entity.TreatmentAction.ACTION_B;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SpringBootTest
class TreatmentPlanServiceApplicationTests {

    @Autowired
    private TreatmentSchedulerService schedulerService;

    @Autowired
    private TreatmentPlanRepository treatmentPlanRepository;

    @Autowired
    private TreatmentTaskRepository treatmentTaskRepository;

    @Test
    public void createTreatmentTasksForSelectedPlan() {
        // Create a sample treatment plan
        TreatmentPlan plan = new TreatmentPlan();
        plan.setAction(TreatmentAction.ACTION_A);
        plan.setPatientId("patient123");
        plan.setStartTime(LocalDateTime.now().plusDays(1));
        plan.setEndTime(LocalDateTime.now().plusMonths(3));
        plan.setRecurrencePattern("daily at 08:00");

        // Save the plan to the repository
        TreatmentPlan savedPlan = treatmentPlanRepository.save(plan);

        // Find active plans
        List<TreatmentPlan> activePlans = schedulerService.findAllActivePlans();
        Assertions.assertEquals(1, activePlans.size());

        // Schedule tasks for the plan
        schedulerService.scheduleTreatmentTasks();

        // Check if tasks were created
        List<TreatmentTask> tasks = treatmentTaskRepository.findAll();
        Assertions.assertTrue(tasks.stream().anyMatch(task -> task.getPatientId().equals(savedPlan.getPatientId()) && task.getStatus() == ACTIVE));

        // Check plan status
        TreatmentPlan updatedPlan = treatmentPlanRepository.findById(savedPlan.getId()).orElseThrow();
        Assertions.assertEquals(ACTION_A, updatedPlan.getAction());
    }

    @Test
    public void createTreatmentTasksForAllPlans() {
        // Create sample plans
        TreatmentPlan planA = new TreatmentPlan();
        planA.setAction(TreatmentAction.ACTION_A);
        planA.setPatientId("patient123");
        planA.setStartTime(LocalDateTime.now().plusDays(1));
        planA.setEndTime(LocalDateTime.now().plusMonths(3));
        planA.setRecurrencePattern("daily at 08:00");

        TreatmentPlan planB = new TreatmentPlan();
        planB.setAction(TreatmentAction.ACTION_B);
        planB.setPatientId("patient456");
        planB.setStartTime(LocalDateTime.now().plusDays(2));
        planB.setEndTime(LocalDateTime.now().plusMonths(4));
        planB.setRecurrencePattern("daily at 16:00");

        // Save plans to repository
        TreatmentPlan savedPlanA = treatmentPlanRepository.save(planA);
        TreatmentPlan savedPlanB = treatmentPlanRepository.save(planB);

        // Find active plans
        List<TreatmentPlan> activePlans = schedulerService.findAllActivePlans();
        Assertions.assertEquals(2, activePlans.size());

        // Schedule tasks for all plans
        schedulerService.scheduleTreatmentTasks();

        // Check tasks for each plan
        List<TreatmentTask> tasksA = treatmentTaskRepository.findAllByPatientIdAndStatus(savedPlanA.getPatientId(), TaskStatus.ACTIVE);
        List<TreatmentTask> tasksB = treatmentTaskRepository.findAllByPatientIdAndStatus(savedPlanB.getPatientId(), TaskStatus.ACTIVE);

        Assertions.assertEquals(4, tasksA.size()); // Two tasks per day for 2 days
        Assertions.assertEquals(5, tasksB.size()); // Five tasks for 5 days

        // Check plan statuses
        TreatmentPlan updatedPlanA = treatmentPlanRepository.findById(savedPlanA.getId()).orElseThrow();
        TreatmentPlan updatedPlanB = treatmentPlanRepository.findById(savedPlanB.getId()).orElseThrow();

        Assertions.assertEquals(ACTION_A, updatedPlanA.getAction());
        Assertions.assertEquals(ACTION_B, updatedPlanB.getAction());
    }
}
