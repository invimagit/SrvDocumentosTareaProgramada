package co.com.invima.sivico.srvdocumentostareaprogramada;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {RedisAutoConfiguration.class})
@EnableScheduling
public class SrvDocumentoTareaProgramadaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SrvDocumentoTareaProgramadaApplication.class, args);
	}

}
