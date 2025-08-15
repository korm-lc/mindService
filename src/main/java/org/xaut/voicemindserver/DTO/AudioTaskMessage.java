package org.xaut.voicemindserver.DTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class AudioTaskMessage{
    private String taskType;  // "extract" / "predict" / "analyze"
    private String audioUrl;
    private Long audioId;     // 可选，用于数据库操作
    private String featureData; // predict 任务可能用
    // getter & setter
}
