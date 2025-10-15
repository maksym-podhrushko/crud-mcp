package com.example.demo.service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.controller.EventController;
import com.example.demo.model.WikiPage;
import com.example.demo.repository.WikiRepository;

/**
 * Service class for business logic related to wiki pages.
 * <p>
 * Handles creation, retrieval, updating, and deletion of wiki pages.
 * </p>
 *
 */

@Service
public class WikiService {

    private final String WIKIPEDIA_URL_API = "https://en.wikipedia.org/w/api.php";

    @Autowired
    private WikiRepository wikiRepository;

    @Autowired
    private EventController eventController;

    public Iterable<WikiPage> getAllWikiPages() {
        return wikiRepository.findAll();
    }

    public WikiPage getWikiPageById(Long id) {
        return wikiRepository.findById(id).orElse(null);
    }

    public void deleteWikiPage(Long id) {
        wikiRepository.deleteById(id);
        eventController.notifyWiki(id);
    }

    public WikiPage saveWikiPage(WikiPage wikiPage) {
        WikiPage savedPage = wikiRepository.save(wikiPage);

        // Notify after successful save
        eventController.notifyWiki(savedPage.getId());
        return savedPage;
    }

    public WikiPage updateWikiPage(Long id, WikiPage wikiPage) {
        // Check if the page exists
        WikiPage existingPage = wikiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("WikiPage with id " + id + " not found"));

        // Update the fields (you might want to be more selective about which fields to
        // update)
        existingPage.setTitle(wikiPage.getTitle());
        existingPage.setContent(wikiPage.getContent());

        String newImageUrl = (wikiPage.getImageUrl() != null) ? wikiPage.getImageUrl() : existingPage.getImageUrl();
        existingPage.setImageUrl(newImageUrl);

        // Save the updated page
        WikiPage savedPage = wikiRepository.save(existingPage);

        // Notify after successful save
        eventController.notifyWiki(id);

        return savedPage;
    }

    public WikiPage fetchWikiPage(String title) {
        String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);

        System.out.println("Fetching wiki page with title: " + title);
        System.out.println("Encoded title: " + encodedTitle);

        // Updated API URL to include both extracts and pageimages (pipe character URL
        // encoded)
        String apiUrl = WIKIPEDIA_URL_API + "?action=query&titles=" + encodedTitle
                + "&prop=extracts%7Cpageimages&explaintext=1&pithumbsize=300&format=json";
        System.out.println("Full API URL: " + apiUrl);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest httpGet = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("User-Agent", "crud-ai-wiki/1.0 (maksym.podhrushko@swisscom.com)")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(httpGet, BodyHandlers.ofString());
            String responseBody = response.body();
            System.out.println("Response status: " + response.statusCode());

            // Check response length before printing full body
            if (responseBody != null) {
                System.out.println("Response length: " + responseBody.length());
                // Print just the beginning of the response if it's very large
                System.out.println("Response preview: " +
                        (responseBody.length() > 500 ? responseBody.substring(0, 500) + "..." : responseBody));
            } else {
                System.out.println("Response body is null");
            }

            // More detailed error checking
            if (responseBody == null || responseBody.isEmpty()) {
                System.out.println("Empty response from Wikipedia API");
                return null;
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);

            // Example: extract the page content (customize as needed)
            JsonNode pages = root.path("query").path("pages");
            if (pages.elements().hasNext()) {
                JsonNode page = pages.elements().next();
                // Check if this is a "missing" page
                if (page.has("missing")) {
                    System.out.println("Wikipedia API indicates this is a missing page");
                    return null;
                }

                String pageTitle = page.path("title").asText();
                String pageId = page.path("pageid").asText("unknown");
                System.out.println("Found page: ID=" + pageId + ", Title=" + pageTitle);

                String extract = page.path("extract").asText();
                if (extract == null || extract.isEmpty()) {
                    System.out.println("No extract found in the response");
                    return null;
                }

                // Extract image URL if available
                String imageUrl = null;
                if (page.has("thumbnail")) {
                    imageUrl = page.path("thumbnail").path("source").asText();
                    System.out.println("Found thumbnail: " + imageUrl);
                } else {
                    System.out.println("No thumbnail found for this page");
                }

                WikiPage wikiPage = new WikiPage();
                wikiPage.setTitle(pageTitle);
                wikiPage.setContent(extract);
                wikiPage.setImageUrl(imageUrl);
                wikiRepository.save(wikiPage);
                System.out.println("Successfully saved wiki page with title: " + pageTitle + " and image: " + imageUrl);

                WikiPage savedPage = saveWikiPage(wikiPage);
                eventController.notifyWiki(savedPage.getId());

                return wikiPage;
            } else {
                System.out.println("No pages found in the response");
            }

        } catch (Exception e) {
            System.out.println("Exception while fetching wiki page: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
