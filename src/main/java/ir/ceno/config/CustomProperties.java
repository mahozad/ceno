package ir.ceno.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
@Getter
@Setter
@SuppressWarnings("unused")
public class CustomProperties {

    private String usersQuery = "select name, password, true from user where name=?";
    private int likeScore = 20;
    private int hotMin = 10;
    private int topPostsSize = 3;
    private int topCategoryPostsSize = 6;
    private int pinnedPostsSize = 10;
    private int categorySliceSize = 9;
    private int searchMaxResults = 10;
    private int searchMaxSimilars = 5;
    private int userMinEditScore = 100;
    private int feedMaxItems = 10;
}
