package org.xaut.voicemindserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class DtpTestApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(DtpTestApplication.class, args);

        Environment env = context.getEnvironment();
        String poolName = env.getProperty("dynamictp.executors[0].threadPoolName");
        System.out.println("DynamicTP executor name from config: " + poolName);
    }
}