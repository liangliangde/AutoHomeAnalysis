package com.data.process;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.IO.IOProcess.readFile;

/**
 * Created by llei on 16-3-1.
 */
public class GetAvgOilCostofSeries {
    private static List<String[]> style;
    private static List<String[]> series;
    private static List<String[]> series_style;
    private static Map<String, String> series_oilMap;

    public static void main(String args[]) throws IOException {
        style = readFile("auto_data/style.csv");
        series = readFile("auto_data/series_lackOilcost.csv");
        series_style = readFile("auto_data/series_style.csv");
        series_oilMap = new HashMap<>();
        FileOutputStream seriesOut = new FileOutputStream("auto_data/series.csv");
        DecimalFormat df = new DecimalFormat("#.00");
        for (int i = 0; i < series_style.size(); ) {
            int curStyleNum = 1;
            String curSeriesId = series_style.get(i)[0];
            for (int j = i + 1; j < series_style.size(); j++) {
                if (series_style.get(i)[0].equals(series_style.get(j)[0])) {
                    curStyleNum++;
                } else {
                    break;
                }
            }
            Double totalOilCost = 0.0;
            int totalPeopleNum = 0;
            for (int j = i; j < i + curStyleNum; j++) {
                String oilCost = style.get(j)[4];
                if (oilCost.equals("null"))
                    continue;
                int peopleNum = Integer.valueOf(oilCost.substring(oilCost.indexOf("(") + 1, oilCost.indexOf("人")));
                totalOilCost += Double.valueOf(oilCost.substring(0, oilCost.indexOf("L"))) * peopleNum;
                totalPeopleNum += peopleNum;
            }
            String AVGOilCost;
            if (totalPeopleNum == 0) {
                AVGOilCost = "null";
            } else {
                Double AVGOilCostNum = totalOilCost / totalPeopleNum;
                AVGOilCost = df.format(AVGOilCostNum) + "(L/100km)";
            }
            series_oilMap.put(curSeriesId, AVGOilCost);
            i += curStyleNum;
        }

        for(int i=0;i<series.size();i++){
            String[] thisSeries = series.get(i);
            StringBuffer outStr = new StringBuffer();
            for (int j = 0; j < thisSeries.length; j++)
                outStr.append(thisSeries[j] + ",");
            if(series_oilMap.containsKey(thisSeries[0]))
                outStr.append(series_oilMap.get(thisSeries[0]) + ",");
            else
                outStr.append("null" + ",");
            outStr.append(VariousMap.brandId2brandName().get(VariousMap.seriesId2brandId().get(thisSeries[0])));
            outStr.append("\n");
            seriesOut.write(outStr.toString().getBytes());
        }
    }



}
