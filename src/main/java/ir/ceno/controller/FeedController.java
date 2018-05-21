package ir.ceno.controller;

import com.rometools.rome.feed.rss.Channel;
import ir.ceno.util.FeedGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeedController {

    private FeedGenerator feedGenerator;

    @Autowired
    public FeedController(FeedGenerator feedGenerator) {
        this.feedGenerator = feedGenerator;
    }

    @GetMapping("/rss/{name}")
    public Channel rss(@PathVariable String name) {
        return feedGenerator.getFeed(name);
    }
}
