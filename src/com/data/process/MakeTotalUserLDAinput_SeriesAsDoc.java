package com.data.process;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by llei on 16-3-3.
 */
public class MakeTotalUserLDAinput_SeriesAsDoc {
    public static void main(String args[]) throws IOException {
        File file = new File("/home/llei/IdeaProjects/autohome/AutoHomeAnalysis_new/auto_data/user_series.csv");
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        FileOutputStream outSeries = new FileOutputStream("ldaresult/series_doc/LDAinput_all.txt");
        FileOutputStream outSeriesId = new FileOutputStream("ldaresult/series_doc/LDADoc.txt");
        Map<String,List<String>> series_userMap = new HashMap<>();
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            String userId = lineTxt.substring(0,lineTxt.indexOf(","));
            String seriesId = lineTxt.substring(lineTxt.indexOf(",")+1);
            if(!series_userMap.containsKey(seriesId)){
                series_userMap.put(seriesId,new ArrayList<>());
            }
            else{
                series_userMap.get(seriesId).add(userId);
            }
        }
        int docNum = 0;
        StringBuffer str = new StringBuffer();
        for(Map.Entry<String, List<String>> entry: series_userMap.entrySet()){
            if(entry.getValue().size()>9){
                docNum ++;
                for(int i=0;i<entry.getValue().size();i++){
                    str.append(entry.getValue().get(i)).append(" ");
                }
                str.append("\n");
                outSeriesId.write((entry.getKey() + "\n").getBytes());
            }
        }
        outSeries.write((docNum+"\n"+str.toString()).getBytes());
        inputStreamReader.close();
        bufferedReader.close();
        outSeries.close();
        outSeriesId.close();
    }
}
