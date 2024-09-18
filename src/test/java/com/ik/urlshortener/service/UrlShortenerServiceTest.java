package com.ik.urlshortener.service;

import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import org.junit.jupiter.api.Assertions;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.ik.urlshortener.model.UrlMapping;
import com.ik.urlshortener.repository.UrlMappingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * In this Test we will mock the UrlMappingRepository and test the business logic in UrlShortenerService
 * 
 */

@SpringBootTest
public class UrlShortenerServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerServiceTest.class);

    @Mock
    private UrlMappingRepository urlMappingRepository;

    @InjectMocks
    private UrlShortenerService urlShortenerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        logger.info("Test setup complete. UrlMappingRepository and UrlShortenerService are initialized.");
    }

    /**
     * The test case tests the logic for shortening the URL
     * 
     */
    @Test
    public void testShortenUrl() throws NoSuchAlgorithmException {
        String longUrl = "https://www.example.com";
        String expectedShortUrlKey = "e149be";

        logger.info("Starting test for URL shortening with longUrl: {}", longUrl);

        // Mocking repository save
        Mockito.when(urlMappingRepository.save(ArgumentMatchers.any(UrlMapping.class))).thenReturn(new UrlMapping());

        // Calling the service method
        String actualShortUrlKey = urlShortenerService.shortenUrl(longUrl);

        // Verify
        Assertions.assertEquals(expectedShortUrlKey, actualShortUrlKey);
        logger.info("Shortened URL test passed. Expected short URL key: {}, Actual short URL key: {}", expectedShortUrlKey, actualShortUrlKey);

        Mockito.verify(urlMappingRepository, Mockito.times(1)).save(ArgumentMatchers.any(UrlMapping.class));
    }

    /**
     * The test case tests the logic for handling collision while shortening the URL
     * 
     */
    @Test
    public void testCollisionWhileShorteningURL() throws NoSuchAlgorithmException {
        String shortUrlKey = "3f94b0";
        String longUrl = "https://www.example.com";

        logger.info("Starting test for URL shortening with collision. Existing short URL key: {}", shortUrlKey);

        // Mocking repository find
        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setId(shortUrlKey);
        urlMapping.setLongUrl(longUrl);
        Mockito.when(urlMappingRepository.findById(shortUrlKey)).thenReturn(Optional.of(urlMapping));

        String longUrlWithCollision = "https://www.example.com/abc";
        String expectedShortUrlKeyAfterCollision = "7eebb0";

        // Mocking repository save
        Mockito.when(urlMappingRepository.save(ArgumentMatchers.any(UrlMapping.class))).thenReturn(new UrlMapping());

        // Calling the service method
        String actualShortUrlKey = urlShortenerService.shortenUrl(longUrlWithCollision);

        // Verify
        Assertions.assertEquals(expectedShortUrlKeyAfterCollision, actualShortUrlKey);
        logger.info("Collision handling test passed. Expected short URL key after collision: {}, Actual short URL key: {}", expectedShortUrlKeyAfterCollision, actualShortUrlKey);

        Mockito.verify(urlMappingRepository, Mockito.times(1)).save(ArgumentMatchers.any(UrlMapping.class));
    }

    /**
     * The test case tests the logic for converting short URL to the actual URL
     * 
     */
    @Test
    public void testGetLongUrl() {
        String shortUrlKey = "e149be";
        String longUrl = "https://www.example.com";

        logger.info("Starting test for converting short URL key to long URL. Short URL key: {}", shortUrlKey);

        // Mocking repository find
        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setId(shortUrlKey);
        urlMapping.setLongUrl(longUrl);
        Mockito.when(urlMappingRepository.findById(shortUrlKey)).thenReturn(Optional.of(urlMapping));

        // Calling the service method
        String actualLongUrl = urlShortenerService.getLongUrl(shortUrlKey);

        // Verify
        Assertions.assertEquals(longUrl, actualLongUrl);
        logger.info("URL retrieval test passed. Expected long URL: {}, Actual long URL: {}", longUrl, actualLongUrl);

        Mockito.verify(urlMappingRepository, Mockito.times(1)).findById(shortUrlKey);
    }

    /**
     * The test case tests the logic while the short URL supplied is invalid or not generated by the service
     * 
     */
    @Test
    public void testGetLongUrlNotFound() {
        String shortUrlKey = "invalidKey";

        logger.info("Starting test for retrieving long URL with invalid short URL key: {}", shortUrlKey);

        // Mocking repository find with empty result
        Mockito.when(urlMappingRepository.findById(shortUrlKey)).thenReturn(Optional.empty());

        // Calling the service method
        String actualLongUrl = urlShortenerService.getLongUrl(shortUrlKey);

        // Verify
        Assertions.assertNull(actualLongUrl);
        logger.info("URL retrieval test for invalid key passed. Expected null long URL.");

        Mockito.verify(urlMappingRepository, Mockito.times(1)).findById(shortUrlKey);
    }
}
