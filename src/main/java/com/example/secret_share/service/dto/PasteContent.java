package com.example.secret_share.service.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record PasteContent(String id, String content, String title, int viewCount, boolean secret, Instant expiresAt) {
}
