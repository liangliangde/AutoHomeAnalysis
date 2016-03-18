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
public class GetDetailSaleOfSeriesServlet extends HttpServlet {
    private static String baseURL = "/home/llei/IdeaProjects/autohome/auto_home.db";

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=UTF-8");
        String seriesNames[] = req.getParameter("seriesNames").toString().replace(" ", "").split(",");
//        String[] seriesNames = new String[]{"宝来","福克斯","速腾","高尔夫","科鲁兹","朗行","朗逸","凯越"};
        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase(baseURL);
        List<String> style2SaleList = QueryFromNeo4j.queryDetailSaleOfSeries(seriesNames, db);
        db.shutdown();
        PrintWriter toClient = resp.getWriter();
        StringBuffer styleAndNum = new StringBuffer();
        for(String style2Sale : style2SaleList) {
            styleAndNum.append(style2Sale).append("\n");
        }
        toClient.print(styleAndNum.toString());
        System.out.println("get style sales ready!");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}
