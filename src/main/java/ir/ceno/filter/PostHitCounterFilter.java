package ir.ceno.filter;

import ir.ceno.service.PostService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletComponentScan;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.regex.Pattern;

import static java.lang.Long.parseLong;
import static java.util.regex.Pattern.compile;
import static org.springframework.web.context.support.SpringBeanAutowiringSupport.processInjectionBasedOnServletContext;

/**
 * {@link WebFilter @WebFilter} annotation only works with embedded web server.
 * To use alternative approaches see
 * <a href="https://stackoverflow.com/questions/19825946/how-to-add-a-filter-class-in-spring-boot">
 * this stack overflow post
 * </a>
 * and don't forget to remove the {@link ServletComponentScan @ServletComponentScan} annotation
 * from the main class.
 */
@WebFilter("/posts/*")
@NoArgsConstructor
public class PostHitCounterFilter implements Filter {

    private static final Pattern POSTS_URI_PATTERN = compile("/posts/\\d+/.+");

    private PostService postService;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    public PostHitCounterFilter(PostService postService) {
        this.postService = postService;
    }

    /**
     * We cannot inject the PostService because the filter is not managed by
     * Spring (but by servlet container), so here we manually sut up the @Autowire to work.
     *
     * @param filterConfig {@inheritDoc}
     */
    @Override
    public void init(FilterConfig filterConfig) {
        // TODO: for a better approach to autowire that is easier to unit test see
        //  https://stackoverflow.com/q/6725234/217324
        processInjectionBasedOnServletContext(this, filterConfig.getServletContext());
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String uri = ((HttpServletRequest) req).getRequestURI();
        if (POSTS_URI_PATTERN.matcher(uri).matches()) {
            long postId = parseLong(uri.split("/")[2]);
            postService.addVisitorIp(postId, req.getRemoteAddr());
        }

        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
        // Destruction code
    }
}
