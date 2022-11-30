package co.com.invima.sivico.srvdocumentostareaprogramada.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Data
@Configuration
@ConfigurationProperties(prefix = "servicio")
@Primary
public class ConfigProperties {

	private String url;

	
}
