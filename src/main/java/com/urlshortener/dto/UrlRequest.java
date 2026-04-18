package com.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UrlRequest {

    @NotBlank(message = "URL cannot be empty")
    @Pattern(
        regexp = "^(http|https)://.*$",
        message = "Invalid URL format (must start with http:// or https://)"
    )
    private String originalUrl;

    private String customAlias;

    private int expiryDays;
}