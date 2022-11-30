package co.com.invima.sivico.srvdocumentostareaprogramada.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


@Component
public class OpenApiConfiguration {

	@Bean
    public OpenAPI customOpenAPI(@Value("0.0.1-SNAPSHOT") String appVersion) {
        return new OpenAPI()
                .info(new Info().title("Micro de tarea programada para documentos").version(appVersion)
                		.description("Esta API es utilizada")
                        .license(new License().name("Apache 2.0").url("http://demo.org"))
                        .contact(new Contact().name("Disney").email("dlopez@soaint.com")));
    }
	

}
