package com.urlshortener.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urlshortener.model.Url;

public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByShortCode(String shortCode);
    void deleteByShortCode(String shortCode);
    boolean existsByShortCode(String shortCode);
}
