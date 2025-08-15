package org.xaut.voicemindserver.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xaut.voicemindserver.Service.AudioService;
import org.xaut.voicemindserver.utils.JwtUtil;
import org.xaut.voicemindserver.utils.TokenBucketManager;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class AudioController {

    private final TokenBucketManager tokenBucketManager;
    private final AudioService audioService;
    private final JwtUtil jwtUtil; // 自己实现的JWT工具类

    public AudioController(TokenBucketManager tokenBucketManager, AudioService audioService, JwtUtil jwtUtil){
        this.tokenBucketManager = tokenBucketManager;
        this.audioService = audioService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/upload_audio")
    public ResponseEntity<?> uploadAudio(
            @RequestParam("audio") MultipartFile file,
            @RequestParam("question_id") String questionId,
            @RequestHeader("Authorization") String authHeader) throws IOException {
                // 解析 token，获取 userId

        String token = authHeader.replace("Bearer ", "");
        String userId = jwtUtil.getSubjectFromToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("无效的Token");
        }

        if (!tokenBucketManager.tryConsume(userId)) {
            return ResponseEntity.status(429).body("操作过于频繁，请稍后再试");
        }


        // 用解析出来的 userId 调业务
        return ResponseEntity.ok(audioService.upload(file, userId, questionId));
    }

}
