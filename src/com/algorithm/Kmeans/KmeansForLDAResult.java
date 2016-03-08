package com.algorithm.Kmeans;

import com.IO.IOProcess;
import com.data.process.ShowCluster;
import com.data.process.VariousMap;

import java.io.*;
import java.util.*;

import static com.data.process.ExtractKeyTerms.extractTerm;

/**
 * Created by llei on 15-9-29.
 */
public class KmeansForLDAResult {

    private static final Double INF = 100000000.0;
    private static final boolean SHOWPROCESS = false;

    public static void main(String args[]) throws IOException {
        int centerNum = 8;
        int degree = 8;
        List<String> seriesIds = getSeriesIds("/home/llei/IdeaProjects/autohome/AutoHomeAnalysis/src/com/algorithm/ldaresult/series_doc/LDADoc.txt");
        Map<String, Double[]> seriesIdUserMap = getUserVec("/home/llei/IdeaProjects/autohome/AutoHomeAnalysis/src/com/algorithm/ldaresult/series_doc/model-final.theta", seriesIds);
        System.out.println("series number = " + seriesIdUserMap.keySet().size());
        List<List<String>> cluster = Kmeans(seriesIdUserMap, centerNum, degree);
        List<List<String>> terms = extractTerm(cluster);
        String content = ShowCluster.getFinalCluster(cluster, terms);
        IOProcess.writeFile("src/com/algorithm/clusterresult/lda_kmeans_result", content);
    }

    private static Map<String,Double[]> getUserVec(String path, List<String> seriesIds) throws IOException {
        Map<String, Double[]> seriesIdUserMap = new HashMap<>();
        File file = new File(path);
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt = null;
        int i=0;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            seriesIdUserMap.put(seriesIds.get(i++), stringArr2Double(lineTxt.split(" ")));
        }
        inputStreamReader.close();
        bufferedReader.close();
        return seriesIdUserMap;
    }

    private static Double[] stringArr2Double(String[] strArr) {
        Double[] doubleArr = new Double[strArr.length];
        for(int i=0;i<strArr.length;i++){
            doubleArr[i] = Double.parseDouble(strArr[i]);
        }
        return doubleArr;
    }

    private static List<String> getSeriesIds(String path) throws IOException {
        List<String> seriesIds = new ArrayList<>();
        File file = new File(path);
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            seriesIds.add(lineTxt);
        }
        inputStreamReader.close();
        bufferedReader.close();
        return seriesIds;
    }

    public static List<List<String>> Kmeans(Map<String, Double[]> seriesIdUserMap, int centerNum, int degree) {
        List<List<Double>> centerList;
        List<List<Double>> newCenterList = null;
        List<List<String>> cluster;
        int round = 1;
        do {
            if(SHOWPROCESS) System.out.println("Round " + round++);
            centerList = newCenterList == null ? selectInitPoints(seriesIdUserMap, centerNum, degree) : newCenterList;
            cluster = new ArrayList<>();
            //assign center to cluster
            for (int i = 0; i < centerList.size(); i++) {
                List<String> inner = new ArrayList<>();
                cluster.add(inner);
            }
            //add remain points to the nearest center of cluster
            for (Map.Entry<String, Double[]> userSeriesId : seriesIdUserMap.entrySet()) {
                double minDis = INF;
                int minDisNum = -1;
                for (int j = 0; j < centerList.size(); j++) {
                    double dis = calEDistance(centerList.get(j), userSeriesId.getValue());
                    if (dis < minDis) {
                        minDisNum = j;
                        minDis = dis;
                    }
                }
                cluster.get(minDisNum).add(userSeriesId.getKey());
            }
            if(SHOWPROCESS) showCluster(seriesIdUserMap, cluster);
            newCenterList = calCenter(seriesIdUserMap, cluster, degree);
        } while (!centerList.equals(newCenterList));
        return cluster;
    }

    private static double calEDistance(List<Double> center, Double[] point) {
        double dis = 0.0;
        for (int i = 0; i < center.size(); i++) {
            dis += Math.pow((center.get(i) - point[i]), 2);
        }
        return dis;
    }

    private static double calEDistance(Double[] center, Double[] point) {
        double dis = 0.0;
        for (int i = 0; i < center.length; i++) {
            dis += Math.pow((center[i] - point[i]), 2);
        }
        return dis;
    }

    private static List<List<Double>> selectInitPoints(Map<String, Double[]> seriesIdUserMap, int centerNum, int degree) {
        List<List<Double>> initPoints = new ArrayList<>();
        while (centerNum > 0) {
            if (initPoints.size() == 0) {
                //select 2 points with max E distance
                String[] maxDisPointPair = calMaxDisPointPair(seriesIdUserMap);
                List<Double> firstVec = new ArrayList<>();
                List<Double> secondVec = new ArrayList<>();
                for (int i = 0; i < degree; i++) {
                    firstVec.add(1.0 * seriesIdUserMap.get(maxDisPointPair[0])[i]);
                    secondVec.add(1.0 * seriesIdUserMap.get(maxDisPointPair[1])[i]);
                }
                initPoints.add(firstVec);
                initPoints.add(secondVec);
                centerNum -= 2;
                if(SHOWPROCESS) System.out.print("Initial points: \n" + firstVec.toString() + "\n" + secondVec.toString() + "\n");
            } else {
                int maxSquareDistance = -1;
                String maxSquareDistanceSeriesId = "";
                for (Map.Entry<String, Double[]> userSeriesId : seriesIdUserMap.entrySet()) {
                    if (initPoints.contains(Arrays.asList(userSeriesId.getValue())))
                        continue;
                    int curSquareDistance = 1;
                    for (int j = 0; j < initPoints.size(); j++) {
                        curSquareDistance *= calEDistance(initPoints.get(j), userSeriesId.getValue());
                    }
                    if (curSquareDistance > maxSquareDistance) {
                        maxSquareDistanceSeriesId = userSeriesId.getKey();
                        maxSquareDistance = curSquareDistance;
                    }
                }
                List<Double> inner = new ArrayList<>();
                for (int i = 0; i < degree; i++) {
                    inner.add(1.0 * seriesIdUserMap.get(maxSquareDistanceSeriesId)[i]);
                }
                initPoints.add(inner);
                centerNum--;
                if(SHOWPROCESS) System.out.print(inner.toString() + "\n");
            }
        }
        if(SHOWPROCESS) System.out.println();
        return initPoints;
    }

    private static String[] calMaxDisPointPair(Map<String, Double[]> seriesIdUserMap) {
        String[] maxDisPointPair = new String[2];
        Double maxDis = -1.0;

        for (Map.Entry<String, Double[]> firstUserSeriesId : seriesIdUserMap.entrySet()) {
            Double[] firstVec = firstUserSeriesId.getValue();
            for (Map.Entry<String, Double[]> secondUserSeriesId : seriesIdUserMap.entrySet()) {
                Double dis = calEDistance(firstVec, secondUserSeriesId.getValue());
                if (dis > maxDis) {
                    maxDisPointPair[0] = firstUserSeriesId.getKey();
                    maxDisPointPair[1] = secondUserSeriesId.getKey();
                    maxDis = dis;
                }
            }
        }
        return maxDisPointPair;
    }

    private static void showCluster(Map<String, Double[]> seriesIdUserMap, List<List<String>> cluster) {
        for (int i = 0; i < cluster.size(); i++) {
            System.out.print("Cluster " + i + ": ");
            for (int j = 0; j < cluster.get(i).size(); j++) {
                System.out.print("(" + cluster.get(i).get(j) + "-" + double2String(seriesIdUserMap.get(cluster.get(i).get(j))) + ") ");
            }
            System.out.println();
        }
    }

    private static String double2String(Double[] doubles) {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < doubles.length; i++) {
            str.append(doubles[i]+",");
        }
        return str.toString();
    }

    private static List<List<Double>> calCenter(Map<String, Double[]> seriesIdUserMap, List<List<String>> cluster, int degree) {
        List<List<Double>> centerList = new ArrayList<>();
        for (int i = 0; i < cluster.size(); i++) {
            double sum[] = new double[degree];
            for (int j = 0; j < cluster.get(i).size(); j++) {
                for (int k = 0; k < degree; k++) {
                    sum[k] += seriesIdUserMap.get(cluster.get(i).get(j))[k];
                }
            }
            List<Double> inner = new ArrayList<>();
            for (int k = 0; k < degree; k++) {
                inner.add(sum[k] / cluster.get(i).size());
            }
            centerList.add(inner);
        }
        if(SHOWPROCESS) {
            System.out.print("Center points: ");
            for (int i = 0; i < centerList.size(); i++) {
                System.out.print(centerList.get(i).toString() + " ");
            }
            System.out.println();
        }
        return centerList;
    }
}