package ir.ceno.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.regex.Pattern;

@Component
public class UrlMaker {

    private static final Pattern URL_PATTERN = Pattern.compile("[^0-9a-zA-Z$\\-_.+!*'()]");

    public String makeUrlOf(String base) {
        String rawUrl = String.format("%s-%s%d", base, LocalDate.now(), LocalTime.now().hashCode());
        rawUrl = rawUrl.toLowerCase();
        return URL_PATTERN.matcher(rawUrl).replaceAll("-");
    }
}
