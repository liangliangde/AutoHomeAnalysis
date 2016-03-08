package com.data.process;

import com.data.query.QueryFromNeo4j;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by llei on 16-3-4.
 */
public class ExtractKeyTerms {

    private static String baseURL = "/home/llei/IdeaProjects/autohome/auto_home.db";

    public static List<List<String>> extractTerm(List<List<String>> clusters) {
        GraphDatabaseService db = new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder(baseURL)
                .newGraphDatabase();
        int totalSeriesSize = countTotalSeriesSize(clusters);
        List<List<String>> terms = extractTermOfSeries(clusters, totalSeriesSize, db);
        List<List<String>> termsOfKoubei = extractTermOfKoubei(clusters, totalSeriesSize, db);
        List<List<String>> termsOfUsers = extractTermOfUsers(clusters, db);
        for(int i=0;i<terms.size();i++){
            terms.get(i).addAll(termsOfKoubei.get(i));
            terms.get(i).addAll(termsOfUsers.get(i));
        }
        db.shutdown();
        return terms;
    }

    private static List<List<String>> extractTermOfUsers(List<List<String>> clusters, GraphDatabaseService db) {
        //extract terms from users
        List<Integer> clusterUserNums = QueryFromNeo4j.queryUserNumOfClusters(clusters, db);
        int totalUserNums = 0;
        for(Integer i : clusterUserNums){
            totalUserNums += i;
        }
        List<List<String>> terms = new ArrayList<>();
        List<Map<String, Integer>> clusterTermsFreqUsers = QueryFromNeo4j.queryUserInfoBySeriesId(clusters, db);
        Map<String, Integer> totalTermsFreqUsers = clusterTermsFreqUsers.get(clusterTermsFreqUsers.size() - 1);
        for (int i = 0; i < clusterTermsFreqUsers.size() - 1; i++) {
            int userNums = clusterUserNums.get(i);
            Map<String, Integer> termsFreq = clusterTermsFreqUsers.get(i);
            List<String> term = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : termsFreq.entrySet()) {
                String attr = entry.getKey();
                int freq = entry.getValue();
                Double prop = ((1.0 * freq) / totalTermsFreqUsers.get(attr)) / ((1.0 * userNums) / totalUserNums);
                if(attr.contains("所在地")) {
                    if (userNums > 50 && freq > userNums * 0.1 && prop > 1.3) {
                        term.add(attr + "," + prop + "\n");
                    }
                }
                else if(attr.contains("年龄段")) {
                    if (userNums > 50 && freq > userNums * 0.05 && prop > 1.3 && freq > 5) {
                        term.add(attr + "," + prop + "\n");
                    }
                }
                else if(attr.contains("性别")) {
                    if (userNums > 50 && prop > 1.3 && freq > 5) {
                        term.add(attr + "," + prop + "\n");
                    }
                }
            }
            terms.add(term);
        }
        return terms;
    }

    private static List<List<String>> extractTermOfKoubei(List<List<String>> clusters, int totalSeriesSize, GraphDatabaseService db) {
        //extract terms from koubei
        List<Integer> clusterKoubeiNums = QueryFromNeo4j.queryKoubeiNumOfClusters(clusters, db);
        int totalKoubeiNums = 0;
        for(Integer i : clusterKoubeiNums){
            totalKoubeiNums += i;
        }
        List<List<String>> terms = new ArrayList<>();
        List<Map<String, Integer>> clusterTermsFreqKoubei = QueryFromNeo4j.queryKoubeiBySeriesId(clusters, db);
        Map<String, Integer> totalTermsFreqKoubei = clusterTermsFreqKoubei.get(clusterTermsFreqKoubei.size() - 1);
        for (int i = 0; i < clusterTermsFreqKoubei.size() - 1; i++) {
            int koubeiNums = clusterKoubeiNums.get(i);
            Map<String, Integer> termsFreq = clusterTermsFreqKoubei.get(i);
            List<String> term = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : termsFreq.entrySet()) {
                String attr = entry.getKey();
                int freq = entry.getValue();
                Double prop = ((1.0 * freq) / totalTermsFreqKoubei.get(attr)) / ((1.0 * koubeiNums) / totalKoubeiNums);
                if (koubeiNums > 50 && freq > koubeiNums * 0.3 && prop > 1.2 && freq > 5) {
                    term.add(attr + "," + prop + "\n");
                }
            }
            terms.add(term);
        }
        return terms;
    }

    private static List<List<String>> extractTermOfSeries(List<List<String>> clusters, int totalSeriesSize, GraphDatabaseService db) {
        //extract terms from series attribution
        List<List<String>> terms = new ArrayList<>();
        List<Map<String, Integer>> clusterTermsFreqSeries = QueryFromNeo4j.querySeriesAttrById(clusters, db);
        Map<String, Integer> totalTermsFreqSeries = clusterTermsFreqSeries.get(clusterTermsFreqSeries.size() - 1);
        for (int i = 0; i < clusterTermsFreqSeries.size() - 1; i++) {
            int clusterSize = clusters.get(i).size();
            Map<String, Integer> termsFreq = clusterTermsFreqSeries.get(i);
            List<String> term = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : termsFreq.entrySet()) {
                String attr = entry.getKey();
                int freq = entry.getValue();
                Double prop = ((1.0 * freq) / totalTermsFreqSeries.get(attr)) / ((1.0 * clusterSize) / totalSeriesSize);
                if (freq > clusterSize * 0.1 && prop > 2.3 && freq > 2) {
                    term.add(attr + "," + prop + "\n");
                }
            }
            terms.add(term);
        }
        return terms;
    }

    private static int countTotalSeriesSize(List<List<String>> clusters) {
        int totalSeriesSize = 0;
        for (int i = 0; i < clusters.size(); i++) {
            totalSeriesSize += clusters.get(i).size();
        }
        return totalSeriesSize;
    }
}
