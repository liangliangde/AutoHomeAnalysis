package com.servlet;

import com.data.query.QueryFromNeo4j;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * Created by llei on 16-2-25.
 */
public class GetKmeansResultServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=UTF-8");

        String seriesIds[] = req.getParameter("seriesIds").toString().split(",");
//        String[] seriesIds = {"633", "639", "874", "66", "792", "364", "530", "2987"};
        System.out.println(seriesIds[0]);
        String kValue = req.getParameter("k").toString();
        System.out.println(kValue);

        Map<String, int[]> userSeriesIdMap = QueryFromNeo4j.queryUserBySeriesId(seriesIds);
        List<List<String>> cluster = QueryFromNeo4j.queryUserClusters(userSeriesIdMap, seriesIds, 10);
        String seriesInfo = QueryFromNeo4j.querySeriesById(seriesIds);
        String clusterInfo = QueryFromNeo4j.getClusterInfo(cluster);
        String collectDetailInfo = QueryFromNeo4j.getCollectDetailInfo(cluster, seriesIds, userSeriesIdMap);

        PrintWriter toClient = resp.getWriter();
        StringBuffer kmeansResult = new StringBuffer();
        kmeansResult.append(seriesInfo).append("###").append(clusterInfo).append("###").append(collectDetailInfo);
        toClient.print("fdasfdsaf");
//        toClient.print(kmeansResult.toString());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }
}
