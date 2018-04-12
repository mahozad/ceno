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

    public String makeUrlOf(String base) {
        String rawUrl = String.format("%s-%s%d", base, LocalDate.now(), LocalTime.now().hashCode());
        rawUrl = rawUrl.toLowerCase();
        return URL_PATTERN.matcher(rawUrl).replaceAll("-");
    }

    public List<Post.ShareUrl> makeShareUrlsFor(String url) {
        List<Post.ShareUrl> shareUrls = new ArrayList<>();
        for (SocialWebsite socialWebsite : SocialWebsite.values()) {
            shareUrls.add(new Post.ShareUrl(socialWebsite.name(),
                    socialWebsite.getShareUrl() + url));
        }
        return shareUrls;
    }

    private enum SocialWebsite {

        FACEBOOK("http://facebook.com/sharer/sharer.php?u="),
        TWITTER("http://twitter.com/home?status="),
        TUMBLR("http://tumblr.com/widgets/share/tool?canonicalUrl="),
        PINTEREST("http://pinterest.com/pin/create/button/?shareUrl=");

        private final String shareUrl;

        SocialWebsite(String shareUrl) {
            this.shareUrl = shareUrl;
        }

        public String getShareUrl() {
            return shareUrl;
        }
    }
}
