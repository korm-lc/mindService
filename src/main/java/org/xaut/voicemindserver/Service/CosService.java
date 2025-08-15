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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xaut.voicemindserver.configure.CosProperties;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CosService {

    private final CosProperties cosProperties;
    private final RedisTemplate<String, Object> redisTemplate;
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

    private static final String REDIS_KEY_PREFIX = "cos:sts:";

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


    public Map<String, Object> getTemporaryCredentialsCached(String userId) throws Exception {
        String redisKey = REDIS_KEY_PREFIX + userId;
        Map<String, Object> cached = (Map<String, Object>) redisTemplate.opsForValue().get(redisKey);
        if (cached != null) {
            log.info("从Redis缓存获取STS凭证");
            return cached;
        }
        log.info("Redis缓存无凭证，重新获取");
        Map<String, Object> sts = getTemporaryCredentials();

        // 设置过期时间，腾讯STS返回的是时间戳（秒），转成过期秒数
        Long expiredTime = (Long) sts.get("expiredTime");
        long now = System.currentTimeMillis() / 1000;
        long expireSeconds = expiredTime - now - 30; // 提前30秒过期

        if (expireSeconds <= 0) {
            expireSeconds = 30; // 最少缓存30秒
        }

        redisTemplate.opsForValue().set(redisKey, sts, expireSeconds, TimeUnit.SECONDS);
        return sts;
    }

    //已有的方法，获取STS临时凭证
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
        log.info("临时STS凭证获取成功，过期时间：{}", resp.getExpiredTime());
        return result;
    }

    // createAssumeRoleRequest 保持不变
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
                "      \"action\": [\"name/cos:PutObject\", \"name/cos:PostObject\"],\n" +
                "      \"effect\": \"allow\",\n" +
                "      \"resource\": [\n" +
                "        \"qcs::cos:" + region + ":uid/" + uid + ":" + bucket + "/" + prefix + "*\"\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        req.setPolicy(policy);
        req.setRoleArn("qcs::cam::uin/100041499335:roleName/userUpload"); // 角色ARN
        req.setRoleSessionName("upload-session");
        return req;
    }
}
