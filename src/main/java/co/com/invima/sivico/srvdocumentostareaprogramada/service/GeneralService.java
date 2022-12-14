package co.com.invima.sivico.srvdocumentostareaprogramada.service;
import co.com.invima.canonicalmodelsivico.dtosivico.GenericRequestDTO;
import co.com.invima.canonicalmodelsivico.dtosivico.GenericResponseDTO;
import co.com.invima.canonicalmodelsivico.dtosivico.requestbody.AuditoriaParamsDTO;
import co.com.invima.canonicalmodelsivico.dtosivico.requestbody.GenericSpBodyDTO;
import co.com.invima.sivico.srvdocumentostareaprogramada.config.ConfigProperties;
import co.com.invima.sivico.srvdocumentostareaprogramada.dto.RequestAuditoriaDTO;
import co.com.invima.sivico.srvdocumentostareaprogramada.dto.RequestDTOSP;
import co.com.invima.sivico.srvdocumentostareaprogramada.repository.StoredProcedureRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;


import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;
import java.util.concurrent.TimeUnit;


@XmlRootElement
@Component
public class GeneralService extends WebServiceGatewaySupport {
    private static final Logger log = LoggerFactory.getLogger(GeneralService.class);

    private final WebClient webClient;

    private final ConfigProperties configProperties;

    private final StoredProcedureRepository storedProcedureRepository;


    @Autowired
    public GeneralService(WebClient.Builder webClientBuilder,
                           ConfigProperties configProperties, StoredProcedureRepository storedProcedureRepository) {

        this.configProperties = configProperties;
        this.storedProcedureRepository = storedProcedureRepository;
        TcpClient tcpClient = TcpClient
                .create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 150000)
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(150000, TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(150000, TimeUnit.MILLISECONDS));
                });

        this.webClient = webClientBuilder
                .baseUrl(this.configProperties.getUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                .build();


    }


   // @Scheduled(fixedDelayString = "${scheduler.fixeddelay}", initialDelayString = "${scheduler.initialDelay}")
   @Scheduled(fixedDelayString = "360000", initialDelayString = "00001")
    public void enviarDocumentos() throws JsonProcessingException {


        //Consulta el procedimiento almacenado
        log.info("Inicio Consulta SP  EnviarDocumentoError");
        String responseSp = storedProcedureRepository.executeStoredProcedure("USP_EnviarDocumentoError_S", null);
        log.info("Fin Consulta SP EnviarDocumentoError");

        if(!responseSp.isEmpty() && !"".equals(responseSp)) {
            Gson g = new Gson();
            RequestAuditoriaDTO requestAuditoriaDTO = g.fromJson(responseSp,RequestAuditoriaDTO.class);
            // sacar el numero registron requestAuditoriaDTO
            AuditoriaParamsDTO  auditoriaParamsDTO = AuditoriaParamsDTO.builder().ip("192.168.0.1").usuario("Leonardo").build();

            Object numeroRegistro= (new JSONObject(responseSp)).getJSONObject("cabecera").get("numeroRegistro");
            JSONObject json = new JSONObject();
            json.put("numeroRegistro", numeroRegistro);
            RequestDTOSP request= RequestDTOSP.builder().entrada(requestAuditoriaDTO.getCabecera()).auditoria(auditoriaParamsDTO).build();

            String responseSpBorrar = storedProcedureRepository.executeStoredProcedure("USP_BorrarRegistroEnviado_D", request.toString());
        }
    }

    public GenericResponseDTO consultarSP(GenericRequestDTO request) {
        return webClient.post()
                .uri("/generico/consumir-sp")
                .body(Mono.just(GenericSpBodyDTO.builder()
                                .schema("Onudi")
                                .procedureName("USP_EnviarDocumentoError_S")
                                .jsonIn(null)
                                .build()),
                        GenericSpBodyDTO.class)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        clientResponse -> clientResponse
                                .bodyToMono(GenericResponseDTO.class)
                                .flatMap(body -> {
                                    var message = body.getObjectResponse().toString();
                                    return Mono.error(new NoSuchElementException(message));
                                }))
                .onStatus(HttpStatus::is5xxServerError,
                        clientResponse -> clientResponse
                                .bodyToMono(GenericResponseDTO.class)
                                .flatMap(body -> {
                                    var message = body.getObjectResponse().toString();
                                    return Mono.error(new Exception(message));
                                }))
                .bodyToMono(GenericResponseDTO.class)
                .block();
    }

}
