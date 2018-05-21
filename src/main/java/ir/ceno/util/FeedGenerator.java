package ir.ceno.util;

import com.rometools.rome.feed.rss.Category;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Item;
import ir.ceno.model.Category.HomepageCategory;
import ir.ceno.model.Feed;
import ir.ceno.model.Post;
import ir.ceno.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Component
public class FeedGenerator {

    // FIXME: it may be better to place this class in another package like support or...
    // FIXME: feeds contain duplicate items

    @Value("${feed.max-items}")
    private int maxItems = 10;

    private Map<String, Feed> feeds = new HashMap<>();
    private PostRepository postRepository;

    @Autowired
    public FeedGenerator(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void initialize() {
        for (HomepageCategory category : HomepageCategory.values()) {
            Feed feed = new Feed(category.name());
            feeds.put(feed.getName(), feed);
        }
        for (HomepageCategory category : HomepageCategory.values()) {
            Sort sort = Sort.by(Sort.Direction.DESC, "creationDateTime");
            PageRequest pageRequest = PageRequest.of(0, 10, sort);
            Slice<Post> posts = postRepository.findByCategoriesName(category.name(), pageRequest);
            posts.forEach(this::addItem);
        }
    }

    public Channel getFeed(String feedName) {
        return feeds.get(feedName).getChannel();
    }

    public void addItem(Post post) {
        Item item = makeItem(post);

        List<Category> categories = makeCategories(post);
        item.setCategories(categories);

        Description description = new Description();
        description.setValue(post.getSummary());
        item.setDescription(description);

        LocalDateTime creationDateTime = post.getCreationDateTime();
        Date publishDate = Date.from(creationDateTime.atZone(ZoneId.systemDefault()).toInstant());
        item.setPubDate(publishDate);

        for (ir.ceno.model.Category category : post.getCategories()) {
            Feed feed = feeds.get(category.getName());
            feed.getItems().add(item);
            feed.setIndex((feed.getIndex() + 1) % maxItems);
            feed.getChannel().setLastBuildDate(new Date()); // or use the item date
        }
    }

    private List<Category> makeCategories(Post post) {
        List<Category> categories = new ArrayList<>();
        for (ir.ceno.model.Category cat : post.getCategories()) {
            Category category = new Category();
            category.setValue(cat.getName());
            categories.add(category);
        }
        return categories;
    }

    private Item makeItem(Post post) {
        Item item = new Item();
        item.setAuthor(post.getAuthor().getName());
        item.setLink(post.getUrl());
        item.setTitle(post.getTitle());
        return item;
    }
}
