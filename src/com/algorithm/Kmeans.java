package com.algorithm;

import com.data.query.QueryFromNeo4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by llei on 15-9-29.
 */
public class Kmeans {

    private static final Double INF = 100000000.0;
    private static final boolean SHOWPROCESS = false;

//    public static void main(String args[]) throws IOException {
//        String[] seriesIds = {"633", "639", "874", "66", "792", "364", "530", "2987"};
//        int centerNum = 5;
//        Map<String, int[]> userSeriesIdMap = QueryFromNeo4j.queryUserBySeriesId(seriesIds);
//        Kmeans(userSeriesIdMap, centerNum, seriesIds.length);
//        System.out.println("user number = " + userSeriesIdMap.keySet().size());
//    }

    public static List<List<String>> Kmeans(Map<String, int[]> userSeriesIdMap, int centerNum, int degree) {
        List<List<Double>> centerList;
        List<List<Double>> newCenterList = null;
        List<List<String>> cluster;
        int round = 1;
        do {
            if(SHOWPROCESS) System.out.println("Round " + round++);
            centerList = newCenterList == null ? selectInitPoints(userSeriesIdMap, centerNum, degree) : newCenterList;
            cluster = new ArrayList<>();
            //assign center to cluster
            for (int i = 0; i < centerList.size(); i++) {
                List<String> inner = new ArrayList<>();
                cluster.add(inner);
            }
            //add remain points to the nearest center of cluster
            for (Map.Entry<String, int[]> userSeriesId : userSeriesIdMap.entrySet()) {
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
            if(SHOWPROCESS) showCluster(userSeriesIdMap, cluster);
            newCenterList = calCenter(userSeriesIdMap, cluster, degree);
        } while (!centerList.equals(newCenterList));
        return cluster;
    }

    private static double calEDistance(List<Double> center, int[] point) {
        double dis = 0.0;
        for (int i = 0; i < center.size(); i++) {
            dis += Math.pow((center.get(i) - point[i]), 2);
        }
        return dis;
    }

    private static double calEDistance(int[] center, int[] point) {
        double dis = 0.0;
        for (int i = 0; i < center.length; i++) {
            dis += Math.pow((center[i] - point[i]), 2);
        }
        return dis;
    }

    private static List<List<Double>> selectInitPoints(Map<String, int[]> userSeriesIdMap, int centerNum, int degree) {
        List<List<Double>> initPoints = new ArrayList<>();
        while (centerNum > 0) {
            if (initPoints.size() == 0) {
                //select 2 points with max E distance
                String[] maxDisPointPair = calMaxDisPointPair(userSeriesIdMap);
                List<Double> firstVec = new ArrayList<>();
                List<Double> secondVec = new ArrayList<>();
                for (int i = 0; i < degree; i++) {
                    firstVec.add(1.0 * userSeriesIdMap.get(maxDisPointPair[0])[i]);
                    secondVec.add(1.0 * userSeriesIdMap.get(maxDisPointPair[1])[i]);
                }
                initPoints.add(firstVec);
                initPoints.add(secondVec);
                centerNum -= 2;
                if(SHOWPROCESS) System.out.print("Initial points: \n" + firstVec.toString() + "\n" + secondVec.toString() + "\n");
            } else {
                int maxSquareDistance = -1;
                String maxSquareDistanceSeriesId = "";
                for (Map.Entry<String, int[]> userSeriesId : userSeriesIdMap.entrySet()) {
                    if (initPoints.contains(Arrays.asList(ints2Doubles(userSeriesId.getValue()))))
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
                    inner.add(1.0 * userSeriesIdMap.get(maxSquareDistanceSeriesId)[i]);
                }
                initPoints.add(inner);
                centerNum--;
                if(SHOWPROCESS) System.out.print(inner.toString() + "\n");
            }
        }
        if(SHOWPROCESS) System.out.println();
        return initPoints;
    }

    private static Double[] ints2Doubles(int[] value) {
        int len = value.length;
        Double[] result = new Double[len];
        for (int i = 0; i < len; i++) {
            result[i] = 1.0 * value[i];
        }
        return result;
    }

    private static String[] calMaxDisPointPair(Map<String, int[]> userSeriesIdMap) {
        String[] maxDisPointPair = new String[2];
        Double maxDis = -1.0;

        for (Map.Entry<String, int[]> firstUserSeriesId : userSeriesIdMap.entrySet()) {
            int[] firstVec = firstUserSeriesId.getValue();
            for (Map.Entry<String, int[]> secondUserSeriesId : userSeriesIdMap.entrySet()) {
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

    private static void showCluster(Map<String, int[]> userSeriesIdMap, List<List<String>> cluster) {
        for (int i = 0; i < cluster.size(); i++) {
            System.out.print("Cluster " + i + ": ");
            for (int j = 0; j < cluster.get(i).size(); j++) {
                System.out.print("(" + cluster.get(i).get(j) + "-" + int2String(userSeriesIdMap.get(cluster.get(i).get(j))) + ") ");
            }
            System.out.println();
        }
    }

    private static String int2String(int[] ints) {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < ints.length; i++) {
            str.append(ints[i]);
        }
        return str.toString();
    }

    private static List<List<Double>> calCenter(Map<String, int[]> userSeriesIdMap, List<List<String>> cluster, int degree) {
        List<List<Double>> centerList = new ArrayList<>();
        for (int i = 0; i < cluster.size(); i++) {
            double sum[] = new double[degree];
            for (int j = 0; j < cluster.get(i).size(); j++) {
                for (int k = 0; k < degree; k++) {
                    sum[k] += userSeriesIdMap.get(cluster.get(i).get(j))[k];
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