package com.data.process;

import com.IO.IOProcess;
import com.algorithm.similarity.Similarity;
import com.data.query.QueryFromNeo4j;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by llei on 16-3-22.
 */
public class CreateMainGraphData_new {
    //select candidate series only by common users

    public static void main(String[] args) throws IOException {
        String[] seriesNames = {"宝马3系", "奥迪A4L"};
        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase(QueryFromNeo4j.getBaseURL());
        int candNum = 7;
        Double lowBound = 0.7;
        Map<String, Double> seriesLDASimMap = QueryFromNeo4j.querySimSeries(seriesNames, db, lowBound);
        Map<String, String[]> seriesName2DetailMap = getSeriesName2Detail(seriesNames, seriesLDASimMap);
        List<String> candList = getCandidate(seriesNames, seriesName2DetailMap, seriesLDASimMap, candNum);
        Map<String, int[]> users = QueryFromNeo4j.queryUserBySeriesName(candList.toArray(), db);
        createUsersCSV(candList, users);
        db.shutdown();
    }

    private static List<String> getCandidate(String[] seriesNames, Map<String, String[]> seriesName2DetailMap, Map<String, Double> seriesLDASimMap, int candNum) throws IOException {

        Map<String, Double> simBtTypesMap = VariousMap.getSimBtTypes();
        List<String> candList = new ArrayList<>();
        for (String name : seriesNames) {
            candList.add(name);
        }
        while (candList.size() < candNum) {
            Double maxSim = 0.0;
            String maxSimSeriesName = null;
            for (Map.Entry<String, String[]> entry : seriesName2DetailMap.entrySet()) {
                if (candList.contains(entry.getKey())) {
                    continue;
                }
                Double totalSim = calTotalSimilarity(seriesName2DetailMap, entry.getKey(), candList, seriesLDASimMap, simBtTypesMap);
                if (totalSim > maxSim) {
                    maxSim = totalSim;
                    maxSimSeriesName = entry.getKey();
                }
            }
            candList.add(maxSimSeriesName);
        }
        return candList;
    }

    private static Double calTotalSimilarity(Map<String, String[]> seriesName2DetailMap, String curSeriesName, List<String> candList, Map<String, Double> seriesLDASimMap, Map<String, Double> simBtTypesMap) {
        Double totalSim = 1.0;
        for (String candName : candList) {
            Double simLDA = seriesLDASimMap.get(candName + "," + curSeriesName);
            //price, type, oilcost
            String[] attr1 = seriesName2DetailMap.get(candName);
            Double minPrice1 = Double.parseDouble(attr1[0]);
            Double maxPrice1 = Double.parseDouble(attr1[1]);
            String type1 = attr1[2];
            Double oil1 = Double.parseDouble(attr1[3]);

            String[] attr2 = seriesName2DetailMap.get(curSeriesName);
            Double minPrice2 = Double.parseDouble(attr2[0]);
            Double maxPrice2 = Double.parseDouble(attr2[1]);
            String type2 = attr2[2];
            Double oil2 = Double.parseDouble(attr2[3]);

            Double[] v1 = {minPrice1 / (minPrice1 + minPrice2), maxPrice1 / (maxPrice1 + maxPrice2), oil1 / (oil1 + oil2)};
            Double[] v2 = {minPrice2 / (minPrice1 + minPrice2), maxPrice2 / (maxPrice1 + maxPrice2), oil2 / (oil1 + oil2)};
            Double simAttr = Similarity.cosSimilarity(v1, v2);

            Double simType = simBtTypesMap.get(type1 + "," + type2);

            System.out.println(simLDA + ", " + simAttr + ", " + simType);
            totalSim *= (simLDA * simAttr * simType);
        }
        return totalSim;
    }

    private static Map<String, String[]> getSeriesName2Detail(String[] seriesNames, Map<String, Double> seriesLDASimMap) throws IOException {
        Map<String, String> seriesId2DetailMap = VariousMap.seriesName2Detail();
        Map<String, String[]> map = new HashMap<>();
        List<String> seriesNameList = new ArrayList<>();
        for (String name : seriesNames) {
            seriesNameList.add(name);
        }
        for (Map.Entry<String, Double> entry : seriesLDASimMap.entrySet()) {
            String seriesName = entry.getKey().split(",")[1];
            if (!seriesNameList.contains(seriesName)) {
                seriesNameList.add(seriesName);
            }
        }
        for (String name : seriesNameList) {
            String[] detail = seriesId2DetailMap.get(name).split(",");
            String[] detail_new = new String[4];
            if (detail[0].equals("null") || detail[1].equals("null") || detail[2].equals("null") || detail[0].indexOf("-") < 0) {
                continue;
            }
            detail_new[0] = detail[0].substring(0, detail[0].indexOf("-"));
            detail_new[1] = detail[0].substring(detail[0].indexOf("-") + 1, detail[0].indexOf("万"));
            detail_new[2] = detail[1];
            detail_new[3] = detail[2];
            map.put(name, detail_new);
        }
        return map;
    }


    private static void createUsersCSV(List<String> candSeries, Map<String, int[]> users) throws IOException {
        StringBuffer str = new StringBuffer();
        str.append("userId,userName,gender,location,birthday,verfied,");
        int len = candSeries.size();
        for (int i = 0; i < len; i++) {
            str.append(candSeries.get(i));
            if (i < len - 1) {
                str.append(",");
            }
        }
        str.append("\n");
        for (Map.Entry<String, int[]> user : users.entrySet()) {
            str.append(user.getKey()).append(",");
            int[] vec = user.getValue();
            for (int i = 0; i < vec.length; i++) {
                str.append(vec[i]);
                if (i < vec.length - 1) {
                    str.append(",");
                }
            }
            str.append("\n");
        }
        IOProcess.writeFile("/home/llei/IdeaProjects/autohome/AutoHomeAnalysis_new/web/data/users.csv", str.toString());
        System.out.println("Create Users.csv finished!");
    }
}
