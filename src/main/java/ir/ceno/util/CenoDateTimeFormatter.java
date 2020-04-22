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
     * Unlike {@link DateFormat}, the {@link DateTimeFormatter}
     * does not take into account the number system by default.
     * So the number system should be explicitly specified in the locale like
     * {@code "fa-u-nu-arabext"} as we did in out lang parameter in HTML or with
     * {@code Locale.forLanguageTag("fa-u-nu-arabext")}. For more info about this refer
     * <a href="https://bugs.java.com/bugdatabase/view_bug.do?bug_id=JDK-8243162">here</a> and
     * <a href="https://docs.oracle.com/javase/tutorial/i18n/locale/extensions.html">here</a>.
     *
     * @return the current year formatted according to the locale and its calendar system
     */
    public String formatDate(LocalDate date, String pattern) {
        Locale locale = LocaleContextHolder.getLocale();
        DateTimeFormatter formatter = ofPattern(pattern).localizedBy(locale);
        if (locale.getLanguage().equals("fa")) {
            PersianDate dateFa = PersianDate.fromGregorian(date);
            return formatter.format(dateFa);
        } else {
            return formatter.format(date);
        }
    }
}
