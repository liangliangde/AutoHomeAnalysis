package com.data.process;

import com.data.query.QueryFromNeo4j;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by llei on 16-3-3.
 */
public class MakeTotalUserLDAinput_SeriesTypeAsDoc {
    public static void main(String args[]) throws IOException {
        String types[] = {"微面", "紧凑型车", "中型SUV", "皮卡", "大型车",
                "小型车", "小型SUV", "中型车", "MPV", "紧凑型SUV", "轻客", "中大型车",
                "中大型SUV", "微型车", "跑车", "大型SUV", "微卡"};
        FileOutputStream outSeries = new FileOutputStream("ldaresult/seriesType_doc/LDAinput_all.txt");
        FileOutputStream outSeriesId = new FileOutputStream("ldaresult/seriesType_doc/LDADoc.txt");
        int docNum = types.length;
        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase(QueryFromNeo4j.getBaseURL());
        String str = QueryFromNeo4j.queryUsersOfType(types, db);
        outSeries.write((docNum + "\n" + str).getBytes());
        for (String type : types) {
            outSeriesId.write((type + "\n").getBytes());
        }
        outSeries.close();
        outSeriesId.close();
    }
}
