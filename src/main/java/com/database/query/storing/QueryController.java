package com.database.query.storing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.List;

@RestController
public class QueryController {

    @Autowired
    private QueryService queryService;

    @PostMapping("/query")
    public String processQuery(@RequestParam String query) {
        String sqlquery=null,whereClause=null,groupByClause=null,havingClause=null,orderByClause=null,selectClause=null;

        Map<String,String> queries=queryService.extractClasuse(query);
        for (String key : queries.keySet())
        {
            if(key=="sqlQuery") {
                System.out.println("sqlquery: "+sqlquery);
                sqlquery=queries.get(key);
            }
            else if(key=="select"){
                System.out.println("select: "+selectClause);
                selectClause=queries.get(key);
            }
            else if (key=="where"){
                System.out.println("where: "+whereClause);
                whereClause=queries.get(key);
            }
            else if(key=="groupBy"){
                groupByClause=queries.get(key);
            }
            else if(key=="having"){
                havingClause=queries.get(key);
            }
            else if(key=="sortBy"){
                orderByClause=queries.get(key);
            }
            else{
                continue;
            }
        }
        String queryStatement = "Insert into sqlTable(sqlquery, whereClause, groupByClause, havingClause, orderByClause) values(?,?,?,?,?)";
        queryService.saveStatement(queryStatement,sqlquery,whereClause,groupByClause,havingClause,orderByClause);

        List<String> parameters=queryService.extractParameters(sqlquery);
        for (int i=0;i<parameters.size(); i++) {
            System.out.println("Entered parameter loop");
            String values = parameters.get(i);
            String queryParameterStatement = "Insert into parametertable(sqlTableID,parameters) values(?,?)";
            queryService.saveParameters(queryParameterStatement,values,sqlquery);
        }
        return "Success";
        }
}
