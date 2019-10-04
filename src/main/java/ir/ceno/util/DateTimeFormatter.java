package ir.ceno.util;

import ir.ceno.model.Post;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.time.temporal.ChronoUnit.*;

@Component
public class DateTimeFormatter {

    @SuppressWarnings("unused") // @formatter:off
    public String formatPostDataTime(Post post) {
        LocalDateTime postCreationDateTime = post.getCreationDateTime();
        Duration duration = Duration.between(postCreationDateTime, now());

        if (duration.toSeconds() < MINUTES.getDuration().getSeconds()) {
            return "seconds ago";
        } else if (duration.toMinutes() < HOURS.getDuration().toMinutes()) {
            return duration.toMinutes() + (duration.toMinutes() < 2 ? " minute" : " minutes") + " ago";
        } else if (duration.toHours() < DAYS.getDuration().toHours()) {
            return duration.toHours() + (duration.toHours() < 2 ? " hour" : " hours") + " ago";
        } else if (duration.toDays() < 3) {
            return duration.toDays() + (duration.toDays() < 2 ? " day" : " days") + " ago";
        } else if (now().getYear() == postCreationDateTime.getYear()) {
            return postCreationDateTime.format(ofPattern("d MMMM"));
        } else {
            return postCreationDateTime.format(ofPattern("d MMMM uuuu"));
        }
    }
}
