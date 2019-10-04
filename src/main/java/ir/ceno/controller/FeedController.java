package ir.ceno.controller;

import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.rss.Channel;
import ir.ceno.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller that deals with rss-related operations.
 * <p>
 * Requests to urls starting with <i>/rss</i> are dispatched here.
 * <p>
 * This class is annotated with {@link RestController} so that every method has
 * an implicit {@link ResponseBody} annotation on it.
 */
@RestController
@RequestMapping({"/rss", "/feed"})
public class FeedController {

    private FeedService feedService;

    @Autowired
    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    /**
     * Returns the feed ({@link Channel}) for the specified category name.
     * <p>
     * Note that when the return type of the controller is of type Channel or {@link Feed},
     * Spring automatically converts the return value into appropriate xml form.
     *
     * @param categoryName name of the category to get its feed
     * @return the channel containing feed items
     */
    @GetMapping("/{categoryName}")
    public Channel getFeed(@PathVariable String categoryName) {
        return feedService.getFeedByCategoryName(categoryName);
    }
}
