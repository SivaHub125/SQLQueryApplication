package com.database.query.storing;
import java.util.List;
import java.util.Map;

public interface QueryService {
    String saveStatement(String queryStatement, String sqlquery, String whereClause, String groupByClause, String havingClause, String orderByClause);

    String saveParameters(String queryParameter,String values,String sqlquery);

    Map<String, String> extractClasuse(String query);

    List<String> extractParameters(String query);
}
