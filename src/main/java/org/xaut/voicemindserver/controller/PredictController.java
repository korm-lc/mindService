package org.xaut.voicemindserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xaut.voicemindserver.DTO.AudioAnalyzeResult;
import org.xaut.voicemindserver.Service.AudioEmotionService;
import org.xaut.voicemindserver.Service.AudioFeatureService;
import org.xaut.voicemindserver.Service.AudioService;
import org.xaut.voicemindserver.utils.JwtUtil;
import org.xaut.voicemindserver.entity.AudioFeature;
import org.xaut.voicemindserver.entity.AudioRecord;

import java.io.IOException;

@RestController
@RequestMapping("/api/audio")
@RequiredArgsConstructor
public class PredictController {

    private final AudioEmotionService audioEmotionService;
    private final AudioService audioService;
    private final AudioFeatureService audioFeatureService;
    private final JwtUtil jwtTokenUtil;

    /**
     * 只提取音频特征
     */
    @PostMapping("/extract")
    public ResponseEntity<?> extractFeatures(@RequestParam("questionId") String questionId,
                                             @RequestHeader("Authorization") String authHeader) throws IOException {

        String userId = parseUserIdFromAuthHeader(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("无效的Token");
        }

        AudioRecord audioRecord = audioService.getAudioByUserIdAndQuestionId(userId, questionId);
        if (audioRecord == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("未找到对应的音频记录");
        }

        String fileUrl = audioRecord.getFileUrl();
        AudioAnalyzeResult result = audioEmotionService.extractFeatures(fileUrl);

        Long audioId = audioRecord.getId();
        audioFeatureService.saveAudioFeature(result, audioId);

        return ResponseEntity.ok(result);
    }

    /**
     * 只预测情绪概率
     */
    @PostMapping("/predict")
    public ResponseEntity<?> predict(@RequestParam("questionId") String questionId,
                                     @RequestHeader("Authorization") String authHeader) throws IOException {

        String userId = parseUserIdFromAuthHeader(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("无效的Token");
        }

        AudioRecord audioRecord = audioService.getAudioByUserIdAndQuestionId(userId, questionId);
        if (audioRecord == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("未找到对应的音频记录");
        }

        Long audioId = audioRecord.getId();
        AudioFeature audioFeature = audioFeatureService.getFeatureByAudioId(audioId);
        if (audioFeature == null || audioFeature.getFeatureData() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("未找到对应的特征数据");
        }

        AudioAnalyzeResult result = audioEmotionService.predict(audioFeature.getFeatureData());

        audioFeatureService.saveAudioFeature(result, audioId);

        return ResponseEntity.ok(result);
    }

    /**
     * 完整分析：文本 + 特征 + 情绪预测
     */
    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeAudio(@RequestParam("questionId") String questionId,
                                          @RequestHeader("Authorization") String authHeader) throws IOException {

        String userId = parseUserIdFromAuthHeader(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("无效的Token");
        }

        AudioRecord audioRecord = audioService.getAudioByUserIdAndQuestionId(userId, questionId);
        if (audioRecord == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("未找到对应的音频记录");
        }

        String fileUrl = audioRecord.getFileUrl();
        AudioAnalyzeResult result = audioEmotionService.analyzeAudio(fileUrl);

        Long audioId = audioRecord.getId();
        audioFeatureService.saveAudioFeature(result, audioId);

        return ResponseEntity.ok(result);
    }

    private String parseUserIdFromAuthHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        return jwtTokenUtil.getSubjectFromToken(token);
    }
}
