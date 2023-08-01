package com.database.query.storing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class QueryServiceImpl implements QueryService{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public String saveStatement(String queryStatement,String sqlquery,String whereClause,String groupByClause,String havingClause,String orderByClause){
        jdbcTemplate.update(queryStatement,sqlquery,whereClause,groupByClause,havingClause,orderByClause);
        return "Check the table";
    }

    @Override
    public String saveParameters(String parametersQuery , String values,String sqlquery) {
        System.out.println("Entered save parameter");
        String query="Select id from sqlTable where sqlquery like '"+sqlquery+"'";
        Integer SqlId=jdbcTemplate.queryForObject(query, Integer.class);
        jdbcTemplate.update(parametersQuery,SqlId,values);
        return "Check parameter table";
    }

    @Override
    public Map<String, String> extractClasuse(String query) {
        String whereClause=null,convertedGroupByClause=null;
        String convertedWhereClause=null;
        Map<String,String> queries=separateQuery(query);
        for (String key : queries.keySet())
        {
            if(key=="sqlQuery") {
            }
            else if (key=="where"){
                whereClause=queries.get(key);
                convertedWhereClause = insertPlaceHoldersInWhereClause(whereClause);
            }
            else if(key=="groupBy"){
                String groupByClause=queries.get(key);
                convertedGroupByClause = insertPlaceHoldersInGroupByClause(groupByClause);
            }
            /*else if(key=="having"){
                String havingClause=queries.get(key);
                convertedHavingClause = insertPlaceHoldersInHavingClause(havingClause);
            }
            else if(key=="sortBy" && !queries.get(key).isEmpty()){
                String orderByClause=queries.get(key);
                convertedSortByClause = insertPlaceHoldersInSortByClause(orderByClause);
            }*/
            else{
                System.out.println("will see");
            }
        }
        String sqlQuerySt="select * form books where "+convertedWhereClause+convertedGroupByClause;
        for(String key:queries.keySet())
        {
            queries.replace("sqlQuery",sqlQuerySt);
            queries.replace("where",convertedWhereClause);
            queries.replace("groupBy",convertedGroupByClause);
        }
        return queries;
    }
    public Map<String,String> separateQuery(String query) {
        String select=null,where=null,group=null,having=null,sort=null;

        String[] whereSplit=query.split("where");

        String beforeWherePart=select=whereSplit[0];
        String afterWherePart=where=whereSplit[1];

        List<String> sqlAfterWherePart=new ArrayList<String>();
        sqlAfterWherePart.add("group by");
        sqlAfterWherePart.add("sort by");

        if(afterWherePart.contains(sqlAfterWherePart.get(0)) && afterWherePart.contains(sqlAfterWherePart.get(1))) {
            String[] groupBySplit = afterWherePart.split("group by");
            String beforeGroupPart = where = groupBySplit[0];
            String afterGroupPart = groupBySplit[1];
            String[] sortBySplit = afterGroupPart.split("sort by");
            String beforeSortPart = group = sortBySplit[0];
            String afterSortPart = sort = sortBySplit[1];
            if (afterGroupPart.contains("having")) {
                String[] havingSplit=beforeSortPart.split("having");
                String beforeHavingPart=group=havingSplit[0];
                String afterHavingPart=having=havingSplit[1];
                System.out.println("Statement 1: "+select+"where"+where+"group by"+group+"having"+having+"sort by"+ sort);

            } else {
                System.out.println("Statement 2: "+select+"where"+where+"group by"+group+"sort by"+ sort);
            }
        }
        else if(afterWherePart.contains(sqlAfterWherePart.get(0))){
            String[] groupBySplit=afterWherePart.split("group by");
            String beforeGroupPart=where=groupBySplit[0];
            String afterGroupPart=groupBySplit[1];
            if(afterGroupPart.contains("having")){
                String[] havingSplit=afterGroupPart.split("having");
                String beforeHavingPart=group=havingSplit[0];
                String afterHavingPart=having=havingSplit[1];
                System.out.println("Statement 3: "+select+"where"+where+"group by"+group+"having"+having);
            }
            else{
                group=afterGroupPart;
                System.out.println("Statement 4: "+select+"where"+where+"group by"+group);
            }
        }
        else if(afterWherePart.contains(sqlAfterWherePart.get(1))){
            String[] sortBySplit=afterWherePart.split("sort");
            String beforeSortPart=where=sortBySplit[0];
            String afterSortPart=sort=sortBySplit[1];
            System.out.println("Statement 5: "+select+"where"+where+"sort by"+sort);
        }
        else {
            System.out.println("Statement 6: "+select+"where"+where);
        }
        Map<String,String> queryMap=new HashMap<>();
        queryMap.put("sqlQuery",query);
        queryMap.put("select",select);
        queryMap.put("where",where);
        queryMap.put("groupBy",group);
        queryMap.put("having",having);
        queryMap.put("sortBy",sort);
        return queryMap;
    }

    public String insertPlaceHoldersInWhereClause(String whereQuery){
        List<String> conditions = new ArrayList();
        conditions.add("like");
        conditions.add("not like");
        conditions.add("=");
        conditions.add("!=");
        conditions.add(">");
        conditions.add("<");
        if(whereQuery.contains("or") && whereQuery.contains("and")){
            System.out.println("Have to construct");
            return null;
        }
        else if(whereQuery.contains("or")) {
            String constructWhereString=null;
            String[] splitbyOr=whereQuery.split("or");
            
            for (int i=0;i<splitbyOr.length;i++) {
                
                String checkWhereCondition = splitbyOr[i];
                
                for (String condition:conditions){
                    if (checkWhereCondition.contains(condition)){
                        
                        String[] getSplitArray=checkWhereCondition.split(condition);
                        String placeholder ="{{"+getSplitArray[0].strip()+"}}";
                        if(constructWhereString==null){
                            constructWhereString=getSplitArray[0]+condition+placeholder;
                        }
                        else {
                            constructWhereString=constructWhereString+" or "+getSplitArray[0]+condition+placeholder;
                        }
                        break;
                    }
                }
            }
            return constructWhereString;
        }
        else if(whereQuery.contains("and")){
            String constructWhereString=null;
            
            String[] splitByAnd=whereQuery.split("and");
            
            for (int i=0;i<splitByAnd.length;i++) {
                String checkWhereCondition = splitByAnd[i];
                
                for (String condition:conditions){
                    
                    if (checkWhereCondition.contains(condition)){
                        
                        String[] getSplitArray=checkWhereCondition.split(condition);
                        String placeholder ="{{"+getSplitArray[0].strip()+"}}";
                        if(constructWhereString==null){
                            constructWhereString=getSplitArray[0]+condition+placeholder;
                        }
                        else {
                            constructWhereString=constructWhereString+"and"+getSplitArray[0]+condition+placeholder;
                        }
                        break;
                    }
                }
            }
            return constructWhereString;
        }
        else {
            System.out.println("This is else part");
            return null;
        }
    }
    public String insertPlaceHoldersInGroupByClause(String groupBy){
        String placeholder="{{Column name}}";
        String convertedGroupBy="group by"+placeholder;
        System.out.println("group by: "+convertedGroupBy);
        return convertedGroupBy;
    }
    @Override
    public List<String> extractParameters(String input) {
        System.out.println("parameter extract");
        String patterExpression="\\{\\{\\s*(.*?)\\s*\\}\\}";
        List<String> values = new ArrayList<>();
        Pattern pattern = Pattern.compile(patterExpression);
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            String value = matcher.group(1);
            values.add(value);
        }
        for (int i=0;i<values.size();i++){
            System.out.println("Values: "+values.get(i));
        }
        return values;
    }

}
