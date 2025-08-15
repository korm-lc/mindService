package org.xaut.voicemindserver.configure;

import org.dromara.dynamictp.core.support.DynamicTp;
import org.dromara.dynamictp.core.support.ThreadPoolBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolConfiguration {

    @DynamicTp("audioAnalysisExecutor")
    @Bean
    public ThreadPoolExecutor audioAnalysisExecutor() {
        return ThreadPoolBuilder.newBuilder()
                .threadPoolName("audioAnalysisExecutor")
                .corePoolSize(5)
                .maximumPoolSize(10)
                .queueCapacity(500)
                .buildDynamic();
    }
}
