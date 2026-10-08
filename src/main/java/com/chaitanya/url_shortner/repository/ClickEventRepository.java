package com.chaitanya.url_shortner.repository;

import com.chaitanya.url_shortner.Entity.ClickEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClickEventRepository
        extends JpaRepository<ClickEvent, Long> {

    List<ClickEvent> findByShortCode(String shortCode);

    long countByShortCode(String shortCode);
}