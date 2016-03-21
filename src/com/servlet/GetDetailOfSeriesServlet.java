package com.servlet;

import com.data.query.QueryFromNeo4j;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * Created by llei on 16-3-16.
 */
public class GetDetailOfSeriesServlet extends HttpServlet {
    private static String baseURL = "/home/llei/IdeaProjects/autohome/auto_home.db";

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=UTF-8");
        String seriesNames[] = req.getParameter("seriesNames").toString().replace(" ", "").split(",");
//        String[] seriesNames = new String[]{"宝来","福克斯","速腾","高尔夫","科鲁兹","朗行","朗逸","凯越"};
        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase(baseURL);

        List<String> style2SaleList = QueryFromNeo4j.queryDetailSaleOfSeries(seriesNames, db);//query style list and koubei num
        Map<String, List<String>> generalAttrMap = QueryFromNeo4j.queryGeneralAttrOfSeries(seriesNames, db);// query general attribution of series
        List<String> styleAttrList = QueryFromNeo4j.queryStyleAttrListOfSeries(seriesNames, db);// query styles' attribution of series
        List<String> seriesScoreList = QueryFromNeo4j.querySeriesScoreList(seriesNames, db);//query series' score
        List<String> seriesAimList = QueryFromNeo4j.querySeriesAimList(seriesNames, db);//query series' aim

        db.shutdown();

        PrintWriter toClient = resp.getWriter();
        StringBuffer result = new StringBuffer();
        for (String style2Sale : style2SaleList) {
            result.append(style2Sale).append("\n");
        }
        result.append("###");// use "###" to split style list and attribution of series
        for (Map.Entry<String, List<String>> entry : generalAttrMap.entrySet()) {
            String seriesName = entry.getKey();
            List<String> attrList = entry.getValue();
            for(String attr : attrList){
                result.append(seriesName).append(",").append(attr).append("\n");
            }
        }
        result.append("###");// use "###" to split
        for(String attr:styleAttrList){
            result.append(attr).append("\n");
        }
        result.append("###");// use "###" to split
        for(String seriesScore:seriesScoreList){
            result.append(seriesScore).append("\n");
        }
        result.append("###");// use "###" to split
        for(String seriesAim:seriesAimList){
            result.append(seriesAim).append("\n");
        }
        toClient.print(result.toString());
        System.out.println("get series detail ready!");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}
