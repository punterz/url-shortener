package com.ik.urlshortener.controller;

import org.mockito.Mockito;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ik.urlshortener.service.UrlShortenerService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * In this test, we will mock the UrlShortenerService and test the UrlShortenerController 
 * to ensure that it handles HTTP requests correctly.
 *
 */

@WebMvcTest(UrlShortenerController.class)
public class UrlShortenerControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlShortenerService urlShortenerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${url.shortener.base-url:http://localhost:8080}")
    private String baseUrl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        logger.info("Test setup complete. MockMvc and UrlShortenerService are initialized.");
    }

    /**
     * The test case tests the service to perform URL Shortening
     * 
     */
    @Test
    public void testShortenUrl() throws Exception {
        String longUrl = "https://www.example.com";
        String shortUrlKey = "abc123";
        String expectedShortUrl = baseUrl + "/" + shortUrlKey;

        logger.info("Starting test for URL shortening with longUrl: {}", longUrl);

        // Mock service behavior
        Mockito.when(urlShortenerService.shortenUrl(longUrl)).thenReturn(shortUrlKey);

        // Create JSON request body
        String requestBody = objectMapper.writeValueAsString(Map.of("longUrl", longUrl));

        // Perform POST request and assert the response
        mockMvc.perform(MockMvcRequestBuilders.post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.shortUrl").value(expectedShortUrl));

        logger.info("Shortened URL test passed. Expected short URL: {}", expectedShortUrl);

        // Verify that the service method was called once
        Mockito.verify(urlShortenerService, Mockito.times(1)).shortenUrl(longUrl);
    }

    @Test
    public void testHealthCheck() throws Exception {
        logger.info("Starting test for healthcheck");

        // Perform a GET request to the /health endpoint
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/ping"));

        // Validate the response
        result.andExpect(MockMvcResultMatchers.status().isOk())                // Expect HTTP 200 OK
              .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("UP")); // Expect the "status" field to be "UP"
        
        logger.info("Test for healthcheck passed");
    }

    /**
     * The test case tests the service to perform correct URL redirecting with short URL
     * 
     */
    @Test
    public void testRedirectToLongUrl() throws Exception {
        String shortUrlKey = "xyz123";
        String longUrl = "https://www.example.com";

        logger.info("Starting test for URL redirect with shortUrlKey: {}", shortUrlKey);

        // Mock service behavior
        Mockito.when(urlShortenerService.getLongUrl(shortUrlKey)).thenReturn(longUrl);

        // Perform GET request and assert redirection
        mockMvc.perform(MockMvcRequestBuilders.get("/" + shortUrlKey))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.header().string("Location", longUrl));

        logger.info("Redirect test passed. Short URL key: {} redirects to long URL: {}", shortUrlKey, longUrl);

        // Verify that the service method was called once
        Mockito.verify(urlShortenerService, Mockito.times(1)).getLongUrl(shortUrlKey);
    }

    /**
     * The test case tests the service behavior if an invalid short URL is encountered 
     * 
     */
    @Test
    public void testRedirectToLongUrlNotFound() throws Exception {
        String shortUrlKey = "invalidKey";

        logger.info("Starting test for URL redirect with invalid shortUrlKey: {}", shortUrlKey);

        // Mock service behavior
        Mockito.when(urlShortenerService.getLongUrl(shortUrlKey)).thenReturn(null);

        // Perform GET request and assert 404 status
        mockMvc.perform(MockMvcRequestBuilders.get("/" + shortUrlKey))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        logger.info("Redirect test for invalid short URL key passed. Expected 404 status.");

        // Verify that the service method was called once
        Mockito.verify(urlShortenerService, Mockito.times(1)).getLongUrl(shortUrlKey);
    }
}
