package com.data.query;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by llei on 16-2-22.
 */
public class QueryFromNeo4j {
//    public static void main(String args[]) {
//        String[] aa = {"2791", "2236", "395"};
//        queryUserBySeriesId(aa);
//    }

    private static String array2String(String[] arr) {
        StringBuffer str = new StringBuffer();
        str.append("[");
        for (int i = 0; i < arr.length; i++) {
            str.append("'" + arr[i] + "'");
            if (i < arr.length - 1) {
                str.append(',');
            }
        }
        str.append("]");
        return str.toString();
    }

    public static Map<String, int[]> queryUserBySeriesId(String[] seriesIds) {
        Map<String, int[]> userSeriesIdMap = new HashMap<>();

        Map<String, Integer> seriesIds2Num = new HashMap<>();
        for (int i = 0; i < seriesIds.length; i++) {
            seriesIds2Num.put(seriesIds[i], i);
        }

        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase("/home/llei/IdeaProjects/autohome_analysis/auto_home.db");
        try (Transaction ignored = db.beginTx();
             Result result = db.execute("MATCH (u)-[r:Like]->(s:Series) where s.seriesId in " + array2String(seriesIds) + " RETURN s.seriesId + ',' + u.userId")) {
            while (result.hasNext()) {
                Map<String, Object> row = result.next();
                for (Map.Entry<String, Object> column : row.entrySet()) {
                    String record = (String) column.getValue();
                    String seriesId = record.substring(0, record.indexOf(','));
                    String userId = record.substring(record.indexOf(',') + 1);
                    if (!userSeriesIdMap.containsKey(userId)) {
                        int[] featureArr = new int[seriesIds.length];
                        featureArr[seriesIds2Num.get(seriesId)] = 1;
                        userSeriesIdMap.put(userId, featureArr);
                    }
                    else{
                        userSeriesIdMap.get(userId)[seriesIds2Num.get(seriesId)] = 1;
                    }
                }
            }
        }
        db.shutdown();
        return userSeriesIdMap;
    }
}
