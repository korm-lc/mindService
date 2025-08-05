package org.xaut.voicemindserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xaut.voicemindserver.Service.AudioService;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class AudioController {


    private AudioService audioService;
    public AudioController(AudioService audioService){
        this.audioService = audioService;
    }
    @PostMapping("/upload_audio")
    public ResponseEntity<?> uploadAudio(
            @RequestParam("audio") MultipartFile file,
            @RequestParam("user_id") String userId,
            @RequestParam("question_id") String questionId) throws IOException {

        return ResponseEntity.ok(audioService.handleUpload(file, userId, questionId));
    }

    @PostMapping("/predict")
    public ResponseEntity<String> predict(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(audioService.callFastApiPredict(request));
    }
}
