package ir.ceno.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.regex.Pattern;

/**
 * Utility class that makes url for the given character sequence.
 */
@Component
public class UrlMaker {

    /**
     * Compiled pattern to avoid using {@link String} methods like <i>replaceAll</i>
     * that compile the regex in every invocation of them.
     */
    private static final Pattern URL_PATTERN = Pattern.compile("[^a-zA-Z0-9ا-ی۰-۹$\\-_.+!*'()]");

    /**
     * Makes url for the given {@link CharSequence}.
     * <p>
     * Concatenates current date and hash of current time to the base (to ensure same base in the
     * same date doesn't produce the same url) and then replaces illegal characters with "-".
     *
     * @param base character sequence to make url from
     * @return url made from the base
     */
    public String makeUrlOf(CharSequence base) {
        String rawUrl = String.format("%s-%s%d", base, LocalDate.now(), LocalTime.now().hashCode());
        rawUrl = rawUrl.toLowerCase();
        return URL_PATTERN.matcher(rawUrl).replaceAll("-");
    }
}
