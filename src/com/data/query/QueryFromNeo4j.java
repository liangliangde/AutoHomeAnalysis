package com.data.query;

import com.algorithm.Kmeans;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

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
        Map<String, int[]> userSeriesIdMap = queryUserBySeriesId(seriesIds);
        List<List<String>> cluster = queryUserClusters(userSeriesIdMap, seriesIds, 10);
        String seriesInfo = querySeriesById(seriesIds);
        String clusterInfo = getClusterInfo(cluster);
        String collectDetailInfo = getCollectDetailInfo(cluster, seriesIds, userSeriesIdMap);
        System.out.print(clusterInfo);
    }


    private static String getCollectDetailInfo(List<List<String>> cluster, String[] seriesIds, Map<String, int[]> userSeriesIdMap) {
        StringBuffer detail = new StringBuffer();
        detail.append("seriesId,collectAmt,clusterId\n");
        for (int i = 0; i < cluster.size(); i++) {
            int clusterSize = cluster.get(i).size();
            int[] collectAmt = new int[seriesIds.length];
            for (int j = 0; j < clusterSize; j++) {
                String userId = cluster.get(i).get(j);
                int[] userVec = userSeriesIdMap.get(userId);
                for (int k = 0; k < userVec.length; k++) {
                    if (userVec[k] == 1) {
                        collectAmt[k]++;
                    }
                }
            }
            for (int k = 0; k < seriesIds.length; k++) {
                if(collectAmt[k]>0) {
                    detail.append(seriesIds[k] + "," + collectAmt[k] + "," + i + "\n");
                }
            }
        }
        return detail.toString();
    }

    private static String getClusterInfo(List<List<String>> cluster) {
        StringBuffer clusterInfo = new StringBuffer();
        clusterInfo.append("clusterId,Amount\n");
        for (int i = 0; i < cluster.size(); i++) {
            clusterInfo.append(i + "," + cluster.get(i).size() + "\n");
        }
        return clusterInfo.toString();
    }

    private static List<List<String>> queryUserClusters(Map<String, int[]> userSeriesIdMap, String[] seriesIds, int clusterNum) {
        return Kmeans.Kmeans(userSeriesIdMap, clusterNum, seriesIds.length);
    }

    private static String querySeriesById(String[] seriesIds) {
        StringBuffer seriesInfo = new StringBuffer();
        seriesInfo.append("seriesId,Amount,seriesName\n");
        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase(baseURL);
        try (Transaction ignored = db.beginTx();
             Result result = db.execute("MATCH (s:Series)<-[r:Like]-(u:User) with s,count(u) as users  ORDER BY users DESC WHERE s.seriesId in "
                     + array2String(seriesIds) + " RETURN s.seriesId + ',' + users + ',' + s.seriesName")) {
            while (result.hasNext()) {
                Map<String, Object> row = result.next();
                for (Map.Entry<String, Object> column : row.entrySet()) {
                    String record = (String) column.getValue();
                    seriesInfo.append(record + "\n");
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
                    } else {
                        userSeriesIdMap.get(userId)[seriesIds2Num.get(seriesId)] = 1;
                    }
                }
            }
        }
        db.shutdown();
        return userSeriesIdMap;
    }

}
