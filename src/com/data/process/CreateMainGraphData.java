package com.data.process;

import com.IO.IOProcess;
import com.data.query.QueryFromNeo4j;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by llei on 16-3-22.
 */
public class CreateMainGraphData {
    //select candidate series only by common users

    private static String baseURL = "/home/llei/IdeaProjects/autohome/auto_home.db";
    public static void main(String[] args) throws IOException {
        String[] seriesIds = {"66"};
        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase(baseURL);
        List<String> attrList = QueryFromNeo4j.querySeriesAttrBySeriesIds(seriesIds, db);
        List<String> similarSeries = QueryFromNeo4j.querySeriesByAttr(attrList, db);
        int[][] comUsersMatrix = queryComUsersOfSimilarSeries(similarSeries, db);
        int candNum = 7;
        List<String> candSeries = selectCandSeries(comUsersMatrix, similarSeries, seriesIds, candNum);
        //        int[][] comUsersMatrixOfCand = getComUsersMatrixOfCand(candSeries, comUsersMatrix, similarSeries);
        Map<String, int[]> users = QueryFromNeo4j.queryUserBySeriesId(candSeries.toArray(), db);
        Map<String, String> seriesId2DetailMap = VariousMap.seriesId2Detail();
        createUsersCSV(candSeries, users, seriesId2DetailMap);
        db.shutdown();
    }
    private static void createUsersCSV(List<String> candSeries, Map<String, int[]> users, Map<String, String> seriesId2DetailMap) throws IOException {
        StringBuffer str = new StringBuffer();
        str.append("userId,userName,gender,location,birthday,verfied,");
        int len = candSeries.size();
        for (int i = 0; i < len; i++) {
            String seriesDetail = seriesId2DetailMap.get(candSeries.get(i));
            str.append(seriesDetail.substring(0, seriesDetail.indexOf(",")));
            if (i < len - 1) {
                str.append(",");
            }
        }
        str.append("\n");
        for (Map.Entry<String, int[]> user : users.entrySet()) {
            str.append(user.getKey()).append(",");
            int[] vec = user.getValue();
            for(int i=0;i<vec.length;i++){
                str.append(vec[i]);
                if(i<vec.length-1){
                    str.append(",");
                }
            }
            str.append("\n");
        }
        IOProcess.writeFile("/home/llei/IdeaProjects/autohome/AutoHomeAnalysis_new/web/data/users.csv", str.toString());
        System.out.println("Create Users.csv finished!");
    }

    private int[][] getComUsersMatrixOfCand(List<String> candSeries, int[][] comUsersMatrix, List<String> similarSeries) {
        int len = candSeries.size();
        int[][] comUsersMatrixOfCand = new int[len][len];
        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j < len; j++) {
                int s1 = similarSeries.indexOf(candSeries.get(i));
                int s2 = similarSeries.indexOf(candSeries.get(j));
                comUsersMatrixOfCand[i][j] = comUsersMatrixOfCand[j][i] = comUsersMatrix[s1][s2];
            }
        }
        return comUsersMatrixOfCand;
    }


    public static List<String> selectCandSeries(int[][] comUsersMatrix, List<String> similarSeries, String[] seriesIds, int candNum) {
        if (similarSeries.size() <= candNum) {
            return similarSeries;
        }
        List<Integer> candSeries = new ArrayList<>();
        for (int i = 0; i < seriesIds.length; i++) {
            candSeries.add(similarSeries.indexOf(seriesIds[i]));
        }
        while (candSeries.size() < candNum) {
            int maxDisi = 0;
            int maxDis = 0;
            for (int i = 0; i < similarSeries.size(); i++) {
                if (candSeries.contains(i)) {
                    continue;
                }
                int curDis = calDis(comUsersMatrix, i, candSeries);
                if (curDis > maxDis) {
                    maxDis = curDis;
                    maxDisi = i;
                }
            }
            candSeries.add(maxDisi);
        }
        List<String> candSeries2 = new ArrayList<>();
        for (int i = 0; i < candNum; i++) {
            candSeries2.add(similarSeries.get(candSeries.get(i)));
        }
        System.out.println("Select candidate series finished!");
        return candSeries2;
    }

    private static int calDis(int[][] comUsersMatrix, int curSeries, List<Integer> candSeries) {
        int dis = 1;
        for (int i = 0; i < candSeries.size(); i++) {
            dis *= comUsersMatrix[curSeries][candSeries.get(i)];
        }
        return dis;
    }

    public static int[][] queryComUsersOfSimilarSeries(List<String> similarSeries, GraphDatabaseService db) {
        int len = similarSeries.size();
        int[][] comUsersMatrix = new int[len][len];
        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j < len; j++) {
                int comUsers = QueryFromNeo4j.queryComUsersOf2Series(similarSeries.get(i), similarSeries.get(j), db);
                comUsersMatrix[i][j] = comUsersMatrix[j][i] = comUsers;
            }
        }
        System.out.println("Query common users finished!");
        return comUsersMatrix;
    }
}
