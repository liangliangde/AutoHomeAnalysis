package com.data.process;

import com.IO.IOProcess;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by llei on 16-3-23.
 */
public class ExtractKeywordFromKoubei {

    private static Map<String, Integer> keywordCountMap;
    private static Map<String, Integer> keywordCountMap_filtered;
    private static Map<String, Integer> aspect2NumMap;
    private static Map<Integer, String> num2AspectMap;
    private static int maxi = 0;

    public static void main(String[] args) throws IOException {
        createAspectAndNumMap();
        keywordCountMap = new HashMap<>();
        keywordCountMap_filtered = new HashMap<>();
        Map<String, Map<String, Integer>> map = readFileWithoutSplit("auto_data/koubei.csv");
        filterKeywords();
        Map<String, Double[]> wordVector = getWordVector(map);
        showWordVector(wordVector);
    }

    private static void showWordVector(Map<String, Double[]> wordVector) throws IOException {
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
        StringBuffer str = new StringBuffer();
        for (Map.Entry<String, Double[]> entry : wordVector.entrySet()) {
            Double maxProp = getMaxProp(entry.getValue());
            if (maxProp < .6) continue;
            str.append(entry.getKey() + ":");
            for (int i = 0; i < entry.getValue().length; i++) {
                str.append(df.format(entry.getValue()[i]) + " ");
            }
            str.append("\n");
        }
        IOProcess.writeFile("auto_data/word2vector.csv", str.toString());
    }

    private static Double getMaxProp(Double[] value) {
        Double maxvalue = 0.0;
        for (int i = 0; i < value.length; i++) {
            if (maxvalue < value[i]) {
                maxvalue = value[i];
                maxi = i;
            }
        }
        return maxvalue;
    }


    private static void createAspectAndNumMap() {
        aspect2NumMap = new HashMap<>();
        aspect2NumMap.put("【外观】", 0);
        aspect2NumMap.put("【舒适性】", 1);
        aspect2NumMap.put("【操控】", 2);
        aspect2NumMap.put("【性价比】", 3);
        aspect2NumMap.put("【内饰】", 4);
        aspect2NumMap.put("【油耗】", 5);
        aspect2NumMap.put("【动力】", 6);
        aspect2NumMap.put("【空间】", 7);

        num2AspectMap = new HashMap<>();
        num2AspectMap.put(0, "【外观】");
        num2AspectMap.put(1, "【舒适性】");
        num2AspectMap.put(2, "【操控】");
        num2AspectMap.put(3, "【性价比】");
        num2AspectMap.put(4, "【内饰】");
        num2AspectMap.put(5, "【油耗】");
        num2AspectMap.put(6, "【动力】");
        num2AspectMap.put(7, "【空间】");
    }

    private static void filterKeywords() {
        for (Map.Entry<String, Integer> entry : keywordCountMap.entrySet()) {
            if (entry.getValue() > 100) {
                keywordCountMap_filtered.put(entry.getKey(), entry.getValue());
            }
        }
        keywordCountMap.clear();
    }

    private static Map<String, Double[]> getWordVector(Map<String, Map<String, Integer>> map) {
        Map<String, Double[]> wordVector = new HashMap<>();
        for (Map.Entry<String, Integer> entry : keywordCountMap_filtered.entrySet()) {
            String word = entry.getKey();
            int totalCount = entry.getValue();
            Double[] props = new Double[8];
            for (Map.Entry<String, Map<String, Integer>> entry2 : map.entrySet()) {
                if (entry2.getValue().containsKey(word)) {
                    int thisCount = entry2.getValue().get(word);
                    props[aspect2NumMap.get(entry2.getKey())] = (1.0 * thisCount) / totalCount;
                } else {
                    props[aspect2NumMap.get(entry2.getKey())] = 0.0;
                }
            }
            wordVector.put(word, props);
        }
        return wordVector;
    }

    //Map<aspect, Map<keyword, count>>
    public static Map<String, Map<String, Integer>> readFileWithoutSplit(String path) throws IOException {

        Map<String, Map<String, Integer>> map = new HashMap<>();
        map.put("【外观】", new HashMap<>());
        map.put("【舒适性】", new HashMap<>());
        map.put("【操控】", new HashMap<>());
        map.put("【性价比】", new HashMap<>());
        map.put("【内饰】", new HashMap<>());
        map.put("【油耗】", new HashMap());
        map.put("【动力】", new HashMap());
        map.put("【空间】", new HashMap());

        File brandFile = new File(path);
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(brandFile), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            for (Map.Entry<String, Map<String, Integer>> entry : map.entrySet()) {
                String aspect = entry.getKey();
                String[] keywords = extractAspect(lineTxt, aspect).split(" ");
                for (String keyword : keywords) {
                    if (keyword.equals(""))
                        continue;
                    Map<String, Integer> keywordMap = map.get(aspect);
                    if (!keywordMap.containsKey(keyword)) {
                        keywordMap.put(keyword, 1);
                    } else {
                        keywordMap.put(keyword, keywordMap.get(keyword) + 1);
                    }
                }
            }
        }
        inputStreamReader.close();
        bufferedReader.close();
        return map;
    }

    private static String extractAspect(String value, String aspect) {
        String content = "";
        StringBuffer result = new StringBuffer();
        int index = value.indexOf(aspect) + 1;
        if (index > aspect.length() - 1) {
            int index_end = value.indexOf('【', index);
            if (index_end > -1) {
                content = value.substring(index, index_end);
            } else {
                content = value.substring(index);
            }
        }
        if (!content.equals("")) {
            List<Term> parse = ToAnalysis.parse(content);
            for (Term term : parse) {
                if (term.getName().length() < 2) {
                    continue;
                }
                String str = term.toString();
                if (str.indexOf("/") > -1 && (str.substring(str.indexOf("/")).equals("/n") || str.substring(str.indexOf("/")).equals("/v"))) {
                    result.append(term.getName()).append(" ");
                    if (!keywordCountMap.containsKey(term.getName())) {
                        keywordCountMap.put(term.getName(), 1);
                    } else {
                        keywordCountMap.put(term.getName(), keywordCountMap.get(term.getName()) + 1);
                    }

                }
            }
        }
        return result.toString();
    }
}
