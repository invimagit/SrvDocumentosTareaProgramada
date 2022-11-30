package co.com.invima.sivico.srvdocumentostareaprogramada.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import java.sql.Types;

@Service
@RequiredArgsConstructor
public class StoredProcedureRepositoryImpl implements StoredProcedureRepository{

    private final JdbcTemplate jdbcTemplate;

    private final String SP_SCHEMA = "Onudi";
    private final String SP_PARAMETER_IN = "json_in";
    private final String SP_PARAMETER_OUT = "json_OUT";

    @Override
    public String executeStoredProcedure(String spName, String jsonIn) {

        return new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName(SP_SCHEMA)
                .withProcedureName(spName)
                .declareParameters(
                        new SqlParameter(SP_PARAMETER_IN, Types.LONGVARCHAR),
                        new SqlOutParameter(SP_PARAMETER_OUT, Types.LONGVARCHAR)
                )
                .execute(new MapSqlParameterSource().addValue(SP_PARAMETER_IN, jsonIn))
                .get(SP_PARAMETER_OUT).toString();
    }
}
