package com.chaitanya.url_shortner.service;

import com.chaitanya.url_shortner.Entity.ClickEvent;
import com.chaitanya.url_shortner.repository.ClickEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnalyticsService {
    @Autowired
    private ClickEventRepository clickEventRepository;

    public void recordClick(
            String shortCode,
            HttpServletRequest request) {

        ClickEvent clickEvent = new ClickEvent();

        clickEvent.setShortCode(shortCode);
        clickEvent.setClickedAt(LocalDateTime.now());
        clickEvent.setIpAddress(request.getRemoteAddr());
        clickEvent.setUserAgent(request.getHeader("User-Agent"));

        clickEventRepository.save(clickEvent);

        System.out.println(
                "Click recorded for URL: " + shortCode
        );
    }

    public List<ClickEvent> getAnalytics(String shortCode) {

        return clickEventRepository
                .findByShortCode(shortCode);
    }

    public long getClickCount(String shortCode) {

        return clickEventRepository
                .countByShortCode(shortCode);
    }
}