package ir.ceno.service;

import com.rometools.rome.feed.rss.Category;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Item;
import ir.ceno.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FeedService {

    private static final int FEED_MAX_SIZE = 10;
    private CategoryService categoryService;

    @Autowired
    public FeedService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * @param categoryName name of the category (i.e. feed name)
     * @return channel of the feed
     */
    @Cacheable(cacheNames = "feeds")
    public Channel getFeedByCategoryName(String categoryName) {
        List<Item> items = new ArrayList<>();
        Slice<Post> posts = categoryService.getPosts(categoryName, 0, FEED_MAX_SIZE);
        for (Post post : posts) {
            Item item = makeItemFrom(post);
            items.add(item);
        }
        return buildChannel(categoryName, items);
    }

    private Channel buildChannel(String channelName, List<Item> items) {
        Channel channel = new Channel("rss_2.0");
        channel.setItems(items);
        channel.setTitle("Ceno " + channelName + " posts");
        channel.setDescription("Latest " + channelName + " posts");
        channel.setLink("http://www.ceno.ir/categories/" + channelName);
        channel.setLanguage("en");
        channel.setLastBuildDate(items.size() == 0 ? new Date() : items.get(0).getPubDate());
        return channel;
    }

    private Item makeItemFrom(Post post) {
        Item item = new Item();
        item.setTitle(post.getTitle());
        item.setAuthor(post.getAuthor().getUsername());
        item.setLink("http://www.ceno.ir/posts/" + post.getUrl());
        item.setCategories(makeCategoriesOf(post));
        item.setDescription(makeDescriptionOf(post));
        LocalDateTime creationDateTime = post.getCreationDateTime();
        item.setPubDate(Date.from(creationDateTime.atZone(ZoneId.systemDefault()).toInstant()));
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
