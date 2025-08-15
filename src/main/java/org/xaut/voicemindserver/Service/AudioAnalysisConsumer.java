package org.xaut.voicemindserver.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.xaut.voicemindserver.DTO.AudioAnalyzeResult;
import org.xaut.voicemindserver.DTO.AudioTaskMessage;
import org.xaut.voicemindserver.configure.RabbitConfig;
import org.xaut.voicemindserver.entity.AudioFeature;

import java.util.concurrent.ThreadPoolExecutor;

@Service
@RequiredArgsConstructor
public class AudioAnalysisConsumer {

    private final AudioEmotionService audioEmotionService;
    private final AudioFeatureService audioFeatureService;
    private final ThreadPoolExecutor audioAnalysisExecutor;

    /**
     * 使用 Spring Boot 默认的 RabbitListener 容器，自动支持 JSON 转换
     */
    @RabbitListener(queues = RabbitConfig.QUEUE_NAME) // 可以在 application.yml 中配置队列名
    public void receiveMessage(AudioTaskMessage message) {
        audioAnalysisExecutor.execute(() -> processTask(message));
    }

    private void processTask(AudioTaskMessage message) {
        try {
            String taskType = message.getTaskType();
            Long audioId = message.getAudioId();

            switch (taskType) {
                case "extract":
                    handleExtract(message, audioId);
                    break;

                case "predict":
                    handlePredict(message, audioId);
                    break;

                case "analyze":
                    handleAnalyze(message, audioId);
                    break;

                default:
                    System.err.println("未知任务类型：" + taskType);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 可选：发送到死信队列或重试
        }
    }

    private void handleExtract(AudioTaskMessage message, Long audioId) throws Exception {
        String audioUrl = message.getAudioUrl();
        AudioAnalyzeResult result = audioEmotionService.extractFeatures(audioUrl);
        audioFeatureService.saveAudioFeature(result, audioId);
        System.out.println("特征提取完成，audioId=" + audioId);
    }

    private void handlePredict(AudioTaskMessage message, Long audioId) throws Exception {
        String featureData = message.getFeatureData();
        if (featureData == null) {
            AudioFeature feature = audioFeatureService.getFeatureByAudioId(audioId);
            if (feature == null || feature.getFeatureData() == null) {
                System.err.println("特征数据不存在，audioId=" + audioId);
                return;
            }
            featureData = feature.getFeatureData();
        }
        AudioAnalyzeResult result = audioEmotionService.predict(featureData);
        audioFeatureService.saveAudioFeature(result, audioId);
        System.out.println("情绪预测完成，audioId=" + audioId);
    }

    private void handleAnalyze(AudioTaskMessage message, Long audioId) throws Exception {
        String audioUrl = message.getAudioUrl();
        AudioAnalyzeResult result = audioEmotionService.analyzeAudio(audioUrl);
        audioFeatureService.saveAudioFeature(result, audioId);
        System.out.println("音频完整分析完成，audioId=" + audioId);
    }
}
