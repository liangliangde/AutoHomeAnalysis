package com.data.process;

import com.IO.IOProcess;

import java.io.IOException;
import java.util.Map;

import static com.data.process.VariousMap.seriesId2Detail;
import static com.data.process.VariousMap.seriesId2OilAttr;

/**
 * Created by llei on 16-3-9.
 */
public class AddOilCostRangeToSeries {
    public static void main(String args[]) throws IOException {
        Map<String, String> seriesId2DetailMap = seriesId2Detail();
        Map<String, String> seriesId2AttrMap = seriesId2OilAttr();
        StringBuffer str=new StringBuffer();
        for (Map.Entry<String, String> entry : seriesId2DetailMap.entrySet()) {
            String seriesId = entry.getKey();
            String seriesInfo = entry.getValue();
            String oilCostRange = seriesId2AttrMap.get(seriesId);
            str.append(seriesId).append(",").append(seriesInfo.replace("(L/100km)","")).append(",").append(oilCostRange).append("\n");
        }
        IOProcess.writeFile("auto_data/series2.csv", str.toString());
    }
}
