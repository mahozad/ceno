package ir.ceno.controller;

import com.rometools.rome.feed.rss.Channel;
import ir.ceno.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that deals with rss-related operations.
 * <p>
 * Requests to urls starting with <i>/rss</i> are dispatched here.
 * <p>
 * This class is annotated with {@link RestController} so that every method has
 * an implicit {@link ResponseBody} annotation on it.
 */
@RestController
@RequestMapping("/rss")
public class FeedController {

    private FeedService feedService;

    @Autowired
    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    /**
     * Returns the feed ({@link Channel}) for the specified category name.
     *
     * @param categoryName name of the category to get its feed
     * @return the channel containing feed items
     */
    @GetMapping("/{categoryName}")
    public Channel getFeed(@PathVariable String categoryName) {
        return feedService.getFeed(categoryName);
    }
}
