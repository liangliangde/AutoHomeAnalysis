package com.data.query;

import com.algorithm.Kmeans;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by llei on 16-2-22.
 */
public class QueryFromNeo4j {

    private static String baseURL = "/home/llei/IdeaProjects/autohome/auto_home.db";
    public static void main(String args[]) {
        String[] seriesIds = {"633", "639", "874", "66", "792", "364", "530", "2987"};
//        queryUserBySeriesId(seriesIds);
//        String series = querySeriesById(seriesIds);
        String userClusters = queryUserClusters(seriesIds, 5);
        System.out.print(userClusters);
    }

    private static String queryUserClusters(String[] seriesIds, int clusterNum) {
        return Kmeans.Kmeans(QueryFromNeo4j.queryUserBySeriesId(seriesIds), clusterNum, seriesIds.length);
    }

    private static String querySeriesById(String[] seriesIds) {
        StringBuffer seriesInfo = new StringBuffer();
        seriesInfo.append("seriesId,collectedUsers,seriesName\n");
        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase(baseURL);
        try (Transaction ignored = db.beginTx();
             Result result = db.execute("MATCH (s:Series)<-[r:Like]-(u:User) with s,count(u) as users  ORDER BY users WHERE s.seriesId in "
                     + array2String(seriesIds) + " RETURN s.seriesId + ',' + users + ',' + s.seriesName")) {
            while (result.hasNext()) {
                Map<String, Object> row = result.next();
                for (Map.Entry<String, Object> column : row.entrySet()) {
                    String record = (String) column.getValue();
                    seriesInfo.append(record+"\n");
                }
            }
        }
        db.shutdown();
        return seriesInfo.toString();
    }

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

        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase(baseURL);
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
