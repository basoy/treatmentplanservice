package com.serg.bash.treatmentplanservice.util;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


public class RecurrencePatternParserUtils {

    private RecurrencePatternParserUtils() {
    }

    public static Stream<LocalDateTime> parse(String pattern) {
        String[] parts = pattern.split("\\s+");

        List<LocalDateTime> scheduleTimes = new ArrayList<>();

        if ("daily".equals(parts[0])) {
            int hour = Integer.parseInt(parts[1]);
            int minute = Integer.parseInt(parts[2]);

            for (int i = 0; i <= 23; i++) {
                scheduleTimes.add(LocalDateTime.of(1900, 1, 1, hour, minute));
            }
        } else if ("weekly".equals(parts[0])) {
            String dayOfWeek = parts[1];
            int hour = Integer.parseInt(parts[2]);
            int minute = Integer.parseInt(parts[3]);

            DayOfWeek dow = DayOfWeek.valueOf(dayOfWeek.toUpperCase());
            for (int week = 0; week <= 52; week++) {
                scheduleTimes.add(LocalDateTime.of(1900, 1, 1, hour, minute).plusWeeks(week * 7 + dow.getValue() - 1));
            }
        } else {
            throw new IllegalArgumentException("Unsupported recurrence pattern: " + pattern);
        }

        return scheduleTimes.stream().map(dt -> dt.truncatedTo(ChronoUnit.MINUTES));
    }
}
