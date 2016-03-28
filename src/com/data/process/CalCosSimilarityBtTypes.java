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
public class CalCosSimilarityBtTypes {
    public static void main(String args[]) throws IOException {
        List<String> seriesTypes = getSeriesTypes("/home/llei/IdeaProjects/autohome/AutoHomeAnalysis_new/ldaresult/seriesType_doc/LDADoc.txt");
        Map<String, Double[]> seriesTypeUserMap = getUserVec("/home/llei/IdeaProjects/autohome/AutoHomeAnalysis_new/ldaresult/seriesType_doc/model-final.theta", seriesTypes);
        String similarityList = calSimilarity(seriesTypeUserMap);
        IOProcess.writeFile("auto_data/similarityBtSeriesType.csv", similarityList);
    }

    private static String calSimilarity(Map<String, Double[]> seriesTypeUserMap) {
        StringBuffer str = new StringBuffer();
        for (Map.Entry<String, Double[]> entry1 : seriesTypeUserMap.entrySet()) {
            for (Map.Entry<String, Double[]> entry2 : seriesTypeUserMap.entrySet()) {
                Double sim = Similarity.cosSimilarity(entry1.getValue(), entry2.getValue());
                str.append(entry1.getKey()).append(",").append(entry2.getKey()).append(",").append(sim).append("\n");
            }
        }
        return str.toString();
    }


    private static List<String> getSeriesTypes(String path) throws IOException {
        List<String> seriesTypes = new ArrayList<>();
        File file = new File(path);
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            seriesTypes.add(lineTxt);
        }
        inputStreamReader.close();
        bufferedReader.close();
        return seriesTypes;
    }

    private static Map<String, Double[]> getUserVec(String path, List<String> seriesIds) throws IOException {
        Map<String, Double[]> seriesTypeUserMap = new HashMap<>();
        File file = new File(path);
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt = null;
        int i = 0;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            seriesTypeUserMap.put(seriesIds.get(i++), stringArr2Double(lineTxt.split(" ")));
        }
        inputStreamReader.close();
        bufferedReader.close();
        return seriesTypeUserMap;
    }

    private static Double[] stringArr2Double(String[] strArr) {
        Double[] doubleArr = new Double[strArr.length];
        for (int i = 0; i < strArr.length; i++) {
            doubleArr[i] = Double.parseDouble(strArr[i]);
        }
        return doubleArr;
    }
}
