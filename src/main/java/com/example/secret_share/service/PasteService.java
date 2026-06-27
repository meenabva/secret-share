package com.example.secret_share.service;

import com.example.secret_share.entity.PasteEntity;
import com.example.secret_share.exception.PasteNotFoundException;
import com.example.secret_share.repository.PasteRepository;
import com.example.secret_share.service.dto.CreatePasteRequest;
import com.example.secret_share.service.dto.PasteContent;
import com.example.secret_share.service.dto.PasteResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.IdGenerator;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasteService {

    private final PasteRepository pasteRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.base-url}")
    private String url;

    // ── CREATE ────────────────────────────────────────────────
    public PasteResponse createPaste(CreatePasteRequest req) {
        String id = String.valueOf(UUID.randomUUID());// 12-char alphanumeric
        String content = req.content();

        Instant expiresAt = req.ttlHours() != null
                ? Instant.now().plusSeconds(req.ttlHours() * 3600L)
                : null;

        PasteEntity paste = PasteEntity.builder()
                .id(id).content(content)
                .secret(req.isBurnAfterRead()).viewLimit(req.viewLimit())
                .expiresAt(expiresAt)
                .title(req.title())
                .build();

        pasteRepository.save(paste);
        log.info("Created paste id={} secret={}", id, req.isBurnAfterRead());
        return new PasteResponse(id, buildUrl(id), expiresAt);
    }

    private String buildUrl(String id) {
        return url + "/" + id;
    }

    // ── READ ──────────────────────────────────────────────────
    @Transactional
    public PasteContent readPaste(String id) {
        PasteEntity paste = pasteRepository.findById(id)
                .orElseThrow(() -> new PasteNotFoundException("Paste not found or already destroyed"));

        if (paste.isExpired()) {
            pasteRepository.delete(paste);
            throw new PasteNotFoundException("Paste has expired");
        }
        if (paste.hasReachedViewLimit()) {
            pasteRepository.delete(paste);
            throw new PasteNotFoundException("View limit reached");
        }

        String content = paste.getContent();

        // Burn-after-read: delete immediately after successful read
        if (paste.isSecret()) {
            pasteRepository.delete(paste);
            log.info("Burned secret paste id={}", id);
        } else {
            paste.setViewCount(paste.getViewCount() + 1);
            pasteRepository.save(paste);
        }

        return PasteContent.builder()
                .id(paste.getId()).content(content).title(paste.getTitle())
                .expiresAt(paste.getExpiresAt())
                .viewCount(paste.getViewCount()).secret(paste.isSecret())
                .build();
    }
}
