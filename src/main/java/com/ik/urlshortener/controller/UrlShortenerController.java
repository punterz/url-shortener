package com.ik.urlshortener.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ik.urlshortener.service.UrlShortenerService;

import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * A Controller class that exposes HTTP endpoints for communication between 
 * the server and the outside world
 * 
 */

@RestController
public class UrlShortenerController {

    @Autowired
    private UrlShortenerService urlShortenerService;

    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerController.class);

    @Value("${url.shortener.base-url}")
    private String baseUrl;

    // Endpoint to shorten a URL
    @PostMapping("/shorten")
    public ResponseEntity<Map<String, String>> shortenUrl(@RequestBody Map<String, String> request) {
        String longUrl = request.get("longUrl");
        logger.info("Received request to shorten URL: {}", longUrl);
        try {
            String shortUrlKey = urlShortenerService.shortenUrl(longUrl);

            // Use the configured base URL
            String shortUrl = baseUrl + "/" + shortUrlKey;

            Map<String, String> response = new HashMap<>();
            response.put("shortUrl", shortUrl);

            logger.info("Shortened URL: {} to {}", longUrl, shortUrl);

            return ResponseEntity.ok(response);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error while shortening URL: {}", longUrl, e);
            return ResponseEntity.status(500).build();
        }
    }

    // Health check URL
    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> healthCheck() {
        logger.info("Health check endpoint called.");
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        
        logger.info("Health check status: {}", response.get("status"));
        return ResponseEntity.ok(response);
    }

    // Endpoint to redirect to the original URL
    @GetMapping("/{shortUrlKey}")
    public ResponseEntity<Void> redirectToLongUrl(@PathVariable String shortUrlKey) {
        logger.info("Received request to redirect short URL: {}", shortUrlKey);
        String longUrl = urlShortenerService.getLongUrl(shortUrlKey);
        if (longUrl != null) {
            logger.info("Redirecting to long URL: {}", longUrl);
            return ResponseEntity.status(302).location(URI.create(longUrl)).build();
        } else {
            logger.warn("Short URL key not found: {}", shortUrlKey);
            return ResponseEntity.notFound().build();
        }
    }
}
