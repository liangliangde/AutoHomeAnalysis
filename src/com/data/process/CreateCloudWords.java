package com.data.process;

import java.io.*;

/**
 * Created by llei on 16-5-31.
 */
public class CreateCloudWords {
    public static void main(String args[]) throws IOException {
        String seriesId = "78";
        String aspect = "动力";
        System.out.println(createCloudWords(seriesId, aspect));
    }

    private static String createCloudWords(String seriesId, String aspect) throws IOException {
        File brandFile = new File("auto_data/series_aspect_adj_num.csv");
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(brandFile), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt;
        StringBuffer result = new StringBuffer();
        while ((lineTxt = bufferedReader.readLine()) != null) {
            if(lineTxt.substring(0, lineTxt.indexOf(",")).equals(seriesId)){
                String[] lineArr = lineTxt.split(",");
                if(lineArr[1].contains(aspect)){
                    String adjWord = lineArr[1].substring(lineArr[1].indexOf("】")+1);
                    for(int i=0;i<Integer.parseInt(lineArr[2]);i++){
                        result.append(adjWord).append(" ");
                    }
                }
            }
        }
        return result.toString();
    }
}
