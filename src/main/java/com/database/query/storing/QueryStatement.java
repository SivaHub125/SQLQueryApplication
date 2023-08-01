package com.database.query.storing;

import jakarta.persistence.*;
public class QueryStatement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String query;
    private String whereClause;
    private String groupByClause;
    private String havingClause;
    private String orderByClause;
    public QueryStatement() {
    }
    public QueryStatement(String query, String whereClause, String groupByClause, String havingClause, String orderByClause) {
        this.query = query;
        this.whereClause = whereClause;
        this.groupByClause = groupByClause;
        this.havingClause = havingClause;
        this.orderByClause = orderByClause;
    }

    public QueryStatement(String sqlquery, String whereClause, String groupByClause, String havingClause, String orderByClause, String others) {
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
}
