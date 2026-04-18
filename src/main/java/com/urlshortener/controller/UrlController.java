package com.urlshortener.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.urlshortener.dto.UrlRequest;
import com.urlshortener.dto.UrlResponse;
import com.urlshortener.exception.ResourceNotFoundException;
import com.urlshortener.service.UrlService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
public class UrlController {

    private final UrlService service;

    public UrlController(UrlService service) {
        this.service = service;
    }

    @PostMapping("/api/shorten")
    public ResponseEntity<UrlResponse> shorten(@Valid @RequestBody UrlRequest request) {
        return ResponseEntity.ok(service.shortenUrl(request));
    }

   @GetMapping("/r/{code}")
    public void redirect(@PathVariable String code,
                     HttpServletRequest request,
                     HttpServletResponse response) throws IOException, ResourceNotFoundException {

        String ip = request.getRemoteAddr();
        String originalUrl = service.getOriginalUrl(code, ip);

        response.sendRedirect(originalUrl);
    }

    @GetMapping("/api/clicks/{code}")
    public ResponseEntity<?> getClicks(@PathVariable String code) {
    return ResponseEntity.ok(service.getClicks(code));
    }

    @DeleteMapping("/api/delete/{code}")
    public ResponseEntity<String> delete(@PathVariable String code) {
        service.delete(code);
        return new ResponseEntity<>("Deleted successfully", HttpStatus.OK);
    }
}
