package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;

import com.example.demo.model.WikiPage;
import com.example.demo.service.WikiService;

@RestController
@RequestMapping("/api/wiki")
@CrossOrigin(origins = "*")
public class CrudController {

    private final WikiService wikiService;

    public CrudController(WikiService wikiService) {
        this.wikiService = wikiService;
    }

    @PostMapping
    public WikiPage createPage(@RequestBody WikiPage wikiPage) {
        return wikiService.saveWikiPage(wikiPage);
    }

    @PutMapping("/{id}")
    public WikiPage updatePage(@PathVariable Long id, @RequestBody WikiPage wikiPage) {
        return wikiService.updateWikiPage(id, wikiPage);
    }

    @GetMapping
    public Iterable<WikiPage> getAllPages() {
        return wikiService.getAllWikiPages();
    }

    @GetMapping("/{id}")
    public WikiPage getPageById(@PathVariable Long id) {
        return wikiService.getWikiPageById(id);
    }

    @DeleteMapping("/{id}")
    public void deletePage(@PathVariable Long id) {
        wikiService.deleteWikiPage(id);
    }

    @GetMapping("/")
    public String index(WikiPage wikiPage) {
        return "index";
    }

    // @PostMapping("/fetch")
    // public WikiPage fetchWikiPage(@RequestBody FetchWikiPageRequest request) {
    // return wikiService.fetchWikiPage(request.getTitle());
    // }

    @GetMapping("/fetch")
    public WikiPage fetchWikiPage(@RequestParam String title) {
        System.out.println("GET /fetch endpoint called with title: " + title);
        return wikiService.fetchWikiPage(title);
    }
}