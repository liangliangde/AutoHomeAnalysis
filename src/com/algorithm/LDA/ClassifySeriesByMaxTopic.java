package com.algorithm.LDA;

import com.IO.IOProcess;
import com.data.process.ShowCluster;
import com.data.process.VariousMap;
import com.data.query.QueryFromNeo4j;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.data.process.ExtractKeyTerms.extractTerm;

/**
 * Created by llei on 16-3-3.
 */
public class ClassifySeriesByMaxTopic {
    public static void main(String args[]) throws IOException {
        int topicNum = 8;
        List<String> seriesIds = getSeriesIds("src/com/algorithm/ldaresult/series_doc/LDADoc.txt");
        Map<String, Double[]> seriesIdUserMap = getUserVec("src/com/algorithm/ldaresult/series_doc/model-01000.theta", seriesIds);
        System.out.println("series number = " + seriesIdUserMap.keySet().size());
        List<List<String>> cluster = assignToTopic(seriesIdUserMap, topicNum);
        List<List<String>> terms = extractTerm(cluster);
        String content = ShowCluster.getFinalCluster(cluster, terms);
        IOProcess.writeFile("src/com/algorithm/clusterresult/lda_maxtopic_result", content);
    }

    private static List<List<String>> assignToTopic(Map<String, Double[]> seriesIdUserMap, int topicNum) {
        List<List<String>> cluster = new ArrayList<>();
        for (int i = 0; i < topicNum; i++) {
            cluster.add(new ArrayList<>());
        }
        for (Map.Entry<String, Double[]> entry : seriesIdUserMap.entrySet()) {
            Double[] vec = entry.getValue();
            int maxTopic = 0;
            Double maxPro = 0.0;
            for (int i = 0; i < vec.length; i++) {
                if (vec[i] > maxPro) {
                    maxPro = vec[i];
                    maxTopic = i;
                }
            }
            cluster.get(maxTopic).add(entry.getKey());
        }
        return cluster;
    }

    private static List<String> getSeriesIds(String path) throws IOException {
        List<String> seriesIds = new ArrayList<>();
        File file = new File(path);
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            seriesIds.add(lineTxt);
        }
        inputStreamReader.close();
        bufferedReader.close();
        return seriesIds;
    }

    private static Map<String, Double[]> getUserVec(String path, List<String> seriesIds) throws IOException {
        Map<String, Double[]> seriesIdUserMap = new HashMap<>();
        File file = new File(path);
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt = null;
        int i = 0;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            seriesIdUserMap.put(seriesIds.get(i++), stringArr2Double(lineTxt.split(" ")));
        }
        inputStreamReader.close();
        bufferedReader.close();
        return seriesIdUserMap;
    }

    private static Double[] stringArr2Double(String[] strArr) {
        Double[] doubleArr = new Double[strArr.length];
        for (int i = 0; i < strArr.length; i++) {
            doubleArr[i] = Double.parseDouble(strArr[i]);
        }
        return doubleArr;
    }
}
