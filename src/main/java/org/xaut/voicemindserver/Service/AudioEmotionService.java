package org.xaut.voicemindserver.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xaut.voicemindserver.DTO.AudioAnalyzeResult;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AudioEmotionService {

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)   // 连接超时，建议根据实际情况调
            .writeTimeout(60, TimeUnit.SECONDS)     // 写超时
            .readTimeout(120, TimeUnit.SECONDS)     // 读超时，预测耗时可能比较长，设置大一点
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${python.feature.url}")
    private String FEATURE_URL;

    @Value("${python.predict.url}")
    private String PREDICT_URL;

    @Value("${python.analyze.url}")
    private String ANALYZE_URL;

    // 只调用 /extract_features/，返回特征和文本
    public AudioAnalyzeResult extractFeatures(String fileUrl) throws IOException {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file_url", fileUrl)
                .build();

        Request request = new Request.Builder()
                .url(FEATURE_URL)
                .post(requestBody)
                .build();

        Response response = httpClient.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("特征提取失败，状态码：" + response.code());
        }

        Map<String, Object> featureResult = objectMapper.readValue(
                response.body().string(),
                new TypeReference<>() {
                }
        );
        response.close();

        AudioAnalyzeResult result = new AudioAnalyzeResult();
        result.setText((String) featureResult.get("text"));
        // 安全转换 features 成 List<Double>
        result.setFeatures(
                objectMapper.convertValue(featureResult.get("features"), new TypeReference<>() {
                })
        );

        return result;
    }

    // 只调用 /predict/，返回预测概率
    public AudioAnalyzeResult predict(String featureData) throws IOException {

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("featureData", featureData)
                .build();

        Request request = new Request.Builder()
                .url(PREDICT_URL)
                .post(requestBody)
                .build();

        Response response = httpClient.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("情绪预测失败，状态码：" + response.code());
        }

        Map<String, Object> predictResult = objectMapper.readValue(
                response.body().string(),
                new TypeReference<>() {}
        );
        response.close();

        Double probability = ((Number) predictResult.get("probability")).doubleValue();

        AudioAnalyzeResult result = new AudioAnalyzeResult();
        result.setProbability(probability);
        return result;
    }

    // 只调用 /analyze/，返回分析结果（特征、文本、概率）
    public AudioAnalyzeResult analyzeAudio(String fileUrl) throws IOException {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file_url", fileUrl)
                .build();

        Request request = new Request.Builder()
                .url(ANALYZE_URL)
                .post(requestBody)
                .build();

        Response response = httpClient.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("分析失败，状态码：" + response.code());
        }

        Map<String, Object> analyzeResult = objectMapper.readValue(
                response.body().string(),
                new TypeReference<>() {}
        );
        response.close();

        AudioAnalyzeResult result = new AudioAnalyzeResult();

        Double probability = ((Number) analyzeResult.get("probability")).doubleValue();
        result.setProbability(probability);
        result.setText((String) analyzeResult.get("text"));
        // 安全转换 features 成 List<Double>
        result.setFeatures(
                objectMapper.convertValue(analyzeResult.get("features"), new TypeReference<>() {
                })
        );

        return result;
    }
}