package ir.ceno.controller;

import com.rometools.rome.feed.rss.Channel;
import ir.ceno.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeedController {

    private FeedService feedService;

    @Autowired
    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping("/rss/{categoryName}")
    public Channel rss(@PathVariable String categoryName) {
        return feedService.getFeed(categoryName);
    }
}
