package com.ik.urlshortener;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a very high-level check to ensure that your application's Spring context 
 * is configured correctly and that all components can be loaded without errors.
 * 
 */

@SpringBootTest
class UrlShortenerApplicationTests {

    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerApplicationTests.class);

    @Test
    void contextLoads() {
        logger.info("Starting the Spring context load test.");

        // The contextLoads test is designed to ensure that the Spring context loads successfully
        // No assertions needed, just checking that the application context can start without issues

        // Log message indicating successful context load
        logger.info("Spring context loaded successfully.");
    }
}
