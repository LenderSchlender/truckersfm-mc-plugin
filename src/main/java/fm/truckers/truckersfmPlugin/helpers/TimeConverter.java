package fm.truckers.truckersfmPlugin.helpers;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeConverter {
    public static String convertTimestampToTime(long timestamp) {
        return Instant.ofEpochSecond(timestamp)
                .atZone(ZoneId.of("GMT"))
                .format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}
