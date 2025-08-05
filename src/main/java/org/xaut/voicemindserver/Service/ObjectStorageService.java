package org.xaut.voicemindserver.Service;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xaut.voicemindserver.configure.CosProperties;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ObjectStorageService {

    private final CosProperties cosProperties;
    private COSClient cosClient;

    @PostConstruct
    public void init() {
        COSCredentials cred = new BasicCOSCredentials(
                cosProperties.getSecretId(),
                cosProperties.getSecretKey()
        );
        ClientConfig clientConfig = new ClientConfig(new Region(cosProperties.getRegion()));
        cosClient = new COSClient(cred, clientConfig);
    }

    public String upload(MultipartFile file, String userId, String questionId) throws IOException {
        String key = String.format("%s/%s/%s/%s",
                cosProperties.getPrefix(),
                userId,
                questionId,
                UUID.randomUUID() + "_" + file.getOriginalFilename());

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());

        PutObjectRequest request = new PutObjectRequest(
                cosProperties.getBucketName(),
                key,
                file.getInputStream(),
                metadata
        );

        cosClient.putObject(request);

        // 生成访问 URL（你可以改成 CDN 域名或绑定域名）
        return String.format("https://%s.cos.%s.myqcloud.com/%s",
                cosProperties.getBucketName(),
                cosProperties.getRegion(),
                key);
    }
}
