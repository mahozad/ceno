package ir.ceno.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeEvent implements Event {

    private final String authorUsername;
    private final String message;

}
