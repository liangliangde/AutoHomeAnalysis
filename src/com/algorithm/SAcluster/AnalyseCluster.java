package com.algorithm.SAcluster;

import com.IO.IOProcess;
import com.data.process.VariousMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.data.process.ExtractKeyTerms.extractTerm;
import static com.data.process.ShowCluster.getFinalCluster;

/**
 * Created by llei on 16-3-6.
 */
public class AnalyseCluster {
    public static void main(String args[]) throws IOException {
        List<String[]> list = IOProcess.readFile("/home/llei/IdeaProjects/autohome/AutoHomeAnalysis/src/com/algorithm/SAcluster/DBLP84170/cluster(comUsers>=8).txt");
        int k = 10;
        List<List<String>> cluster = processList(list, k);
        List<List<String>> terms = extractTerm(cluster);
        String content = getFinalCluster(cluster, terms);
        IOProcess.writeFile("src/com/algorithm/clusterresult/sa-cluster_result(comUsers>=8)", content);
    }

    private static List<List<String>> processList(List<String[]> list, int k) throws IOException {
        Map<String, String> num2SeriesIdMap = VariousMap.num2SeriesId();
        List<List<String>> cluster = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            cluster.add(new ArrayList<>());
        }
        for (int i = 0; i < list.size(); i++) {
            int clusterId = Integer.parseInt(list.get(i)[0]) - 1;
            cluster.get(clusterId).add(num2SeriesIdMap.get("" + (i + 1)));
        }
        return cluster;
    }
}
