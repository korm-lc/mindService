package org.xaut.voicemindserver;

import org.dromara.dynamictp.spring.annotation.EnableDynamicTp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDynamicTp
@SpringBootApplication
public class VoiceMindServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(VoiceMindServerApplication.class, args);
	}

}
