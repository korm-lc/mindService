package org.xaut.voicemindserver.Service;

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

    private final AudioMapper audioMapper;
    private final FastApiService fastApiService;
    private final CosService cosService;

    public AudioService(AudioMapper audioMapper, CosService cosService,
                        FastApiService fastApiService){
        this.audioMapper = audioMapper;
        this.fastApiService = fastApiService;
        this.cosService = cosService;
    }

    public Map<String, Object> handleUpload(MultipartFile file, String userId, String questionId) throws IOException {
        Map<String, Object> result = new HashMap<>();
        String fileUrl = cosService.upload(file, userId, questionId);
        //将audio的对象存储路径也存入数据库汇总
        audioMapper.saveAudioUrl(userId, questionId, fileUrl, LocalDateTime.now());

        String fastApiResult = fastApiService.transcribe(fileUrl, userId, questionId);

        result.put("fileUrl", fileUrl);
        result.put("fastapi_result", fastApiResult);
        return result;
    }

    public String callFastApiPredict(Map<String, Object> request) {
        // Call the predict method of the fastApiService with the given request
        return fastApiService.predict(request);
    }

}
