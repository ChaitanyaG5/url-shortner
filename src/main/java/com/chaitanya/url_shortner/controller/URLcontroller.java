package com.chaitanya.url_shortner.controller;

import com.chaitanya.url_shortner.service.AnalyticsService;
import com.google.common.hash.Hashing;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RequestMapping("/rest/url")
@RestController
public class URLcontroller {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    AnalyticsService analyticsService;


    @GetMapping("/{id}")
    public String getUrl(
            @PathVariable String id,
            HttpServletRequest request) {

        // Get original URL from Redis
        String url = redisTemplate
                .opsForValue()
                .get(id);

        System.out.println("URL Retrieved: " + url);

        if (url == null) {
            throw new RuntimeException(
                    "There is no shorter URL for : " + id
            );
        }

        // Record analytics in MySQL
        analyticsService.recordClick(id, request);

        // Return original URL
        return url;
    }


    @PostMapping
    public String create(@RequestBody String url) {

        UrlValidator urlValidator =
                new UrlValidator(
                        new String[]{"http", "https"}
                );

        if (urlValidator.isValid(url)) {

            String id =
                    Hashing.murmur3_32()
                            .hashString(
                                    url,
                                    StandardCharsets.UTF_8
                            )
                            .toString();

            System.out.println(
                    "URL Id generated: " + id
            );

            // Store URL in Redis
            redisTemplate
                    .opsForValue()
                    .set(id, url);

            return id;
        }

        throw new RuntimeException(
                "URL Invalid: " + url
        );
    }
}