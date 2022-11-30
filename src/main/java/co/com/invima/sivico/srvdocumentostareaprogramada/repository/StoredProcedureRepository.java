package co.com.invima.sivico.srvdocumentostareaprogramada.repository;

public interface StoredProcedureRepository {

    String executeStoredProcedure(String spName, String jsonIn);

}
