package org.xaut.voicemindserver.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xaut.voicemindserver.DTO.AudioAnalyzeResult;
import org.xaut.voicemindserver.Mapper.AudioFeatureMapper;
import org.xaut.voicemindserver.annotation.InjectTimestamps;
import org.xaut.voicemindserver.entity.AudioFeature;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@Service
public class AudioFeatureService {
    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private AudioFeatureMapper audioFeatureMapper;
    // 单独写一个方法，把传进来的结果存到数据库
    @InjectTimestamps
    public void saveAudioFeature(AudioAnalyzeResult analyzeResult,Long audioId) throws IOException {
        String text = analyzeResult.getText();
        List<Double> features = analyzeResult.getFeatures();
        Double probability = analyzeResult.getProbability();

        AudioFeature audioFeature = new AudioFeature();
        audioFeature.setAudioText(text);
        audioFeature.setFeatureData(objectMapper.writeValueAsString(features));
        audioFeature.setProbability(probability);
        audioFeature.setAudioId(audioId);

        audioFeatureMapper.insert(audioFeature);
    }

    public AudioFeature getFeatureByAudioId(Long audioId) {
        return audioFeatureMapper.getFeatureByAudioId(audioId);

    }
}
