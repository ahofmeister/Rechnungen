package de.alexanderhofmeister.rechnungen.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static String formatToDisplayDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        DateTimeFormatter germanDatePattnern = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return date.format(germanDatePattnern);
    }


}
