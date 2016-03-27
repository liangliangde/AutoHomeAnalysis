package com.data.process;

import com.IO.IOProcess;
import com.algorithm.similarity.Similarity;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by llei on 16-3-27.
 */
public class CalCosSimilarityBtSeries {
    public static void main(String args[]) throws IOException {
        int topicNum = 100;
        List<String> seriesIds = getSeriesIds("/home/llei/IdeaProjects/autohome/AutoHomeAnalysis_new/ldaresult/series_doc/LDADoc.txt");
        Map<String, Double[]> seriesIdUserMap = getUserVec("/home/llei/IdeaProjects/autohome/AutoHomeAnalysis_new/ldaresult/series_doc/model-final.theta", seriesIds);
        System.out.println("series number = " + seriesIdUserMap.keySet().size());
        String similarityList = calSimilarity(seriesIdUserMap);
        IOProcess.writeFile("auto_data/similarityBtSeries.csv", similarityList);
    }

    private static String calSimilarity(Map<String, Double[]> seriesIdUserMap) {
        StringBuffer str = new StringBuffer();
        for (Map.Entry<String, Double[]> entry1 : seriesIdUserMap.entrySet()) {
            for (Map.Entry<String, Double[]> entry2 : seriesIdUserMap.entrySet()) {
                if (entry1.getKey().compareTo(entry2.getKey()) > 0) {
                    Double sim = Similarity.cosSimilarity(entry1.getValue(), entry2.getValue());
                    str.append(entry1.getKey()).append(",").append(entry2.getKey()).append(",").append(sim).append("\n");
                }
            }
        }
        return str.toString();
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
