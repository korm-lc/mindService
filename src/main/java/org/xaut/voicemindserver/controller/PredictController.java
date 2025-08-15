package org.xaut.voicemindserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xaut.voicemindserver.DTO.AudioTaskMessage;
import org.xaut.voicemindserver.Service.AudioAnalysisProducer;
import org.xaut.voicemindserver.Service.AudioFeatureService;
import org.xaut.voicemindserver.Service.AudioService;
import org.xaut.voicemindserver.entity.AudioFeature;
import org.xaut.voicemindserver.entity.AudioRecord;
import org.xaut.voicemindserver.utils.JwtUtil;

@RestController
@RequestMapping("/api/audio")
@RequiredArgsConstructor
public class PredictController {

    private final AudioService audioService;
    private final AudioFeatureService audioFeatureService;
    private final AudioAnalysisProducer audioAnalysisProducer;
    private final JwtUtil jwtTokenUtil;

    @PostMapping("/extract")
    public ResponseEntity<?> extractFeatures(@RequestParam("questionId") String questionId,
                                             @RequestHeader("Authorization") String authHeader) {
        String userId = parseUserId(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("无效的Token");
        }

        AudioRecord audioRecord = audioService.getAudioByUserIdAndQuestionId(userId, questionId);
        if (audioRecord == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("未找到对应的音频记录");
        }

        AudioTaskMessage message = new AudioTaskMessage();
        message.setTaskType("extract");
        message.setAudioId(audioRecord.getId());
        message.setAudioUrl(audioRecord.getFileUrl());

        audioAnalysisProducer.sendAudioMessage(message);

        return ResponseEntity.ok("特征提取任务已异步发送");
    }

    @PostMapping("/predict")
    public ResponseEntity<?> predict(@RequestParam("questionId") String questionId,
                                     @RequestHeader("Authorization") String authHeader) {
        String userId = parseUserId(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("无效的Token");
        }

        AudioRecord audioRecord = audioService.getAudioByUserIdAndQuestionId(userId, questionId);
        if (audioRecord == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("未找到对应的音频记录");
        }

        AudioTaskMessage message = new AudioTaskMessage();
        message.setTaskType("predict");
        message.setAudioId(audioRecord.getId());

        audioAnalysisProducer.sendAudioMessage(message);

        return ResponseEntity.ok("预测任务已异步发送");
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeAudio(@RequestParam("questionId") String questionId,
                                          @RequestHeader("Authorization") String authHeader) {
        String userId = parseUserId(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("无效的Token");
        }

        AudioRecord audioRecord = audioService.getAudioByUserIdAndQuestionId(userId, questionId);
        if (audioRecord == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("未找到对应的音频记录");
        }

        AudioTaskMessage message = new AudioTaskMessage();
        message.setTaskType("analyze");
        message.setAudioId(audioRecord.getId());
        message.setAudioUrl(audioRecord.getFileUrl());

        audioAnalysisProducer.sendAudioMessage(message);

        return ResponseEntity.ok("音频完整分析任务已异步发送");
    }

    private String parseUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String token = authHeader.substring(7);
        return jwtTokenUtil.getSubjectFromToken(token);
    }
}
