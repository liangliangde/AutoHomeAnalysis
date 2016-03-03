package com.data.process;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by llei on 16-3-2.
 */
public class MakeTotalUserLDAinput_UserasDoc {
    public static void main(String args[]) throws IOException {
        File file = new File("/home/llei/IdeaProjects/autohome/auto_data/user_series.csv");
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        FileOutputStream outSeries = new FileOutputStream("ldaresult/user_doc/LDAinput_all.txt");
        Map<String,List<String>> user_seriesMap = new HashMap<>();
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            String userId = lineTxt.substring(0,lineTxt.indexOf(","));
            String seriesId = lineTxt.substring(lineTxt.indexOf(",")+1);
            if(!user_seriesMap.containsKey(userId)){
                user_seriesMap.put(userId,new ArrayList<String>());
            }
            else{
                user_seriesMap.get(userId).add(seriesId);
            }
        }
        int docNum = 0;
        StringBuffer str = new StringBuffer();
        for(Map.Entry<String, List<String>> entry: user_seriesMap.entrySet()){
            if(entry.getValue().size()>4){
                docNum ++;
                for(int i=0;i<entry.getValue().size();i++){
                    str.append(entry.getValue().get(i)).append(" ");
                }
                str.append("\n");
            }
        }
        outSeries.write((docNum+"\n"+str.toString()).getBytes());
        inputStreamReader.close();
        bufferedReader.close();
        outSeries.close();
    }
}
