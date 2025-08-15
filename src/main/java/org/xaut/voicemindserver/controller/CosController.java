package org.xaut.voicemindserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xaut.voicemindserver.Service.CosService;
import org.xaut.voicemindserver.utils.JwtUtil;

import java.util.Map;

@RestController
@RequestMapping("/api/cos")
@RequiredArgsConstructor
public class CosController {

    private final CosService cosService;
    private final JwtUtil jwtUtil;

    @GetMapping("/sts")
    public ResponseEntity<?> getCosSts(@RequestHeader(value = "Authorization", required = false)
                                           String authHeader) throws Exception {
        String userId = jwtUtil.parseUserIdFromAuthHeader(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("无效的Token");
        }
        Map<String, Object> creds = cosService.getTemporaryCredentialsCached(userId);
        return ResponseEntity.ok(creds);
    }
}
