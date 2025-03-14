package org.epam.util;

import java.time.LocalDate;

public class CheckerField {
    public static boolean check(String val) {
        return val != null && !val.isEmpty() && !val.isBlank();
    }

    public static boolean check(Integer val) {
        return val != null && val > 0;
    }

    public static boolean check(LocalDate val) {
        return val != null;
    }
}
