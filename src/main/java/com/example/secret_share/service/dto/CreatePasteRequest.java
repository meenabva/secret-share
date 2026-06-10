package com.example.secret_share.service.dto;

public record CreatePasteRequest(String content, String title,
                                 boolean isBurnAfterRead,
                                 int viewLimit,
                                 Long ttlHours) {
}
