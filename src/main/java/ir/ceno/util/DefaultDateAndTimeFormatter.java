package ir.ceno.util;

import com.github.mfathi91.time.PersianDate;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DecimalStyle;
import java.time.temporal.Temporal;
import java.util.Locale;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.time.temporal.ChronoUnit.*;

@Component("dateTimeFormatter")
public class DefaultDateAndTimeFormatter implements DateAndTimeFormatter {

    private final MessageSource messageSource;

    public DefaultDateAndTimeFormatter(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String format(LocalDateTime dateTime) {
        Locale locale = LocaleContextHolder.getLocale();
        Duration age = Duration.between(dateTime, now());

        if (age.toSeconds() < MINUTES.getDuration().getSeconds()) {
            return messageSource.getMessage("age.seconds-old", null, locale);
        } else if (age.toMinutes() < HOURS.getDuration().toMinutes()) {
            Object[] args = {age.toMinutes()};
            return messageSource.getMessage("age.minutes-old", args, locale);
        } else if (age.toHours() < DAYS.getDuration().toHours()) {
            Object[] args = {age.toHours()};
            return messageSource.getMessage("age.hours-old", args, locale);
        } else if (age.toDays() < 3) {
            Object[] args = {age.toDays()};
            return messageSource.getMessage("age.days-old", args, locale);
        } else if (dateTime.getYear() == now().getYear()) {
            return formatTemporal(dateTime, "d MMMM");
        } else {
            return formatTemporal(dateTime, "d MMMM uuuu");
        }
    }

    public String formatNow(String pattern) {
        return formatTemporal(LocalDateTime.now(), pattern);
    }

    /**
     * Formats the given date according to the specified pattern and
     * with respect to the user locale and its calendar system.
     * <p>
     * Unlike {@link DateFormat}, the {@link DateTimeFormatter} by default
     * does not take into account the number system of the given locale.
     * So the number system should be explicitly specified either with
     * <ul>
     *   <li>
     *     specifying number system extension ({@code fa-u-nu-arabext}) in lang parameter in HTML
     *     or with {@code .localizedBy(Locale.forLanguageTag("fa-u-nu-arabext"))}
     *   </li>
     * </ul>
     * or with
     * <ul>
     *   <li>
     *     specifying explicitly to the formatter that it should use the decimal style of this locale:
     *     {@code .withLocale(locale).withDecimalStyle(DecimalStyle.of(locale))}
     *   </li>
     * </ul>
     *
     * For more info refer
     * <a href="https://bugs.java.com/bugdatabase/view_bug.do?bug_id=JDK-8243162">here</a> and
     * <a href="https://docs.oracle.com/javase/tutorial/i18n/locale/extensions.html">here</a>.
     * <p>
     * As of JDK 15 the bug is resolved. Just use {@link DateTimeFormatter#localizedBy(Locale)}.
     * See <a href="https://github.com/openjdk/jdk/commit/ed4bc1bf237c3875174d559f8c5a05d50cc88bf2">this</a> and
     * <a href="https://jdk.java.net/15/release-notes#JDK-8244245">this</a>.
     *
     * @return the formatted date
     */
    private String formatTemporal(Temporal temporal, String pattern) {
        LocalDate date = LocalDate.from(temporal);
        Locale locale = LocaleContextHolder.getLocale();
        // FIXME: As of JDK 15 just call localizedBy(); specifying the decimal style won't be necessary.
        DateTimeFormatter formatter = ofPattern(pattern).localizedBy(locale).withDecimalStyle(DecimalStyle.of(locale));
        if (locale.getLanguage().equals("fa")) {
            PersianDate dateFa = PersianDate.fromGregorian(date);
            return formatter.format(dateFa);
        } else {
            return formatter.format(date);
        }
    }
}
