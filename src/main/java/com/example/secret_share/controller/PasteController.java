package com.example.secret_share.controller;

import com.example.secret_share.service.PasteService;
import com.example.secret_share.service.dto.CreatePasteRequest;
import com.example.secret_share.service.dto.PasteContent;
import com.example.secret_share.service.dto.PasteResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pastes")
@RequiredArgsConstructor
@Validated
public class PasteController {

    private final PasteService pasteService;

    @PostMapping
    public ResponseEntity<PasteResponse> create(
            @Valid @RequestBody CreatePasteRequest req,
            HttpServletRequest httpReq
    ) {
        PasteResponse resp = pasteService.createPaste(req);
        return ResponseEntity.status(201).body(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PasteContent> read(
            @PathVariable String id,
            @RequestParam(required = false) String passphrase,
            HttpServletRequest httpReq
    ) {
        return ResponseEntity.ok(pasteService.readPaste(id));
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable String id) {
//        pasteService.deletePaste(id);
//        return ResponseEntity.noContent().build();
//    }

//    private String getClientIp(HttpServletRequest req) {
//        String fwd = req.getHeader("X-Forwarded-For");
//        return fwd != null ? fwd.split(",")[0].trim() : req.getRemoteAddr();
//    }
}
