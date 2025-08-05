package org.xaut.voicemindserver.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xaut.voicemindserver.Mapper.AudioMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 这是自己的python端的api
 */
@Service
public class AudioService {

    private AudioMapper audioMapper;
    private FastApiService fastApiService;
    private ObjectStorageService objectStorageService;

    public AudioService(AudioMapper audioMapper, ObjectStorageService objectStorageService,
                        FastApiService fastApiService){
        this.audioMapper = audioMapper;
        this.fastApiService = fastApiService;
        this.objectStorageService = objectStorageService;
    }

    public Map<String, Object> handleUpload(MultipartFile file, String userId, String questionId) throws IOException {
        Map<String, Object> result = new HashMap<>();
        String audioUrl = objectStorageService.upload(file, userId, questionId);
        //将audio的对象存储路径也存入数据库汇总
        audioMapper.saveAudioUrl(userId, questionId, audioUrl, LocalDateTime.now());

        String fastApiResult = fastApiService.transcribe(audioUrl, userId, questionId);

        result.put("audio_url", audioUrl);
        result.put("fastapi_result", fastApiResult);
        return result;
    }

    public String callFastApiPredict(Map<String, Object> request) {
        return fastApiService.predict(request);
    }

}
