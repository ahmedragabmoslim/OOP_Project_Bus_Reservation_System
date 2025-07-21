package busbooking;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Utils {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            System.err.println("Utils: Invalid date format for string '" + dateStr + "'. Expected yyyy-MM-dd. Error: " + e.getMessage());
            return null;
        }
    }

    public static String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        try {
            return date.format(DATE_FORMATTER);
        } catch (Exception e) {
            System.err.println("Utils: Error formatting date: " + date + ". Error: " + e.getMessage());
            return "Invalid Date";
        }
    }

    public static LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalTime.parse(timeStr.trim(), TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            System.err.println("Utils: Invalid time format for string '" + timeStr + "'. Expected HH:mm. Error: " + e.getMessage());
            return null;
        }
    }

    public static String formatTime(LocalTime time) {
        if (time == null) {
            return "";
        }
        try {
            return time.format(TIME_FORMATTER);
        } catch (Exception e) {
            System.err.println("Utils: Error formatting time: " + time + ". Error: " + e.getMessage());
            return "Invalid Time";
        }
    }
}