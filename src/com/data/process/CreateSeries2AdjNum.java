package com.data.process;

import com.IO.IOProcess;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.io.*;
import java.util.*;

/**
 * Created by llei on 16-3-29.
 */
public class CreateSeries2AdjNum {

    private static Map<String, Integer> aspect2NumMap;
    private static Map<Integer, String> num2AspectMap;

    public static void main(String args[]) throws IOException {
        createAspectAndNumMap();
        Map<String, String> style2SeriesMap = VariousMap.styleId2SeriesId();
        Set<String> adjSet = VariousMap.getAdj();
        Map<String, Integer> series_aspect_word2Num = new TreeMap<>();
        File brandFile = new File("auto_data/koubei.csv");
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(brandFile), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        Map<String, Map<String, Integer>> map = new HashMap<>();
        map.put("【外观】", new HashMap<>());
        map.put("【舒适性】", new HashMap<>());
        map.put("【操控】", new HashMap<>());
        map.put("【性价比】", new HashMap<>());
        map.put("【内饰】", new HashMap<>());
        map.put("【油耗】", new HashMap<>());
        map.put("【动力】", new HashMap<>());
        map.put("【空间】", new HashMap<>());
        String lineTxt;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            String[] line = lineTxt.split(",");
            String seriesId = style2SeriesMap.get(line[1]);
            if (seriesId == null)
                continue;
            String koubei = line[16] + " " + line[19].replace("null", "");

            for (Map.Entry<String, Map<String, Integer>> entry : map.entrySet()) {
                String aspect = entry.getKey();
                String[] keywords_count = extractAspect(koubei, aspect, adjSet).split("###");
                for (String keywordcount : keywords_count) {
                    if (keywordcount.equals(""))
                        continue;
                    String word = keywordcount.split(",")[0];
                    int num = Integer.parseInt(keywordcount.split(",")[1]);
                    if (num == 1) {
                        continue;
                    }
                    String key = seriesId + "," + aspect + word;
                    if (!series_aspect_word2Num.containsKey(key)) {
                        series_aspect_word2Num.put(key, num);
                    } else {
                        series_aspect_word2Num.put(key, series_aspect_word2Num.get(key) + num);
                    }
                }
            }
        }
        inputStreamReader.close();
        bufferedReader.close();

        StringBuffer str = new StringBuffer();
        for (Map.Entry<String, Integer> entry : series_aspect_word2Num.entrySet()) {
            str.append(entry.getKey()).append(",").append(entry.getValue()).append("\n");
        }
        IOProcess.writeFile("auto_data/series_aspect_adj_num.csv", str.toString());
    }

    private static String extractAspect(String value, String aspect, Set<String> adjSet) {
        Map<String, Integer> keywordCountMap = new HashMap<>();
        String content = "";
        StringBuffer result = new StringBuffer();
        int index = value.indexOf(aspect) + aspect.length();
        if (index > aspect.length() - 1) {
            int index_end = value.indexOf('【', index);
            if (index_end > -1) {
                content = value.substring(index, index_end);
            } else {
                content = value.substring(index);
            }
        }
        int indexAddon = value.lastIndexOf(aspect) + aspect.length();
        if (index != indexAddon && index > aspect.length() - 1) {
            int indexAddon_end = value.indexOf('【', indexAddon);
            if (indexAddon_end > -1) {
                content += ("," + value.substring(indexAddon, indexAddon_end));
            } else {
                content += ("," + value.substring(indexAddon));
            }
        }

        if (!content.equals("")) {
            List<Term> parse = ToAnalysis.parse(content);
            for (Term term : parse) {
                if (term.getName().length() < 2) {
                    continue;
                }
                String str = term.toString();
                String wordType = str.substring(str.indexOf("/"));
                if (str.indexOf("/") > -1 && (wordType.equals("/a") || wordType.equals("/ad")) && adjSet.contains(term.getName())) {
                    if (!keywordCountMap.containsKey(term.getName())) {
                        keywordCountMap.put(term.getName(), 1);
                    } else {
                        keywordCountMap.put(term.getName(), keywordCountMap.get(term.getName()) + 1);
                    }

                }
            }
        }
        for (Map.Entry<String, Integer> entry : keywordCountMap.entrySet()) {
            result.append(entry.getKey()).append(",").append(entry.getValue()).append("###");
        }
        return result.toString();
    }

    private static void createAspectAndNumMap() {
        aspect2NumMap = new HashMap<>();
        aspect2NumMap.put("外观", 0);
        aspect2NumMap.put("舒适性", 1);
        aspect2NumMap.put("操控", 2);
        aspect2NumMap.put("性价比", 3);
        aspect2NumMap.put("内饰", 4);
        aspect2NumMap.put("油耗", 5);
        aspect2NumMap.put("动力", 6);
        aspect2NumMap.put("空间", 7);

        num2AspectMap = new HashMap<>();
        num2AspectMap.put(0, "appearance");
        num2AspectMap.put(1, "comfort");
        num2AspectMap.put(2, "control");
        num2AspectMap.put(3, "costPerform");
        num2AspectMap.put(4, "interior");
        num2AspectMap.put(5, "oil");
        num2AspectMap.put(6, "power");
        num2AspectMap.put(7, "space");
    }
}
