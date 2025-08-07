package org.xaut.voicemindserver.utils;

import org.springframework.stereotype.Component;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ModelUtils {

    /**
     * 获取模型的输入特征维度
     * 这个方法模拟Python版本的get_model_input_size函数
     * 在实际使用中，您需要根据您的模型文件格式来实现
     */
    public static int getModelInputSize(String modelPath) {
        try {
            // 检查模型文件是否存在
            Path path = Paths.get(modelPath);
            if (!path.toFile().exists()) {
                throw new RuntimeException("Model file not found: " + modelPath);
            }

            return 520; // 示例值，需要根据实际情况调整

        } catch (Exception e) {
            throw new RuntimeException("Failed to get model input size", e);
        }
    }

    /**
     * 检查模型文件是否存在
     */
    public static boolean modelExists(String modelPath) {
        try {
            Path path = Paths.get(modelPath);
            return path.toFile().exists();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取模型文件大小（字节）
     */
    public static long getModelFileSize(String modelPath) {
        try {
            Path path = Paths.get(modelPath);
            File file = path.toFile();
            if (file.exists()) {
                return file.length();
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }
}