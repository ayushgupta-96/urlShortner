package com.urlshortener.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.urlshortener.dto.UrlRequest;
import com.urlshortener.dto.UrlResponse;
import com.urlshortener.exception.ResourceNotFoundException;
import com.urlshortener.model.ClickEvent;
import com.urlshortener.model.Url;
import com.urlshortener.repository.ClickEventRepository;
import com.urlshortener.repository.UrlRepository;
import com.urlshortener.util.Base62Encoder;

@Service
public class UrlService {
    private final UrlRepository repository;
    private final ClickEventRepository clickRepo;

    @Value("${app.base-url}")
    private String baseUrl;

    public UrlService(UrlRepository repository) {
        this.repository = repository;
        this.clickRepo = null;
    }
    @Autowired
    public UrlService(UrlRepository repository, ClickEventRepository clickRepo) {
    this.repository = repository;
    this.clickRepo = clickRepo;
    }

    public UrlResponse shortenUrl(UrlRequest request) {

        String shortCode;

                // 👉 If user gives custom alias
                if (request.getCustomAlias() != null && !request.getCustomAlias().isEmpty()) {

                    if (repository.existsByShortCode(request.getCustomAlias())) {
                        throw new RuntimeException("Custom alias already exists");
                    }

                    shortCode = request.getCustomAlias();

                } else {

                    // 👉 Auto-generate and avoid duplicates
                    do {
                        shortCode = Base62Encoder.generateShortCode();
                    } while (repository.existsByShortCode(shortCode));
                }

        Url url = Url.builder()
                .originalUrl(request.getOriginalUrl())
                .shortCode(shortCode)
                .createdAt(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusDays(request.getExpiryDays()))
                .clickCount(0)
                .build();

        repository.save(url);

        return UrlResponse.builder()
                .shortUrl(baseUrl + "r/" + shortCode)
                .originalUrl(url.getOriginalUrl())
                .build();
    }
    public List<ClickEvent> getClicks(String code) {
    return clickRepo.findByShortCode(code);
    }

    public String getOriginalUrl(String code, String ip)throws ResourceNotFoundException {

        Url url = repository.findByShortCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Short URL not found"));

        if (url.getExpiryDate() != null &&
                url.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("URL expired");
        }

        // ✅ Increase click count
        url.setClickCount(url.getClickCount() + 1);
        repository.save(url);

        // ✅ Save click event
        ClickEvent event = ClickEvent.builder()
                .shortCode(code)
                .timestamp(LocalDateTime.now())
                .ipAddress(ip)
                .build();

        clickRepo.save(event);

        return url.getOriginalUrl();
    }

    public Url getStats(String code) {
        return repository.findByShortCode(code)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    public void delete(String code) {
        repository.deleteByShortCode(code);
    }
    
}