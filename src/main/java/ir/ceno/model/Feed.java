package ir.ceno.model;

import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Item;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * An object encapsulating a {@link Channel}, its {@link Item}s and some other related attributes.
 */
@Getter
@Setter
public class Feed {

    private Channel channel;
    private List<Item> items;
    private String name;
    private int index;

    public Feed(String categoryName) {
        this.name = categoryName.toLowerCase();
        this.channel = new Channel("rss_2.0");
        this.items = new ArrayList<>();
        this.channel.setItems(items);
        this.channel.setTitle("Ceno " + name + " posts feed");
        this.channel.setDescription("Latest " + name + " posts");
        this.channel.setLink("/categories/" + name);
        this.channel.setLanguage("en");
    }
}
