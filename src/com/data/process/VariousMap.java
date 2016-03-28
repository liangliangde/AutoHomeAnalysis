package com.data.process;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by llei on 16-3-3.
 */
public class VariousMap {
    public static Map<String, String> seriesId2Detail() throws IOException {
        Map<String, String> seriesId2DetailMap = new HashMap<>();
        File file = new File("auto_data/series.csv");
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            int split = lineTxt.indexOf(",");
            seriesId2DetailMap.put(lineTxt.substring(0, split), lineTxt.substring(split + 1));
        }
        inputStreamReader.close();
        bufferedReader.close();
        return seriesId2DetailMap;
    }

    public static Map<String, String> seriesName2Detail() throws IOException {
        //return price, type, oilcost
        Map<String, String> seriesId2DetailMap = new HashMap<>();
        File file = new File("auto_data/series.csv");
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            String[] series = lineTxt.split(",");
            seriesId2DetailMap.put(series[1], series[2] + "," + series[3] + "," + series[4]);
        }
        inputStreamReader.close();
        bufferedReader.close();
        return seriesId2DetailMap;
    }

    public static Map<String, String> seriesId2Num() throws IOException {
        Map<String, String> seriesId2NumMap = new HashMap<>();
        File file = new File("auto_data/series.csv");
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt = null;
        int i = 1;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            int split = lineTxt.indexOf(",");
            seriesId2NumMap.put(lineTxt.substring(0, split), "" + (i++));
        }
        inputStreamReader.close();
        bufferedReader.close();
        return seriesId2NumMap;
    }

    public static Map<String, String> num2SeriesId() throws IOException {
        Map<String, String> seriesId2NumMap = new HashMap<>();
        File file = new File("auto_data/series.csv");
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt = null;
        int i = 1;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            int split = lineTxt.indexOf(",");
            seriesId2NumMap.put("" + (i++), lineTxt.substring(0, split));
        }
        inputStreamReader.close();
        bufferedReader.close();
        return seriesId2NumMap;
    }

    public static Map<String, String> styleId2SeriesId() throws IOException {
        Map<String, String> styleId2SeriesIdMap = new HashMap<>();
        File file = new File("auto_data/series_style.csv");
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            int split = lineTxt.indexOf(",");
            styleId2SeriesIdMap.put(lineTxt.substring(split + 1), lineTxt.substring(0, split));
        }
        inputStreamReader.close();
        bufferedReader.close();
        return styleId2SeriesIdMap;
    }

    public static Map<String, String> seriesId2brandId() throws IOException {
        Map<String, String> seriesId2brandIdMap = new HashMap<>();
        File file = new File("auto_data/brand_series.csv");
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            int split = lineTxt.indexOf(",");
            seriesId2brandIdMap.put(lineTxt.substring(split + 1), lineTxt.substring(0, split));
        }
        inputStreamReader.close();
        bufferedReader.close();
        return seriesId2brandIdMap;
    }

    public static Map<String, String> brandId2brandName() throws IOException {
        Map<String, String> brandId2brandNameMap = new HashMap<>();
        File file = new File("auto_data/brand.csv");
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            int split = lineTxt.indexOf(",");
            brandId2brandNameMap.put(lineTxt.substring(0, split), lineTxt.substring(split + 1));
        }
        inputStreamReader.close();
        bufferedReader.close();
        return brandId2brandNameMap;
    }

    public static Map<String, String> attrName2AttrId() throws IOException {
        Map<String, String> attrName2AttrIdMap = new HashMap<>();
        File file = new File("auto_data/attrId_attrName.csv");
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            int split = lineTxt.indexOf(",");
            attrName2AttrIdMap.put(lineTxt.substring(split + 1), lineTxt.substring(0, split));
        }
        inputStreamReader.close();
        bufferedReader.close();
        return attrName2AttrIdMap;
    }

    public static Map<String, String> attrId2AttrName() throws IOException {
        Map<String, String> attrId2AttrNameMap = new HashMap<>();
        File file = new File("auto_data/attrId_attrName.csv");
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            int split = lineTxt.indexOf(",");
            attrId2AttrNameMap.put(lineTxt.substring(0, split), lineTxt.substring(split + 1));
        }
        inputStreamReader.close();
        bufferedReader.close();
        return attrId2AttrNameMap;
    }

    public static Map<String, String> attr2AttrId() throws IOException {
        Map<String, String> attr2AttrIdMap = new HashMap<>();
        File file = new File("auto_data/seriesAttr.csv");
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            int split = lineTxt.indexOf(",");
            attr2AttrIdMap.put(lineTxt.substring(split + 1), lineTxt.substring(0, split));
        }
        inputStreamReader.close();
        bufferedReader.close();
        return attr2AttrIdMap;
    }

    public static Map<String, String> seriesId2OilAttr() throws IOException {
        Map<String, String> seriesId2AttrMap = new HashMap<>();
        File file = new File("auto_data/seriesId_attr.csv");
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            if (lineTxt.contains("工信部")) {
                int split = lineTxt.indexOf(",");
                seriesId2AttrMap.put(lineTxt.substring(0, split), lineTxt.substring(split + 1));
            }
        }
        inputStreamReader.close();
        bufferedReader.close();
        return seriesId2AttrMap;
    }

    public static Map<String, String> chinese2Score() throws IOException {
        Map<String, String> chinese2ScoreMap = new HashMap<>();
        File file = new File("auto_data/Chinese2Score");
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "GB2312");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            String[] strs = lineTxt.split(" ");
            chinese2ScoreMap.put(strs[0], strs[1]);
        }
        inputStreamReader.close();
        bufferedReader.close();
        return chinese2ScoreMap;
    }

    public static Map<String, String> userMap() throws IOException {
        Map<String, String> userMap = new HashMap<>();
        File file = new File("auto_data/user.csv");
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "GB2312");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            int split = lineTxt.indexOf(",");
            userMap.put(lineTxt.substring(0, split), lineTxt.substring(split + 1));
        }
        inputStreamReader.close();
        bufferedReader.close();
        return userMap;
    }

    public static Map<String, String> city2ProvinceMap() throws IOException {
        Map<String, String> city2ProvinceMap = new HashMap<>();
        File file = new File("/home/llei/IdeaProjects/autohome/AutoHomeAnalysis_new/auto_data/city2Province.csv");
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            String province = lineTxt.split(":")[0];
            String[] cities = lineTxt.split(":")[1].split("　");
            for (String city : cities) {
                city2ProvinceMap.put(city.trim(), province);
            }
        }
        inputStreamReader.close();
        bufferedReader.close();
        return city2ProvinceMap;
    }

    public static Map<String, Double> getSimBtTypes() throws IOException {
        Map<String, Double> simBtTypesMap = new HashMap<>();
        File file = new File("auto_data/similarityBtSeriesType.csv");
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            String[] strs = lineTxt.split(",");
            String t1 = strs[0];
            String t2 = strs[1];
            simBtTypesMap.put(t1 + "," + t2, Double.parseDouble(strs[2]));
            simBtTypesMap.put(t2 + "," + t1, Double.parseDouble(strs[2]));
        }
        inputStreamReader.close();
        bufferedReader.close();
        return simBtTypesMap;
    }
}
