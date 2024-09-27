package com.serg.bash.treatmentplanservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TreatmentPlanServiceApplication {


    public static void main(String[] args) {
        SpringApplication.run(TreatmentPlanServiceApplication.class, args);
    }

}
