package ir.ceno.util;

import java.time.LocalDateTime;

public interface DateAndTimeFormatter {

    String format(LocalDateTime dateTime);

    String formatNow(String pattern);
}
