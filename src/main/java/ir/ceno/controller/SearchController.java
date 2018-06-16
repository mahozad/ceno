package ir.ceno.controller;

import ir.ceno.model.Post;
import ir.ceno.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controller that deals with full-text search operations.
 */
@Controller
public class SearchController {

    private SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Searches for the given query.
     *
     * @param query the query to search for
     * @param model the model to add attributes to
     * @return view name of the result page
     */
    @GetMapping("/search")
    public String search(@RequestParam("q") String query, Model model) {
        List<Post> posts = searchService.searchByQuery(query);
        model.addAttribute("query", query);
        model.addAttribute("posts", posts);
        return "search";
    }
}
