package com.data.process;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by llei on 16-3-6.
 */
public class ShowCluster {

    public static String getFinalCluster(List<List<String>> cluster, List<List<String>> terms) throws IOException {
        StringBuffer str = new StringBuffer();
        Map<String, String> seriesIdtoDetail = VariousMap.seriesId2Detail();
        for (int i = 0; i < cluster.size(); i++) {
            str.append("\nCluster==================================================== " + i + "(" + cluster.get(i).size() + "series)" + ":\n");
            for (int j = 0; j < cluster.get(i).size(); j++) {
                str.append(seriesIdtoDetail.get(cluster.get(i).get(j)) + "\n");
            }
            str.append("\nKey words:\n");
            for (int j = 0; j < terms.get(i).size(); j++) {
                str.append(terms.get(i).get(j));
            }
        }
        return str.toString();
    }
}
