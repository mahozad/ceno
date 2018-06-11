package ir.ceno.service;

import com.rometools.rome.feed.rss.Category;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Item;
import ir.ceno.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FeedService {

    // FIXME: feed and item dates are not correct

    @Value("${feed-max-items}")
    private int feedMaxSize;
    private CategoryService categoryService;

    @Autowired
    public FeedService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * @param categoryName name of the category (i.e. feed name)
     * @return channel of the feed
     */
    public Channel getFeed(String categoryName) {
        List<Item> items = new ArrayList<>();
        Slice<Post> posts = categoryService.getPosts(categoryName, 0, feedMaxSize);
        for (Post post : posts) {
            items.add(makeItemFrom(post));
        }
        return buildChannel(categoryName, items);
    }

    private Channel buildChannel(String categoryName, List<Item> items) {
        Channel channel = new Channel("rss_2.0");
        channel.setItems(items);
        channel.setTitle("Ceno " + categoryName + " posts");
        channel.setDescription("Latest " + categoryName + " posts");
        channel.setLink("http://www.ceno.ir/categories/" + categoryName);
        channel.setLanguage("en");
        return channel;
    }

    private Item makeItemFrom(Post post) {
        Item item = new Item();
        item.setTitle(post.getTitle());
        item.setAuthor(post.getAuthor().getName());
        item.setLink("http://www.ceno.ir/posts/" + post.getUrl());
        item.setCategories(makeCategoriesOf(post));
        item.setDescription(makeDescriptionOf(post));
        LocalDateTime creationDateTime = post.getCreationDateTime();
        item.setPubDate(Date.from(creationDateTime.atZone(ZoneId.of("Iran")).toInstant()));
        return item;
    }

    private List<Category> makeCategoriesOf(Post post) {
        List<Category> categories = new ArrayList<>();
        for (ir.ceno.model.Category cat : post.getCategories()) {
            Category category = new Category();
            category.setValue(cat.getName());
            categories.add(category);
        }
        return categories;
    }

    private Description makeDescriptionOf(Post post) {
        Description description = new Description();
        description.setValue(post.getSummary());
        return description;
    }
}
