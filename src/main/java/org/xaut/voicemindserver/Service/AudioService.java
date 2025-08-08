package org.xaut.voicemindserver.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xaut.voicemindserver.Mapper.AudioMapper;
import org.xaut.voicemindserver.entity.AudioRecord;

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
    private final CosService cosService;
    public AudioService(AudioMapper audioMapper, CosService cosService){
        this.audioMapper = audioMapper;
        this.cosService = cosService;
    }

    public Map<String, Object> upload(MultipartFile file, String userId, String questionId) throws IOException {
        Map<String, Object> result = new HashMap<>();
        String fileUrl = cosService.upload(file, userId, questionId);
        //将audio的对象存储路径也存入数据库汇总
        audioMapper.saveAudioUrl(userId, questionId, fileUrl, LocalDateTime.now());
        result.put("fileUrl", fileUrl);
        result.put("userId", userId);
        result.put("questionId", questionId);
        return result;
    }

    public AudioRecord getAudioIdOnCos(String fileUrl){
        AudioRecord audioRecord = audioMapper.findAudioIdByUrl(fileUrl);
        return audioRecord;
    }

    public AudioRecord getAudioByUserIdAndQuestionId(String userId, String questionId){
        AudioRecord audioRecord = audioMapper.getAudioByUserIdAndQusetionId(userId,questionId);
        return audioRecord;
    }

}
