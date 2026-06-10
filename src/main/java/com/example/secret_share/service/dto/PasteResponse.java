package com.example.secret_share.service.dto;

import java.time.Instant;

public record PasteResponse(String id, String url, Instant expiresAt) {
}
