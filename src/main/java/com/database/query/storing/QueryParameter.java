package com.database.query.storing;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class QueryParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long queryId;
    private String parameterName;
    public QueryParameter() {
    }
    public QueryParameter(Long queryId, String parameterName) {
        this.queryId = queryId;
        this.parameterName = parameterName;
    }
}
