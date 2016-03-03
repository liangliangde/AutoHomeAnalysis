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
        File file = new File("/home/llei/IdeaProjects/autohome/auto_data/series.csv");
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

    public static Map<String, String> styleId2SeriesId() throws IOException {
        Map<String, String> styleId2SeriesIdMap = new HashMap<>();
        File file = new File("/home/llei/IdeaProjects/autohome/auto_data/series_style.csv");
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
        File file = new File("/home/llei/IdeaProjects/autohome/auto_data/brand_series.csv");
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
        File file = new File("/home/llei/IdeaProjects/autohome/auto_data/brand.csv");
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            int split = lineTxt.indexOf(",");
            brandId2brandNameMap.put(lineTxt.substring(0, split), lineTxt.substring(split+1));
        }
        inputStreamReader.close();
        bufferedReader.close();
        return brandId2brandNameMap;
    }
}
