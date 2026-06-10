package com.example.secret_share.repository;

import com.example.secret_share.entity.PasteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface PasteRepository extends JpaRepository<PasteEntity, String> {

    @Query("SELECT p FROM PasteEntity p WHERE p.expiresAt IS NOT NULL AND p.expiresAt < :now")
    List<PasteEntity> findExpired(@Param("now") Instant now);

    long countByCreatedAtAfter(Instant since);
}
