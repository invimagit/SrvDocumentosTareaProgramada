package co.com.invima.sivico.srvdocumentostareaprogramada.service.mapper;


import co.com.invima.sivico.srvdocumentostareaprogramada.dto.GenericResponseDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenericMapper {

    private final ObjectMapper mapper;

    private static final String MESSAGE = "mensaje";
    private static final String CONFIRMATION_CODE = "codigoConfirmacion";
    private static final String CONFIRMATION_MESSAGE = "mensajeConfirmacion";
    private static final String TOTAL_REGISTROS = "totalRegistros";
    private static final String RESPONSE_BODY = "retorno";

    @SneakyThrows
    public GenericResponseDTO genericMapping(String jsonStr) {

        ObjectMapper mapper = new ObjectMapper();

        JsonNode jsonResponse = mapper.readTree(jsonStr);

        ObjectNode jsonRespons;

        if (jsonResponse instanceof ArrayNode) {
            jsonRespons = (ObjectNode) jsonResponse.get(0);
        } else {
            jsonRespons = (ObjectNode) jsonResponse;
        }

        if (jsonRespons.get(MESSAGE).get(CONFIRMATION_CODE).textValue().equals("01")) {
            return GenericResponseDTO.builder()
                    .message("La petición no puede ser entendida por el servidor debido a errores de sintaxis")
                    .objectResponse(jsonRespons.get(MESSAGE).get(CONFIRMATION_MESSAGE).textValue())
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .build();
        }

        if (jsonRespons.get(MESSAGE).get(CONFIRMATION_CODE).textValue().equals("200")) {

            GenericResponseDTO genericResponse = GenericResponseDTO.builder()
                    .message(jsonRespons.get(MESSAGE))
                    .objectResponse(jsonRespons.get(RESPONSE_BODY))
                    .statusCode(HttpStatus.OK.value())
                    .build();

            if (jsonRespons.hasNonNull(TOTAL_REGISTROS)) {
                genericResponse.setTotalRegistros(Integer.valueOf(jsonRespons.get(TOTAL_REGISTROS).textValue()));
            }

            return genericResponse;
        }

        return GenericResponseDTO.builder()
                .message("Se presento una condición inesperada que impidió completar la petición")
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();
    }

    @SneakyThrows
    public String requestDtoToJsonStr(Object requestDTO) {

        return mapper.writeValueAsString(requestDTO);

    }
}
