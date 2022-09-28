package br.com.sw2you.realmeet.util;

import static java.time.temporal.ChronoUnit.MILLIS;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class DateUtils {
    public static final ZoneOffset DEFAULT_TIMEZONE = ZoneOffset.of("-03:00");
    public static final String DATA_PATTERN = "dd/MM/yyyy";
    public static final String DATA_TIME_PATTERN = "dd/MM/yyyy HH:MM";

    private DateUtils() {}

    public static OffsetDateTime now() {
        return OffsetDateTime.now(DEFAULT_TIMEZONE).truncatedTo(MILLIS);
    }

    public static boolean isOverlapping(
        OffsetDateTime start1,
        OffsetDateTime end1,
        OffsetDateTime start2,
        OffsetDateTime end2
    ) {
        return start1.compareTo(end2) < 0 && end1.compareTo(start2) > 0;
    }

    public static String formatUsingDatePattern(LocalDate localDate) {
        return Objects.requireNonNull(localDate).format(DateTimeFormatter.ofPattern(DATA_PATTERN));
    }

    public static String formatUsingDateTimePattern(OffsetDateTime offsetDateTime) {
        return Objects.requireNonNull(offsetDateTime).format(DateTimeFormatter.ofPattern(DATA_TIME_PATTERN));
    }
}
