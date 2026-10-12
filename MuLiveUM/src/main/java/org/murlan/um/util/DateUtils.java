package org.murlan.um.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public final class DateUtils {
    private DateUtils() {}

    public static LocalDateTime getLocalDateTimeNowUTC() {
        return LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
    }
}
