package com.servlet;

import com.data.process.VariousMap;
import com.data.query.QueryFromNeo4j;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by llei on 16-3-16.
 */
public class GetGeneralAttrOfSeriesServlet extends HttpServlet {
    private static String baseURL = "/home/llei/IdeaProjects/autohome/auto_home.db";
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=UTF-8");
//        String seriesIds[] = req.getParameter("seriesIds").toString().replace(" ", "").split(",");
        String[] seriesIds = {"66", "692"};
        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase(baseURL);
        Map<String, List<String>> generalAttrMap = QueryFromNeo4j.queryGeneralAttrOfSeries(seriesIds, db);
        db.shutdown();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}
