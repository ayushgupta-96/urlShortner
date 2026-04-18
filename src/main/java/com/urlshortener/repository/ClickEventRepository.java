package com.urlshortener.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urlshortener.model.ClickEvent;

public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {

    List<ClickEvent> findByShortCode(String shortCode);
}
