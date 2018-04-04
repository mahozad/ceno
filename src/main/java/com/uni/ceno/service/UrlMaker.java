package com.uni.ceno.service;

import com.uni.ceno.model.Post;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class UrlMaker {

    private static final Pattern URL_PATTERN = Pattern.compile("[^0-9a-zA-Z$\\-_.+!*'()]");
    private static final String FACEBOOK_SHR = "http://facebook.com/sharer/sharer.php?u=";
    private static final String TWITTER_SHR = "http://twitter.com/home?status=";
    private static final String TUMBLR_SHR = "http://tumblr.com/widgets/share/tool?canonicalUrl=";
    private static final String PINTEREST_SHR = "https://pinterest.com/pin/create/button/?url=";

    public String makeUrlOf(String base) {
        String rawUrl = base + LocalDate.now() + LocalTime.now().hashCode();
        return URL_PATTERN.matcher(rawUrl).replaceAll("-");
    }

    public List<Post.ShareUrl> makeShareUrlsFor(String postUrl) {
        List<Post.ShareUrl> shareUrls = new ArrayList<>();
        shareUrls.add(new Post.ShareUrl("facebook", FACEBOOK_SHR + postUrl));
        shareUrls.add(new Post.ShareUrl("twitter", TWITTER_SHR + postUrl));
        shareUrls.add(new Post.ShareUrl("pinterest", PINTEREST_SHR + postUrl));
        shareUrls.add(new Post.ShareUrl("tumblr", TUMBLR_SHR + postUrl));
        return shareUrls;
    }
}
