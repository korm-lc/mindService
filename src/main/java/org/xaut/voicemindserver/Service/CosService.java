package org.xaut.voicemindserver.Service;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.sts.v20180813.StsClient;
import com.tencentcloudapi.sts.v20180813.models.AssumeRoleRequest;
import com.tencentcloudapi.sts.v20180813.models.AssumeRoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xaut.voicemindserver.configure.CosProperties;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CosService {

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

        try {
            PutObjectRequest request = new PutObjectRequest(
                    cosProperties.getBucketName(),
                    key,
                    file.getInputStream(),
                    metadata
            );
            cosClient.putObject(request);
        } catch (Exception e) {
            // 这里可以日志记录或抛出业务异常
            throw new IOException("上传到COS失败", e);
        }

        // 生成访问 URL（你可以改成 CDN 域名或绑定域名）
        return String.format("https://%s.cos.%s.myqcloud.com/%s",
                cosProperties.getBucketName(),
                cosProperties.getRegion(),
                key);
    }

    public Map<String, Object> getTemporaryCredentials() throws Exception {
        Credential cred = new Credential(
                cosProperties.getSecretId(),
                cosProperties.getSecretKey()
        );

        StsClient client = new StsClient(cred, cosProperties.getRegion());

        AssumeRoleRequest req = createAssumeRoleRequest();

        AssumeRoleResponse resp = client.AssumeRole(req);

        Map<String, Object> result = new HashMap<>();
        result.put("tmpSecretId", resp.getCredentials().getTmpSecretId());
        result.put("tmpSecretKey", resp.getCredentials().getTmpSecretKey());
        result.put("sessionToken", resp.getCredentials().getToken());
        result.put("expiredTime", resp.getExpiredTime());
        result.put("bucket", cosProperties.getBucketName());
        result.put("region", cosProperties.getRegion());
        result.put("prefix", cosProperties.getPrefix());
        return result;
    }

    private AssumeRoleRequest createAssumeRoleRequest() {
        AssumeRoleRequest req = new AssumeRoleRequest();
        req.setDurationSeconds(1800L);

        String region = cosProperties.getRegion();
        String uid = "1372824172"; // 建议改成配置参数
        String bucket = cosProperties.getBucketName();
        String prefix = cosProperties.getPrefix();

        if (!prefix.endsWith("/")) {
            prefix += "/";
        }

        String policy = "{\n" +
                "  \"version\": \"2.0\",\n" +
                "  \"statement\": [\n" +
                "    {\n" +
                "      \"action\": [\n" +
                "        \"name/cos:PutObject\",\n" +
                "        \"name/cos:PostObject\"\n" +
                "      ],\n" +
                "      \"effect\": \"allow\",\n" +
                "      \"resource\": [\n" +
                "        \"qcs::cos:" + region + ":uid/" + uid + ":prefix//" + uid + "/" + bucket + "/" + prefix + "*\"\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        req.setPolicy(policy);
        req.setRoleArn("qcs::cam::uin/100041499335:roleName/userUpload"); // 替换成实际角色ARN
        req.setRoleSessionName("upload-session");
        return req;
    }
}
