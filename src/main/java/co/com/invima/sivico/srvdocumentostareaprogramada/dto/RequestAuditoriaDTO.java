package co.com.invima.sivico.srvdocumentostareaprogramada.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestAuditoriaDTO implements Serializable {


        private Object cabecera;
        private Object[] archivos;


}
