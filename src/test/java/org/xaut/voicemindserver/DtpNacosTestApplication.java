package org.xaut.voicemindserver;

import org.dromara.dynamictp.core.DtpRegistry;
import org.dromara.dynamictp.core.support.ExecutorWrapper;
import org.dromara.dynamictp.spring.annotation.EnableDynamicTp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableDynamicTp // 确保 DynamicTP 自动扫描 Nacos 配置
public class DtpNacosTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(DtpNacosTestApplication.class, args);
        System.out.println("DtpNacosTestApplication started. Waiting for Nacos config refresh...");
    }
}

/**
 * 启动时检查线程池是否已注册
 */
@Component
class DtpExecutorStartupChecker {

    @EventListener
    public void checkExecutorAfterStartup(ContextRefreshedEvent event) {
        checkExecutor("dtpExecutor1", "[Startup Check]");
    }

    private void checkExecutor(String poolName, String tag) {
        try {
            ExecutorWrapper executor = DtpRegistry.getExecutorWrapper(poolName);
            if (executor != null) {
                System.out.println(tag + " Executor loaded: " + poolName);
                System.out.println(tag + " Executor class: " + executor.getExecutor().getClass().getName());
            }
        } catch (Exception e) {
            System.out.println(tag + " Executor not found yet: " + poolName);
        }
    }
}

/**
 * 监听 Nacos 配置刷新事件
 */
@Component
class DtpNacosListener {

    @EventListener
    public void onNacosRefresh(EnvironmentChangeEvent event) {
        checkExecutor("dtpExecutor1", "[Nacos Refresh]");
    }

    private void checkExecutor(String poolName, String tag) {
        try {
            ExecutorWrapper executor = DtpRegistry.getExecutorWrapper(poolName);
            if (executor != null) {
                System.out.println(tag + " Executor loaded after refresh: " + poolName);
                System.out.println(tag + " Executor class: " + executor.getExecutor().getClass().getName());
            } else {
                System.out.println(tag + " Executor not found yet: " + poolName);
            }
        } catch (Exception e) {
            System.out.println(tag + " Exception: " + e.getMessage());
        }
    }
}
