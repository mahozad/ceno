package ir.ceno.service;

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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
public class FeedService {

    // FIXME: feed and item dates are not correct

    @Value("${feed-max-items}")
    private int feedMaxItems;
    private Map<String, Feed> feeds = new HashMap<>();
    private PostRepository postRepository;

    @Autowired
    public FeedService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /**
     * Initializes feed for the main categories with {@link #feedMaxItems} latest posts.
     */
    public void initialize() {
        for (HomepageCategory category : HomepageCategory.values()) {
            Feed feed = new Feed(category.name());
            Slice<Post> posts = getPostsOf(category);
            for (Post post : posts) {
                Item item = makeItem(post);
                feed.getItems().add(item);
                feed.setIndex((feed.getIndex() + 1) % feedMaxItems);
                feed.getChannel().setLastBuildDate(new Date());
            }
            feeds.put(feed.getName(), feed);
        }
    }

    private Slice<Post> getPostsOf(HomepageCategory category) {
        Sort sort = Sort.by(DESC, "creationDateTime");
        PageRequest pageRequest = PageRequest.of(0, feedMaxItems, sort);
        return postRepository.findByCategoriesName(category.name(), pageRequest);
    }

    /**
     * Gets the feed from the {@link #feeds} if it exists and returns its channel,
     * otherwise makes a temporary feed and returns its empty channel.
     *
     * @param feedName name of the feed (category name)
     * @return channel of the feed
     */
    public Channel getFeed(String feedName) {
        return feeds.getOrDefault(feedName, new Feed(feedName)).getChannel();
    }

    void addItem(Post post) {
        Item item = makeItem(post);
        for (ir.ceno.model.Category category : post.getCategories()) {
            Feed feed = feeds.getOrDefault(category.getName(), new Feed(category.getName()));
            feed.getItems().add(item);
            feed.setIndex((feed.getIndex() + 1) % feedMaxItems);
            feed.getChannel().setLastBuildDate(new Date());
        }
    }

    private Item makeItem(Post post) {
        Item item = new Item();
        item.setTitle(post.getTitle());
        item.setAuthor(post.getAuthor().getName());
        item.setLink("http://www.ceno.ir/posts/" + post.getUrl());
        item.setCategories(extractCategoriesOf(post));
        Description description = new Description();
        description.setValue(post.getSummary());
        item.setDescription(description);
        LocalDateTime creationDateTime = post.getCreationDateTime();
        Date publishDate = Date.from(creationDateTime.atZone(ZoneId.of("Iran")).toInstant());
        item.setPubDate(publishDate);
        return item;
    }

    private List<Category> extractCategoriesOf(Post post) {
        List<Category> categories = new ArrayList<>();
        for (ir.ceno.model.Category cat : post.getCategories()) {
            Category category = new Category();
            category.setValue(cat.getName());
            categories.add(category);
        }
        return categories;
    }
}
