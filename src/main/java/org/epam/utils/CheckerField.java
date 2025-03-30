package org.epam.utils;


import org.epam.models.enums.TrainingTypeName;

import java.time.LocalDate;
import java.util.List;

public class CheckerField {
    public static boolean check(String val) {
        return val != null && !val.trim().isEmpty() && !val.isBlank() && !val.equals(" ");
    }

    public static boolean check(Integer val) {
        return val != null && val > 0;
    }

    public static boolean check(LocalDate val) {
        return val != null;
    }

    public static boolean check(Boolean isActive) {
        return isActive != null;
    }

    public static boolean check(TrainingTypeName trainingTypeName) {
        return trainingTypeName != null;
    }

    public static boolean check(Long duration) {
        return duration != null;
    }

    public static boolean check(List<?> list) {
        return list != null && !list.isEmpty();
    }
}
