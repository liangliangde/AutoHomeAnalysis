package com.servlet;

import com.data.query.QueryFromNeo4j;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.IO.IOProcess.writeFile;

/**
 * Created by llei on 16-2-25.
 */
public class GetMainGraphDataServlet extends HttpServlet {

    private static String baseURL = "/home/llei/IdeaProjects/autohome/auto_home.db";
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=UTF-8");

        String seriesIds[] = req.getParameter("seriesIds").toString().replace(" ", "").split(",");
//        String[] seriesIds = {"633", "639"};
        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase(baseURL);
        List<String> attrList = QueryFromNeo4j.querySeriesAttrBySeriesIds(seriesIds, db);
        List<String> similarSeries = QueryFromNeo4j.querySeriesByAttr(attrList, db);
        int[][] comUsersMatrix = queryComUsersOfSimilarSeries(similarSeries, db);
        int candNum = 10;
        List<String> candSeries = selectCandSeries(comUsersMatrix, similarSeries, seriesIds, candNum);
        int[][] comUsersMatrixOfCand = getComUsersMatrixOfCand(candSeries, comUsersMatrix, similarSeries);


//        PrintWriter toClient = resp.getWriter();
//        StringBuffer kmeansResult = new StringBuffer();
//        kmeansResult.append(seriesInfo).append("###").append(clusterInfo).append("###").append(collectDetailInfo);
//        toClient.print(kmeansResult.toString());
        db.shutdown();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    private int[][] getComUsersMatrixOfCand(List<String> candSeries, int[][] comUsersMatrix, List<String> similarSeries) {
        int len = candSeries.size();
        int[][] comUsersMatrixOfCand = new int[len][len];
        for(int i=0;i<len;i++){
            for(int j=i+1;j<len;j++){
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
                int comUsers = QueryFromNeo4j.querComUsersOf2Series(similarSeries.get(i), similarSeries.get(j), db);
                comUsersMatrix[i][j] = comUsersMatrix[j][i] = comUsers;
            }
        }
        return comUsersMatrix;
    }
}
