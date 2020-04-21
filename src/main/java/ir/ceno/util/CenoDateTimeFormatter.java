package ir.ceno.util;

import com.github.mfathi91.time.PersianDate;
import ir.ceno.model.Post;
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

    public String formatPostDataTime(Post post) {
        LocalDateTime postCreationDateTime = post.getCreationDateTime();
        Duration age = Duration.between(postCreationDateTime, now());

        if (age.toSeconds() < MINUTES.getDuration().getSeconds()) {
            return "seconds ago";
        } else if (age.toMinutes() < HOURS.getDuration().toMinutes()) {
            return age.toMinutes() + (age.toMinutes() < 2 ? " minute" : " minutes") + " ago";
        } else if (age.toHours() < DAYS.getDuration().toHours()) {
            return age.toHours() + (age.toHours() < 2 ? " hour" : " hours") + " ago";
        } else if (age.toDays() < 3) {
            return age.toDays() + (age.toDays() < 2 ? " day" : " days") + " ago";
        } else if (now().getYear() == postCreationDateTime.getYear()) {
            return postCreationDateTime.format(ofPattern("d MMMM"));
        } else {
            return postCreationDateTime.format(ofPattern("d MMMM uuuu"));
        }
    }

    public String formatToday(String pattern) {
        return formatDate(LocalDate.now(), pattern);
    }

    /**
     * Unlike {@link DateFormat}, the {@link DateTimeFormatter}
     * does not take into account the number system by default.
     * So the number system should be explicitly specified through
     * {@link DateTimeFormatter#localizedBy(Locale)} method like {@code "fa-u-nu-arabext"}.
     * For more info about this
     * <a href="https://bugs.java.com/bugdatabase/view_bug.do?bug_id=JDK-8243162">refer here</a>.
     *
     * @return the current year formatted according to the locale and its calendar system
     */
    public String formatDate(LocalDate date, String pattern) {
        Locale locale = LocaleContextHolder.getLocale();
        DateTimeFormatter formatter = ofPattern(pattern);
        if (locale.getLanguage().equals("fa")) {
            Locale localeFa = Locale.forLanguageTag("fa-u-nu-arabext");
            PersianDate dateFa = PersianDate.fromGregorian(date);
            return formatter.localizedBy(localeFa).format(dateFa);
        } else {
            return formatter.format(date);
        }
    }
}
