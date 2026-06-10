package com.example.secret_share.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "pastes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasteEntity {

    @Id
    @Column
    private String id;              // e.g. "aB3xK9mNpQ7r"

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;         // plaintext OR AES-encrypted

    @Column(name = "is_secret")
    private boolean secret;         // burn-after-read

    @Column(name = "view_limit")
    private Integer viewLimit;      // null=unlimited, N=self-destructs after N reads

    @Column(name = "view_count")
    private int viewCount = 0;

    @Column(name = "expires_at")
    private Instant expiresAt;      // null = never expires

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "title", length = 255)
    private String title;

    @PrePersist
    void onCreate() { this.createdAt = Instant.now(); }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public boolean hasReachedViewLimit() {
        return viewLimit != null && viewCount >= viewLimit;
    }
}
