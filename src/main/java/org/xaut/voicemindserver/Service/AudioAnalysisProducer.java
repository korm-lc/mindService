package org.xaut.voicemindserver.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.xaut.voicemindserver.DTO.AudioTaskMessage;
import org.xaut.voicemindserver.configure.RabbitConfig;

@Service
@RequiredArgsConstructor
public class AudioAnalysisProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送音频任务到队列
     * @param message 包含 taskType, audioId, audioUrl 等信息
     */
    public void sendAudioMessage(AudioTaskMessage message) {
        // 发送到配置好的交换机和路由键
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, RabbitConfig.ROUTING_KEY, message);
        System.out.println("音频任务已发送到队列: " + message);
    }

    /**
     * 辅助方法：直接通过音频 URL 生成任务并发送
     */
    public void sendAudioUrlAsTask(Long audioId, String audioUrl, String taskType) {
        AudioTaskMessage message = new AudioTaskMessage();
        message.setAudioId(audioId);
        message.setAudioUrl(audioUrl);
        message.setTaskType(taskType);
        // 可以根据需要设置其他字段
        sendAudioMessage(message);
    }
}
