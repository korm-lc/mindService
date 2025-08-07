package org.xaut.voicemindserver.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class FastApiService {

    WebClient webClient;
    public FastApiService(WebClient webClient){
        this.webClient = webClient;
    }

    public String transcribe(String url, String userId, String questionId) {
        return webClient.post()
                .uri("/transcribe")
                .bodyValue(Map.of("audio_url", url, "user_id", userId, "question_id", questionId))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String predict(Map<String, Object> features) {
        return webClient.post()
                .uri("/predict")
                .bodyValue(features)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
