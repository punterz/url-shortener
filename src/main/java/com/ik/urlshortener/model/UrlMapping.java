package com.ik.urlshortener.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class to store the short URL key and long URL
 * 
 */

@Data
@NoArgsConstructor
@RedisHash("UrlMapping")
public class UrlMapping {
    @Id
    private String id;      // The short URL key
    private String longUrl; // The original long URL
}