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
public class CenoDateTimeFormatter {

    private final MessageSource messageSource;

    public CenoDateTimeFormatter(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String formatAge(LocalDateTime creationDateTime) {
        Locale locale = LocaleContextHolder.getLocale();
        Duration age = Duration.between(creationDateTime, now());

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
        } else if (creationDateTime.getYear() == now().getYear()) {
            return formatDate(creationDateTime.toLocalDate(), "d MMMM");
        } else {
            return formatDate(creationDateTime.toLocalDate(), "d MMMM uuuu");
        }
    }

    public String formatToday(String pattern) {
        return formatDate(LocalDate.now(), pattern);
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
     *
     * @return the formatted date
     */
    public String formatDate(Temporal temporal, String pattern) {
        LocalDate date = LocalDate.from(temporal);
        Locale locale = LocaleContextHolder.getLocale();
        DateTimeFormatter formatter = ofPattern(pattern).withLocale(locale).withDecimalStyle(DecimalStyle.of(locale));
        if (locale.getLanguage().equals("fa")) {
            PersianDate dateFa = PersianDate.fromGregorian(date);
            return formatter.format(dateFa);
        } else {
            return formatter.format(date);
        }
    }
}
