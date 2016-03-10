package com.data.query;

import com.algorithm.Kmeans.KmeansFor01Vec;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.IOException;
import java.util.*;

import static com.IO.IOProcess.writeFile;

/**
 * Created by llei on 16-2-22.
 */
public class QueryFromNeo4j {

    private static String baseURL = "/home/llei/IdeaProjects/autohome/auto_home.db";

    public static void main(String args[]) throws IOException {
//        String[] seriesIds = {"633", "639"};
//        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase(baseURL);
//        List<String> attrList = QueryFromNeo4j.querySeriesAttrBySeriesIds(seriesIds, db);
//        List<String> similarSeries = QueryFromNeo4j.querySeriesByAttr(seriesIds, attrList, db);
//        int[][] comUsersMatrix = queryComUsersOfSimilarSeries(similarSeries, db);
//        int candNum = 10;
//        List<String> candSeries = selectCandSeries(comUsersMatrix, similarSeries, seriesIds, candNum);
////        System.out.print(list2String(candSeries));
//        db.shutdown();
    }


    public static int queryComUsersOf2Series(String s1, String s2, GraphDatabaseService db) {
        int comUser = 0;
        try (Transaction ignored = db.beginTx();
             Result result = db.execute("match (s1:Series{seriesId:'" + s1 + "'})<-[:Like]-(u:User)-[:Like]->(s2:Series{seriesId:'"
                     + s2 + "'}) return count(distinct u)")) {
            while (result.hasNext()) {
                Map<String, Object> row = result.next();
                for (Map.Entry<String, Object> column : row.entrySet()) {
                    String record = column.getValue().toString();
                    comUser = Integer.parseInt(record);
                }
            }
        }
        return comUser;
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

    private static String list2String(List<String> seriesIds) {
        StringBuffer str = new StringBuffer();
        str.append("[");
        for (int i = 0; i < seriesIds.size(); i++) {
            str.append("'" + seriesIds.get(i) + "'");
            if (i < seriesIds.size() - 1) {
                str.append(',');
            }
        }
        str.append("]");
        return str.toString();
    }

    private static String array2String(Object[] arr) {
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

    public static Map<String, int[]> queryUserBySeriesId(Object[] seriesIds, GraphDatabaseService db) {
        Map<String, int[]> userSeriesIdMap = new HashMap<>();
        Map<Object, Integer> seriesIds2Num = new HashMap<>();
        for (int i = 0; i < seriesIds.length; i++) {
            seriesIds2Num.put(seriesIds[i], i);
        }

        try (Transaction ignored = db.beginTx();
             Result result = db.execute("MATCH (u)-[r:Like]->(s:Series) where s.seriesId in " + array2String(seriesIds)
                     + " RETURN s.seriesId + ',' + u.userId + ',' + u.userName + ',' + u.gender + ',' + u.location + ',' + u.birthday + ',' + u.verified")) {
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
        System.out.println("Query users of candidate series finished!");
        return userSeriesIdMap;
    }

    public static List<String[]> querySeriesPair(int num) {
        //query series pairs with more than 'num' common collect users
        List<String[]> seriesPairs = new ArrayList<>();
        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase(baseURL);
        try (Transaction ignored = db.beginTx();
             Result result = db.execute("match (s1:Series)<-[:Like]-(u:User)-[:Like]->(s2:Series) with s1,s2, count(u) as num " +
                     "where s1.seriesId > s2.seriesId and num>=" + num + " return s1.seriesId+','+s2.seriesId")) {
            while (result.hasNext()) {
                Map<String, Object> row = result.next();
                for (Map.Entry<String, Object> column : row.entrySet()) {
                    seriesPairs.add(((String) column.getValue()).split(","));
                }
            }
        }
        db.shutdown();
        return seriesPairs;
    }

    public static List<Map<String, Integer>> querySeriesAttrByCluster(List<List<String>> clusterSeriesIds, GraphDatabaseService db) {
        List<Map<String, Integer>> clusterTermsFreq = new ArrayList<>();
        Map<String, Integer> totalTermsFreq = new HashMap<>();
        for (int i = 0; i < clusterSeriesIds.size(); i++) {
            Map<String, Integer> termsFreq = new HashMap<>();
            List<String> seriesIds = clusterSeriesIds.get(i);
            try (Transaction ignored = db.beginTx();
                 Result result = db.execute("match (s:Series)-[Is]->(a:SeriesAttr) where s.seriesId in "
                         + list2String(seriesIds) + " RETURN a.attr")) {
                while (result.hasNext()) {
                    Map<String, Object> row = result.next();
                    for (Map.Entry<String, Object> column : row.entrySet()) {
                        String value = (String) column.getValue();
                        if (!termsFreq.containsKey(value)) {
                            termsFreq.put(value, 1);
                        } else {
                            termsFreq.put(value, termsFreq.get(value) + 1);
                        }
                        if (!totalTermsFreq.containsKey(value)) {
                            totalTermsFreq.put(value, 1);
                        } else {
                            totalTermsFreq.put(value, totalTermsFreq.get(value) + 1);
                        }
                    }
                }
            }
            clusterTermsFreq.add(termsFreq);
        }
        clusterTermsFreq.add(totalTermsFreq);
        return clusterTermsFreq;
    }

    public static List<Map<String, Integer>> queryUserInfoBySeriesCluster(List<List<String>> clusterSeriesIds, GraphDatabaseService db) {
        List<Map<String, Integer>> clusterTermsFreq = new ArrayList<>();
        Map<String, Integer> totalTermsFreq = new HashMap<>();
        for (int i = 0; i < clusterSeriesIds.size(); i++) {
            Map<String, Integer> termsFreq = new HashMap<>();
            List<String> seriesIds = clusterSeriesIds.get(i);
            try (Transaction ignored = db.beginTx();
                 Result result = db.execute("match (s:Series)<-[:Like]-(u:User) where s.seriesId in "
                         + list2String(seriesIds) + "return '<性别>'+u.gender+',<所在地>'+u.location+',<年龄段>'+u.ageCategory")) {
                while (result.hasNext()) {
                    Map<String, Object> row = result.next();
                    for (Map.Entry<String, Object> column : row.entrySet()) {
                        String value = (String) column.getValue();
                        String terms[] = value.split(",");
                        for (int j = 0; j < terms.length; j++) {
                            String term = terms[j];
                            if (term.contains("null") || term.contains("其他") || term.contains("其它"))
                                continue;
                            if (!termsFreq.containsKey(term)) {
                                termsFreq.put(term, 1);
                            } else {
                                termsFreq.put(term, termsFreq.get(term) + 1);
                            }
                            if (!totalTermsFreq.containsKey(term)) {
                                totalTermsFreq.put(term, 1);
                            } else {
                                totalTermsFreq.put(term, totalTermsFreq.get(term) + 1);
                            }
                        }
                    }
                }
            }
            clusterTermsFreq.add(termsFreq);
        }
        clusterTermsFreq.add(totalTermsFreq);
        return clusterTermsFreq;
    }

    public static List<Map<String, Integer>> queryKoubeiBySeriesCluster(List<List<String>> clusterSeriesIds, GraphDatabaseService db) {
        List<Map<String, Integer>> clusterTermsFreq = new ArrayList<>();
        Map<String, Integer> totalTermsFreq = new HashMap<>();
        for (int i = 0; i < clusterSeriesIds.size(); i++) {
            Map<String, Integer> termsFreq = new HashMap<>();
            List<String> seriesIds = clusterSeriesIds.get(i);
            try (Transaction ignored = db.beginTx();
                 Result result = db.execute("MATCH (series:Series)-[r:Has]->(style:Style)<-[:About]-(k:Koubei) where series.seriesId in "
                         + list2String(seriesIds) + " RETURN k.aim")) {
                while (result.hasNext()) {
                    Map<String, Object> row = result.next();
                    for (Map.Entry<String, Object> column : row.entrySet()) {
                        String value = (String) column.getValue();
                        if (value.equals("null"))
                            continue;
                        String terms[] = value.split(" ");
                        for (int j = 0; j < terms.length; j++) {
                            String term = "[购车目的]" + terms[j];
                            if (!termsFreq.containsKey(term)) {
                                termsFreq.put(term, 1);
                            } else {
                                termsFreq.put(term, termsFreq.get(term) + 1);
                            }
                            if (!totalTermsFreq.containsKey(term)) {
                                totalTermsFreq.put(term, 1);
                            } else {
                                totalTermsFreq.put(term, totalTermsFreq.get(term) + 1);
                            }
                        }
                    }
                }
            }
            clusterTermsFreq.add(termsFreq);
        }
        clusterTermsFreq.add(totalTermsFreq);
        return clusterTermsFreq;
    }

    public static List<Integer> queryUserNumOfClusters(List<List<String>> clusterSeriesIds, GraphDatabaseService db) {
        List<Integer> clusterUserNums = new ArrayList<>();
        for (int i = 0; i < clusterSeriesIds.size(); i++) {
            List<String> seriesIds = clusterSeriesIds.get(i);
            try (Transaction ignored = db.beginTx();
                 Result result = db.execute("MATCH (s:Series)<-[r:Like]-(u:User) where s.seriesId in "
                         + list2String(seriesIds) + " return count(distinct u)")) {
                while (result.hasNext()) {
                    Map<String, Object> row = result.next();
                    for (Map.Entry<String, Object> column : row.entrySet()) {
                        String value = column.getValue().toString();
                        clusterUserNums.add(Integer.parseInt(value));
                    }
                }
            }
        }
        return clusterUserNums;
    }

    public static List<Integer> queryKoubeiNumOfClusters(List<List<String>> clusterSeriesIds, GraphDatabaseService db) {
        List<Integer> clusterKoubeiNums = new ArrayList<>();
        for (int i = 0; i < clusterSeriesIds.size(); i++) {
            List<String> seriesIds = clusterSeriesIds.get(i);
            try (Transaction ignored = db.beginTx();
                 Result result = db.execute("MATCH (series:Series)-[r:Has]->(style:Style)<-[:About]-(k:Koubei) where series.seriesId in "
                         + list2String(seriesIds) + " return count(distinct k)")) {
                while (result.hasNext()) {
                    Map<String, Object> row = result.next();
                    for (Map.Entry<String, Object> column : row.entrySet()) {
                        String value = column.getValue().toString();
                        clusterKoubeiNums.add(Integer.parseInt(value));
                    }
                }
            }
        }
        return clusterKoubeiNums;
    }

    public static List<String> querySeriesAttrBySeriesIds(String[] seriesIds, GraphDatabaseService db) {
        List<String> attrList = new ArrayList<>();
        try (Transaction ignored = db.beginTx();
             Result result = db.execute("match (s:Series)-[Is]->(a:SeriesAttr) where s.seriesId in "
                     + array2String(seriesIds) + " RETURN a.attr")) {
            while (result.hasNext()) {
                Map<String, Object> row = result.next();
                for (Map.Entry<String, Object> column : row.entrySet()) {
                    String value = (String) column.getValue();
                    if (value.contains("品牌"))
                        continue;
                    if (!attrList.contains(value))
                        attrList.add(value);
                }
            }
        }
        System.out.println("Query series attribution finished!");
        return attrList;
    }

    public static List<String> querySeriesByAttr(List<String> attrList, GraphDatabaseService db) {
        List<String> typeList = new ArrayList<>();
        List<String> priceList = new ArrayList<>();
        List<String> oilCostList = new ArrayList<>();
        for (int i = 0; i < attrList.size(); i++) {
            String attr = attrList.get(i);
            if (attr.contains("车型")) {
                typeList.add(attr.replace("[车型]", ""));
            } else if (attr.contains("价格")) {
                priceList.add(attr);
            } else if (attr.contains("工信部")) {
                oilCostList.add(attr.replace("[工信部综合油耗(L/100km)]", ""));
            }
        }
        List<String> seriesList = new ArrayList<>();
        String cypher = "match (s:Series)-[:Is]->(a:SeriesAttr) where s.seriesType in " + list2String(typeList);
        if (priceList.size() > 0) {
            cypher += " and a.attr in " + list2String(priceList);
        }
        if (oilCostList.size() > 0) {
            cypher += " and s.oilCostRange in " + list2String(oilCostList);
        }
        cypher += " RETURN DISTINCT s.seriesId";
        try (Transaction ignored = db.beginTx();
             Result result = db.execute(cypher)) {
            while (result.hasNext()) {
                Map<String, Object> row = result.next();
                for (Map.Entry<String, Object> column : row.entrySet()) {
                    String value = (String) column.getValue();
                    seriesList.add(value);
                }
            }
        }
        System.out.println("Query all matched serieses finished!");
        return seriesList;
    }
}
