package org.xaut.voicemindserver.DTO;

import lombok.Data;

import java.util.List;

@Data
public class AudioAnalyzeResult {
    private String text;               // 可为空（如只预测）
    private List<Double> features;     // 可为空（如仅识别文本）
    private Double probability;        // 可为空（如仅提取特征）
}
