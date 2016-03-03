package com.data.query;

import com.algorithm.Kmeans.KmeansFor01Vec;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.IO.IOProcess.writeFile;

/**
 * Created by llei on 16-2-22.
 */
public class QueryFromNeo4j {

    private static String baseURL = "/home/llei/IdeaProjects/autohome/auto_home.db";

    public static void main(String args[]) throws IOException {
//        String[] seriesIds = {"633", "639", "874", "66", "792", "364", "530", "2987"};
//        String[] seriesIds = {"65", "66", "3207", "692", "588", "639", "364", "526", "633", "442"};
//        Map<String, int[]> userSeriesIdMap = queryUserBySeriesId(seriesIds);
//        List<List<String>> cluster = queryUserClusters(userSeriesIdMap, seriesIds, 10);
//        String seriesInfo = querySeriesById(seriesIds);
//        Map<String, String> seriesId2NameMap = seriesId2Name(seriesInfo);

//        String clusterInfo = getClusterInfo(cluster);
//        String collectDetailInfo = getCollectDetailInfo(cluster, seriesIds, userSeriesIdMap);
//        System.out.print(clusterInfo);
//        makeLDADoc(userSeriesIdMap, seriesIds, seriesId2NameMap);
    }

    private static Map<String, String> seriesId2Name(String seriesInfo) {
        Map<String, String> seriesId2NameMap = new HashMap<>();
        String[] seriesInfoArr = seriesInfo.split("\n");
        for (int i = 1; i < seriesInfoArr.length; i++) {
            String[] series = seriesInfoArr[i].split(",");
            seriesId2NameMap.put(series[0], series[2]);
        }
        return seriesId2NameMap;
    }

    private static void makeLDADoc(Map<String, int[]> userSeriesIdMap, String[] seriesIds, Map<String, String> seriesId2NameMap) throws IOException {
        StringBuffer str = new StringBuffer();
        str.append(userSeriesIdMap.size()).append("\n");
        for (Map.Entry<String, int[]> entry : userSeriesIdMap.entrySet()) {
            int[] value = entry.getValue();
            for (int i = 0; i < value.length; i++) {
                if (value[i] == 1) {
                    str.append(seriesId2NameMap.get(seriesIds[i])).append(" ");
                }
            }
            str.append("\n");
        }
        writeFile("LDAinput.txt", str.toString());
    }


    public static String getCollectDetailInfo(List<List<String>> cluster, String[] seriesIds, Map<String, int[]> userSeriesIdMap) {
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
                if (collectAmt[k] > 0) {
                    detail.append(seriesIds[k] + "," + collectAmt[k] + "," + i + "\n");
                }
            }
        }
        return detail.toString();
    }

    public static String getClusterInfo(List<List<String>> cluster) {
        StringBuffer clusterInfo = new StringBuffer();
        clusterInfo.append("clusterId,Amount\n");
        for (int i = 0; i < cluster.size(); i++) {
            clusterInfo.append(i + "," + cluster.get(i).size() + "\n");
        }
        return clusterInfo.toString();
    }

    public static List<List<String>> queryUserClusters(Map<String, int[]> userSeriesIdMap, String[] seriesIds, int clusterNum) {
        return KmeansFor01Vec.Kmeans(userSeriesIdMap, clusterNum, seriesIds.length);
    }

    public static String querySeriesById(String[] seriesIds) {
        StringBuffer seriesInfo = new StringBuffer();
        seriesInfo.append("seriesId,Amount,seriesName\n");
        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase(baseURL);
        try (Transaction ignored = db.beginTx();
             Result result = db.execute("MATCH (s:Series)<-[r:Like]-(u:User) with s,count(u) as userNum  ORDER BY users DESC WHERE s.seriesId in "
                     + array2String(seriesIds) + " RETURN s.seriesId + ',' + userNum + ',' + s.seriesName")) {
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
