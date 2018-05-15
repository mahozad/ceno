package ir.ceno.controller;

import ir.ceno.model.Post;
import ir.ceno.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SearchController {

    private SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public String search(@RequestParam String q, Model model) {
        List<Post> posts = searchService.searchByQuery(q);
        model.addAttribute("query", q);
        model.addAttribute("posts", posts);
        return "search";
    }
}
