package com.ik.urlshortener.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ik.urlshortener.model.UrlMapping;
import com.ik.urlshortener.repository.UrlMappingRepository;

import java.util.Optional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This Spring bean will be responsible for all the logic needed for
 *  - shortening of the URL
 *  - detecting collision and resolving while shortening
 *  - retrieval of correct URL when short URL is supplied
 *  - identify if the supplied URL is invalid or not generated through the app
 * 
 */

@Service
public class UrlShortenerService {

    @Autowired
    private UrlMappingRepository urlMappingRepository;

    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerService.class);

    /**
     * This is the method responsible for shortening the URL. It ensures that the short URL is unique
     * and handles any collisions that may happen gracefully.
     * 
     * @param longUrl
     * @return
     * @throws NoSuchAlgorithmException
     */

    public String shortenUrl(String longUrl) throws NoSuchAlgorithmException {
        logger.info("Attempting to shorten URL: {}", longUrl);
        
        // Generate a hash of the URL
        String shortUrlKey = getShortUrlKey(longUrl);

        int attempts = 0;
        final int maxAttempts = 5;  // Define a max number of attempts to handle collisions

        // Loop to resolve collisions
        while (attempts < maxAttempts) {
            logger.debug("Checking for collision with short URL key: {}", shortUrlKey);
            Optional<UrlMapping> existingMapping = urlMappingRepository.findById(shortUrlKey);

            if (existingMapping.isPresent()) {
                // Check if it maps to the same URL
                if (existingMapping.get().getLongUrl().equals(longUrl)) {
                    logger.info("Short URL already exists for the same long URL: {}", longUrl);
                    return shortUrlKey;
                } else {
                    // If it's a different URL, resolve the collision by modifying the longUrl (or key)
                    logger.warn("Collision detected: short URL key {} already exists but maps to a different long URL", shortUrlKey);
                    attempts++;
                    shortUrlKey = getShortUrlKey(longUrl + attempts); // Append a counter to avoid collisions
                }
            } else {
                // If the key doesn't exist, save the mapping
                UrlMapping urlMapping = new UrlMapping();
                urlMapping.setId(shortUrlKey);
                urlMapping.setLongUrl(longUrl);
                urlMappingRepository.save(urlMapping);
                logger.info("Successfully created short URL key {} for long URL {}", shortUrlKey, longUrl);
                return shortUrlKey;
            }
        }

        // If we exhausted all attempts, throw an exception (or handle accordingly)
        logger.error("Unable to generate unique short URL key after {} attempts for long URL: {}", maxAttempts, longUrl);
        throw new IllegalStateException("Unable to generate unique short URL key after " + maxAttempts + " attempts.");
    }

    /**
     * This is the method responsible for generating a 6-character short key.
     * 
     * @param longUrl
     * @return
     * @throws NoSuchAlgorithmException
     */

    private String getShortUrlKey(String longUrl) throws NoSuchAlgorithmException {
        logger.debug("Generating short URL key for long URL: {}", longUrl);
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] hash = digest.digest(longUrl.getBytes(StandardCharsets.UTF_8));

        // Convert hash bytes to a hex string and take the first 6 characters
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < 3; i++) { // First 3 bytes = 6 hex chars
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        String shortUrlKey = hexString.toString();
        logger.debug("Generated short URL key: {}", shortUrlKey);
        return shortUrlKey;
    }

    /**
     * This is the method responsible for fetching the correct URL for a short URL key.
     * 
     * @param shortUrlKey
     * @return
     */

    public String getLongUrl(String shortUrlKey) {
        logger.info("Fetching long URL for short URL key: {}", shortUrlKey);
        return urlMappingRepository.findById(shortUrlKey)
                .map(UrlMapping::getLongUrl)
                .orElseGet(() -> {
                    logger.warn("No long URL found for short URL key: {}", shortUrlKey);
                    return null;
                });
    }
}
