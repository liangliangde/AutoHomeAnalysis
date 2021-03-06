package com.data.query;

import com.algorithm.Kmeans.KmeansFor01Vec;
import com.data.process.VariousMap;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
//        String[] seriesIds = {"633", "639"};
        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase(baseURL);
//        List<String> attrList = QueryFromNeo4j.querySeriesAttrBySeriesIds(seriesIds, db);
//        List<String> similarSeries = QueryFromNeo4j.querySeriesByAttr(seriesIds, attrList, db);
//        int[][] comUsersMatrix = queryComUsersOfSimilarSeries(similarSeries, db);
//        int candNum = 10;
//        List<String> candSeries = selectCandSeries(comUsersMatrix, similarSeries, seriesIds, candNum);
////        System.out.print(list2String(candSeries));
        List<String> list = querySeriesAimList(new String[]{"宝马3系","奥迪A4L","奔驰C级","凯迪拉克ATS-L","英菲尼迪Q50L","沃尔沃S60L","沃尔沃V60"}, db);
        for(String str : list){
            System.out.println(str);
        }
        db.shutdown();
    }

    public static String getBaseURL() {
        return baseURL;
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

    public static Map<String, int[]> queryUserBySeriesName(Object[] seriesNames, GraphDatabaseService db) {
        Map<String, int[]> userSeriesIdMap = new HashMap<>();
        Map<Object, Integer> seriesNames2Num = new HashMap<>();
        for (int i = 0; i < seriesNames.length; i++) {
            seriesNames2Num.put(seriesNames[i], i);
        }

        try (Transaction ignored = db.beginTx();
             Result result = db.execute("MATCH (u)-[r:Like]->(s:Series) where u.location <> '澳门' and s.seriesName in " + array2String(seriesNames)
                     + " RETURN s.seriesName + ',' + u.userId + ',' + u.userName + ',' + u.gender + ',' + u.location + ',' + u.birthday + ',' + u.verified")) {
            while (result.hasNext()) {
                Map<String, Object> row = result.next();
                for (Map.Entry<String, Object> column : row.entrySet()) {
                    String record = (String) column.getValue();
                    String seriesName = record.substring(0, record.indexOf(','));
                    String userId = record.substring(record.indexOf(',') + 1);
                    if (!userSeriesIdMap.containsKey(userId)) {
                        int[] featureArr = new int[seriesNames.length];
                        featureArr[seriesNames2Num.get(seriesName)] = 1;
                        userSeriesIdMap.put(userId, featureArr);
                    } else {
                        userSeriesIdMap.get(userId)[seriesNames2Num.get(seriesName)] = 1;
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
             Result result = db.execute("match (s1:Series)-[r:Colike]-(s2:Series) " +
                     "where s1.seriesId > s2.seriesId and r.userNum>=" + num + " return s1.seriesId+','+s2.seriesId")) {
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

    public static Map<String, List<String>> queryGeneralAttrOfSeries(String[] seriesNames, GraphDatabaseService db) {
        Map<String, List<String>> generalAttrMap = new HashMap<>();
        for (int i = 0; i < seriesNames.length; i++) {
            String seriesName = seriesNames[i];
            generalAttrMap.put(seriesName, new ArrayList<>());
            String cypher = "match (se:Series{seriesName:'" + seriesName + "'})-[:Has]->(s:Style)-[r:Is]->(a:StyleAttr)" +
                    " with a,count(r) as num order by num desc return a.attr+','+num";
            int styleNum = 0;
            try (Transaction ignored = db.beginTx();
                 Result result = db.execute(cypher)) {
                while (result.hasNext()) {
                    Map<String, Object> row = result.next();
                    for (Map.Entry<String, Object> column : row.entrySet()) {
                        String value = (String) column.getValue();
                        int split = value.indexOf(",");
                        String attr = value.substring(0, split);
                        int num = Integer.parseInt(value.substring(split + 1));
                        styleNum = styleNum == 0 ? num : styleNum;
                        if (num > styleNum * 0.6) {
//                            List<String> list = generalAttrMap.get(seriesId);
//                            list.add(attr);
//                            generalAttrMap.put(seriesId, list);
                            generalAttrMap.get(seriesName).add(attr);
                        }
                    }
                }
            }
        }
        return generalAttrMap;
    }

    public static List<String> queryStyleAttrListOfSeries(String[] seriesNames, GraphDatabaseService db) {
        List<String> styleAttrList = new ArrayList<>();
        for (int i = 0; i < seriesNames.length; i++) {
            String seriesName = seriesNames[i];
            String cypher = "match (se:Series{seriesName:'" + seriesName + "'})-[:Has]->(s:Style)-[r:Is]->(a:StyleAttr)" +
                    " return se.seriesName + ',' + s.styleId + ',' + a.attr";
            try (Transaction ignored = db.beginTx();
                 Result result = db.execute(cypher)) {
                while (result.hasNext()) {
                    Map<String, Object> row = result.next();
                    for (Map.Entry<String, Object> column : row.entrySet()) {
                        String value = (String) column.getValue();
                        styleAttrList.add(value);
                    }
                }
            }
        }
        return styleAttrList;
    }

    public static List<String> queryDetailSaleOfSeries(String seriesNames[], GraphDatabaseService db) {
        List<String> style2SaleList = new ArrayList<>();
        for (int i = 0; i < seriesNames.length; i++) {
            String seriesName = seriesNames[i];
            boolean hasResult = false;
            try (Transaction ignored = db.beginTx();
                 Result result = db.execute("match (se:Series{seriesName:'" + seriesName + "'})-[:Has]->(s:Style)<-[:About]-(k:Koubei) " +
                         "with s, count(s) as num order by num desc return s.styleId+','+s.styleName+','+num")) {
                while (result.hasNext()) {
                    hasResult = true;
                    Map<String, Object> row = result.next();
                    for (Map.Entry<String, Object> column : row.entrySet()) {
                        String value = (String) column.getValue();
                        style2SaleList.add(seriesName + "," + value);
                    }
                }
            }
            if (hasResult == false) {
                try (Transaction ignored = db.beginTx();
                     Result result = db.execute("match (se:Series{seriesName:'" + seriesName + "'})-[:Has]->(s:Style) " +
                             "return s.styleId+','+s.styleName+',0'")) {
                    while (result.hasNext()) {
                        Map<String, Object> row = result.next();
                        for (Map.Entry<String, Object> column : row.entrySet()) {
                            String value = (String) column.getValue();
                            style2SaleList.add(seriesName + "," + value);
                        }
                    }
                }
            }
        }
        return style2SaleList;
    }

    public static List<String> queryBoughtTimeOfStyle(String seriesNames[], GraphDatabaseService db) {
        List<String> list = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("0.00");
        for (int i = 0; i < seriesNames.length; i++) {
            String seriesName = seriesNames[i];
            Map<String, Double> totalPriceMap = new HashMap<>();
            Map<String, Integer> totalNumMap = new HashMap<>();
            try (Transaction ignored = db.beginTx();
                 Result result = db.execute("match (se:Series{seriesName:'" + seriesName + "'})-[:Has]->(s:Style)<-[:About]-(k:Koubei) " +
                         "return s.styleId+','+k.boughtTime+','+k.price")) {
                while (result.hasNext()) {
                    Map<String, Object> row = result.next();
                    for (Map.Entry<String, Object> column : row.entrySet()) {
                        String value = (String) column.getValue();
                        String[] valueArr = value.split(",");
                        String styleId = valueArr[0];
                        String boughtTime = valueArr[1];
                        Double price = Double.parseDouble(valueArr[2].substring(0, valueArr[2].indexOf(" ")));
                        int year = Integer.parseInt(boughtTime.substring(0, 4));
                        if (year < 2015)
                            continue;
                        int month = Integer.parseInt(boughtTime.substring(boughtTime.indexOf("年") + 1, boughtTime.indexOf("月")));
                        int time2Num = (year - 2015) * 12 + month;
                        String key = styleId + "," + time2Num;
                        if (!totalNumMap.containsKey(key)) {
                            totalNumMap.put(key, 1);
                            totalPriceMap.put(key, price);
                        } else {
                            totalNumMap.put(key, totalNumMap.get(key) + 1);
                            totalPriceMap.put(key, totalPriceMap.get(key) + price);
                        }
                    }
                }
            }
            for (Map.Entry<String, Double> entry : totalPriceMap.entrySet()) {
                list.add(seriesName + "," + entry.getKey() + "," + df.format(entry.getValue() / totalNumMap.get(entry.getKey()))
                        + "," + totalNumMap.get(entry.getKey()));
            }
        }
        return list;
    }

    public static List<String> queryAttrOfStyle(String styleId, GraphDatabaseService db) {
        List<String> attrList = new ArrayList<>();
        try (Transaction ignored = db.beginTx();
             Result result = db.execute("match (s:Style{styleId:'" + styleId + "'})-[:Is]->(a:StyleAttr) " +
                     "return a.attr")) {
            while (result.hasNext()) {
                Map<String, Object> row = result.next();
                for (Map.Entry<String, Object> column : row.entrySet()) {
                    String value = (String) column.getValue();
                    attrList.add(value);
                }
            }
        }
        return attrList;
    }

    public static List<String> querySeriesScoreList(String[] seriesNames, GraphDatabaseService db) {
        List<String> seriesScoreList = new ArrayList<>();
        for (int i = 0; i < seriesNames.length; i++) {
            String seriesName = seriesNames[i];
            try (Transaction ignored = db.beginTx();
                 Result result = db.execute("match (se:Series{seriesName:'" + seriesName + "'})-[:Has]->(s:Style)<-[:About]-(k:Koubei) " +
                         "return avg(toInt(k.costPerform))+','+avg(toInt(k.control))+','+avg(toInt(k.space))+','+avg(toInt(k.comfort))" +
                         "+','+avg(toInt(k.interior))+','+avg(toInt(k.oil))+','+avg(toInt(k.appearence))+','+avg(toInt(k.power))")) {
                while (result.hasNext()) {
                    Map<String, Object> row = result.next();
                    for (Map.Entry<String, Object> column : row.entrySet()) {
                        String value = (String) column.getValue();
                        seriesScoreList.add(seriesName + "," + value);
                    }
                }
            }
        }
        return seriesScoreList;
    }

    public static List<String> querySeriesStyleScoreList(String[] seriesNames, GraphDatabaseService db) {
        List<String> seriesScoreList = new ArrayList<>();
        for (int i = 0; i < seriesNames.length; i++) {
            String seriesName = seriesNames[i];
            try (Transaction ignored = db.beginTx();
                 Result result = db.execute("match (se:Series{seriesName:'" + seriesName + "'})-[:Has]->(s:Style)<-[:About]-(k:Koubei) " +
                         "return se.seriesName+','+s.styleId+','+avg(toInt(k.costPerform))+','+avg(toInt(k.control))+','+avg(toInt(k.space))+','+avg(toInt(k.comfort))" +
                         "+','+avg(toInt(k.interior))+','+avg(toInt(k.oil))+','+avg(toInt(k.appearence))+','+avg(toInt(k.power))")) {
                while (result.hasNext()) {
                    Map<String, Object> row = result.next();
                    for (Map.Entry<String, Object> column : row.entrySet()) {
                        String value = (String) column.getValue();
                        seriesScoreList.add(value);
                    }
                }
            }
        }
        return seriesScoreList;
    }

    public static List<String> queryKoubeiDetailofSeries(String[] seriesNames, String aspect, GraphDatabaseService db) {
        Map<String, String> eng2ChiAspectMap = new HashMap<>();
        eng2ChiAspectMap.put("外观", "appearence");
        eng2ChiAspectMap.put("舒适度", "comfort");
        eng2ChiAspectMap.put("操控", "control");
        eng2ChiAspectMap.put("性价比", "costPerform");
        eng2ChiAspectMap.put("内饰", "interior");
        eng2ChiAspectMap.put("油耗", "oil");
        eng2ChiAspectMap.put("动力", "power");
        eng2ChiAspectMap.put("空间", "space");

        List<String> koubeiDetailList = new ArrayList<>();
        for (int i = 0; i < seriesNames.length; i++) {
            String seriesName = seriesNames[i];
            try (Transaction ignored = db.beginTx();
                 Result result = db.execute("match (se:Series{seriesName:'" + seriesName + "'})-[:Has]->(s:Style)<-[:About]-(k:Koubei) " +
                         "return k." + eng2ChiAspectMap.get(aspect) + "+','+k.koubeiText")) {
                while (result.hasNext()) {
                    Map<String, Object> row = result.next();
                    for (Map.Entry<String, Object> column : row.entrySet()) {
                        String value = (String) column.getValue();
                        int split = value.indexOf(",");
                        String score = value.substring(0, split);
                        String aspectContent = getAspectContent(value.substring(split + 1), '【' + aspect + '】');
                        koubeiDetailList.add(aspectContent + "," + score);
                        System.out.println(score + "," + aspectContent);
                    }
                }
            }
        }
        return koubeiDetailList;
    }

    public static List<String> queryKoubeiBestOrWorstofSeries(String[] seriesNames, String aspect, GraphDatabaseService db) {

        List<String> koubeiDetailList = new ArrayList<>();
        for (int i = 0; i < seriesNames.length; i++) {
            String seriesName = seriesNames[i];
            try (Transaction ignored = db.beginTx();
                 Result result = db.execute("match (se:Series{seriesName:'" + seriesName + "'})-[:Has]->(s:Style)<-[:About]-(k:Koubei) " +
                         "return k.koubeiText")) {
                while (result.hasNext()) {
                    Map<String, Object> row = result.next();
                    for (Map.Entry<String, Object> column : row.entrySet()) {
                        String value = (String) column.getValue();
                        String aspectContent = getAspectContent(value, "【" + aspect + "】");
                        koubeiDetailList.add(aspectContent);
                        System.out.println(aspectContent);
                    }
                }
            }
        }
        return koubeiDetailList;
    }

    private static String getAspectContent(String value, String aspect) {
        String result = "null";
        int index = value.indexOf(aspect);
        if (index > -1) {
            int index_end = value.indexOf('【', index + 1);
            if (index_end > -1) {
                result = value.substring(index, index_end);
            } else {
                result = value.substring(index);
            }
        }
        return result;
    }

    public static List<String> querySeriesAimList(String[] seriesNames, GraphDatabaseService db) {
        List<String> seriesScoreList = new ArrayList<>();
        for (int i = 0; i < seriesNames.length; i++) {
            String seriesName = seriesNames[i];
            Map<String, Integer> aimPropMap = new HashMap<>();
            int koubeiNum = 0;
            try (Transaction ignored = db.beginTx();
                 Result result = db.execute("match (se:Series{seriesName:'" + seriesName + "'})-[:Has]->(s:Style)<-[:About]-(k:Koubei) return k.aim")) {
                while (result.hasNext()) {
                    koubeiNum++;
                    Map<String, Object> row = result.next();
                    for (Map.Entry<String, Object> column : row.entrySet()) {
                        String value = (String) column.getValue();
                        String aims[] = value.split(" ");
                        for (String aim : aims) {
                            if (!aimPropMap.containsKey(aim)) {
                                aimPropMap.put(aim, 1);
                            } else {
                                aimPropMap.put(aim, aimPropMap.get(aim) + 1);
                            }
                        }
                    }
                }
            }
            for (Map.Entry<String, Integer> entry : aimPropMap.entrySet()) {
                seriesScoreList.add(seriesName + ',' + entry.getKey() + ',' + (1.0 * entry.getValue()) / koubeiNum);
            }
        }
        return seriesScoreList;
    }

    public static List<String> querySeriesBoughtInfo(String[] seriesNames, GraphDatabaseService db) throws IOException {
        Map<String, String> city2ProvinceMap = VariousMap.city2ProvinceMap();

        List<String> seriesBoughtInfo = new ArrayList<>();
        for (int i = 0; i < seriesNames.length; i++) {
            String seriesName = seriesNames[i];
            try (Transaction ignored = db.beginTx();
                 Result result = db.execute("match (se:Series{seriesName:'" + seriesName +
                         "'})-[:Has]->(s:Style)<-[:About]-(k:Koubei)<-[:Release]-(u:User) " +
                         "return se.seriesName+','+u.location+','+k.boughtSite+','+k.price")) {
                while (result.hasNext()) {
                    Map<String, Object> row = result.next();
                    for (Map.Entry<String, Object> column : row.entrySet()) {
                        String value = (String) column.getValue();
                        if (value == null || value.equals("null"))
                            continue;
                        String[] info = value.split(",");
                        String boughtSite = info[2].trim();
                        String userLoc = info[1].trim();
                        if (boughtSite.equals("其他") || boughtSite.equals("其它") || userLoc.equals("其他") || userLoc.equals("其它")
                                || userLoc.equals("澳门") || userLoc.equals("澳门"))
                            continue;
                        if (!city2ProvinceMap.containsKey(boughtSite)) {
                            System.out.println(boughtSite);
                            continue;
                        }
                        String price = info[3].substring(0, info[3].indexOf(" "));
                        String value2 = info[0] + "," + userLoc + "," + city2ProvinceMap.get(boughtSite) + "," + price;
                        seriesBoughtInfo.add(value2);
                    }
                }
            }
        }
        return seriesBoughtInfo;
    }

    public static String queryUsersOfType(String[] types, GraphDatabaseService db) {
        StringBuffer str = new StringBuffer();
        for (String type : types) {
            try (Transaction ignored = db.beginTx();
                 Result result = db.execute("match (se:Series{seriesType:'" + type + "'})-[:Like]-(u:User) return distinct u.userId")) {
                while (result.hasNext()) {
                    Map<String, Object> row = result.next();
                    for (Map.Entry<String, Object> column : row.entrySet()) {
                        String value = (String) column.getValue();
                        str.append(value).append(" ");
                    }
                }
            }
            str.append("\n");
        }
        return str.toString();
    }

    public static Map<String, Double> querySimSeries(String[] seriesNames, GraphDatabaseService db, Double lowBound) {
        Map<String, Double> seriesSimMap = new HashMap<>();
        List<String> seriesList = new ArrayList<>();
        for (String name : seriesNames) {
            seriesList.add(name);
        }
        for (String seriesName : seriesNames) {
            try (Transaction ignored = db.beginTx();
                 Result result = db.execute("match (a:Series{seriesName:'" + seriesName + "'})-[s:Similarity]-(b:Series) where toFloat(s.similarity) > "
                         + lowBound + " return b.seriesName")) {
                while (result.hasNext()) {
                    Map<String, Object> row = result.next();
                    for (Map.Entry<String, Object> column : row.entrySet()) {
                        String value = ((String) column.getValue());
                        seriesList.add(value);
                    }
                }
            }
        }
        for (String s1 : seriesList) {
            for (String s2 : seriesList) {
                if (s1.compareTo(s2) > 0) {
                    try (Transaction ignored = db.beginTx();
                         Result result = db.execute("match (a:Series{seriesName:'" + s1 + "'})-[s:Similarity]-(b:Series{seriesName:'" + s2
                                 + "'}) return a.seriesName+','+b.seriesName+','+s.similarity")) {
                        while (result.hasNext()) {
                            Map<String, Object> row = result.next();
                            for (Map.Entry<String, Object> column : row.entrySet()) {
                                String value = ((String) column.getValue());
                                String strs[] = value.split(",");
                                seriesSimMap.put(strs[0] + "," + strs[1], Double.parseDouble(strs[2]));
                                seriesSimMap.put(strs[1] + "," + strs[0], Double.parseDouble(strs[2]));
                            }
                        }
                    }
                }
            }
        }
        return seriesSimMap;
    }

    public static List<String> getFocusOfSeries(String[] seriesNames, GraphDatabaseService db) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < seriesNames.length; i++) {
            String seriesName = seriesNames[i];
            try (Transaction ignored = db.beginTx();
                 Result result = db.execute("match (:Series{seriesName:'" + seriesName + "'})-[f:Focus]-(a:Aspect) " +
                         "return a.name+','+f.degree")) {
                while (result.hasNext()) {
                    Map<String, Object> row = result.next();
                    for (Map.Entry<String, Object> column : row.entrySet()) {
                        String value = (String) column.getValue();
                        list.add(seriesName + "," + value);
                    }
                }
            }
        }
        return list;
    }

    public static List<String> getAvgAgeOfBoughtUsersOfSeries(String[] seriesNames, GraphDatabaseService db) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < seriesNames.length; i++) {
            String seriesName = seriesNames[i];
            int totalAge = 0;
            int count = 0;
            try (Transaction ignored = db.beginTx();
                 Result result = db.execute("match (:Series{seriesName:'" + seriesName + "'})-[:Has]-(:Style)-[:About]-(Koubei)-[:Release]-(u:User) " +
                         "return u.birthday")) {
                while (result.hasNext()) {
                    Map<String, Object> row = result.next();
                    for (Map.Entry<String, Object> column : row.entrySet()) {
                        String value = (String) column.getValue();
                        if (!value.equals("null")) {
                            totalAge += (2016 - Integer.parseInt(value.substring(0, 4)));
                            count++;
                        }
                    }
                }
            }
            System.out.println(seriesName + ", " + 1.0 * totalAge / count);
        }
        return list;
    }
}
