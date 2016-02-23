package com.data.process;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by llei on 16-1-25.
 */
public class BrandlistParser {
    public static void main(String args[]) throws IOException {
        File brandFile = new File("/home/llei/pythonworkspace/myspider-spider/autohome/autohome_brand_list_all2");
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(brandFile), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        FileOutputStream outSeries = new FileOutputStream("auto_data/series.csv");
        FileOutputStream outBrand = new FileOutputStream("auto_data/brand.csv");
        FileOutputStream outBrand2Series = new FileOutputStream("auto_data/brand_series.csv");
        Map<String, String> seriesMap = new HashMap<>();
        Map<String, String> brandMap = new HashMap<>();
        Map<String, String> series2brandMap = new HashMap<>();
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            JSONTokener jsonTokener = new JSONTokener(lineTxt);
            JSONObject JSONObject;
            try {
                JSONObject = (JSONObject) jsonTokener.nextValue();
                if(!JSONObject.has("bid") || !JSONObject.has("cid"))
                    continue;
                String cid = JSONObject.getString("cid").trim();
                String series = JSONObject.getString("style").trim();
                seriesMap.put(cid, series);

                String bid = JSONObject.getString("bid").trim();
                String brand = JSONObject.getString("brand").trim();
                brandMap.put(bid, brand);
                series2brandMap.put(cid, bid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        for(Map.Entry<String,String> entry : seriesMap.entrySet()){
            StringBuffer cid2series = new StringBuffer();
            cid2series.append(entry.getKey()).append(",").append(entry.getValue()).append("\n");
            outSeries.write(cid2series.toString().getBytes());
        }
        for(Map.Entry<String,String> entry : brandMap.entrySet()){
            StringBuffer bid2brand = new StringBuffer();
            bid2brand.append(entry.getKey()).append(",").append(entry.getValue()).append("\n");
            outBrand.write(bid2brand.toString().getBytes());
        }
        for(Map.Entry<String,String> entry : series2brandMap.entrySet()){
            StringBuffer bid2series = new StringBuffer();
            bid2series.append(entry.getValue()).append(",").append(entry.getKey()).append("\n");
            outBrand2Series.write(bid2series.toString().getBytes());
        }
        System.out.print("finish");
        outSeries.close();
        outBrand.close();
        outBrand2Series.close();
    }
}
