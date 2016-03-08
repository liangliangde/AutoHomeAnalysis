package com.algorithm.SAcluster;

import com.IO.IOProcess;
import com.data.process.VariousMap;
import com.data.query.QueryFromNeo4j;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by llei on 16-3-5.
 */
public class CreateInputData {
    public static void main(String args[]) throws IOException {
//        createAttr2AttrIdMap();
        Map<String, String> seriesId2NumMap = VariousMap.seriesId2Num();
        Map<String, String> attrName2AttrId = VariousMap.attrName2AttrId();
        Map<String, String> attr2AttrIdMap = VariousMap.attr2AttrId();
        List<String[]> seriesId_attrList = IOProcess.readFile("/home/llei/IdeaProjects/autohome/AutoHomeAnalysis/auto_data/seriesId_attr.csv");
        createDataAttrbute(seriesId2NumMap, attrName2AttrId, attr2AttrIdMap, seriesId_attrList);
        createDataset(seriesId2NumMap, attr2AttrIdMap, seriesId_attrList);
    }

    private static void createDataset(Map<String, String> seriesId2NumMap, Map<String, String> attr2AttrIdMap, List<String[]> seriesId_attrList) throws IOException {
        int minCommUsers = 3;
        List<String[]> pairList = QueryFromNeo4j.querySeriesPair(minCommUsers);
        int sVertexSize = seriesId2NumMap.size();
        int aVertexSize = attr2AttrIdMap.size();
        int[][] transMatrix = new int[sVertexSize + aVertexSize + 1][sVertexSize + aVertexSize + 1];
        for (int i = 0; i < pairList.size(); i++) {
            String[] pair = pairList.get(i);
            int x = Integer.parseInt(seriesId2NumMap.get(pair[0]));
            int y = Integer.parseInt(seriesId2NumMap.get(pair[1]));
            transMatrix[x][y] = transMatrix[y][x] = 1;
        }
        for (int i = 0; i < seriesId_attrList.size(); i++) {
            String seriesId = seriesId_attrList.get(i)[0];
            String attr = seriesId_attrList.get(i)[1];
            int x = Integer.parseInt(seriesId2NumMap.get(seriesId));
            int y = Integer.parseInt(attr2AttrIdMap.get(attr)) + sVertexSize;
//            System.out.println(Integer.parseInt(attr2AttrIdMap.get(attr))+", "+sVertexSize);
            transMatrix[x][y] = transMatrix[y][x] = 1;
        }
        Double[][] probMatrix = createProbMatrix(transMatrix);
        IOProcess.writeFile("/home/llei/IdeaProjects/autohome/AutoHomeAnalysis/src/com/algorithm/SAcluster/inputdata/Dataset.txt", createSparseMatrix(probMatrix));
    }

    private static String createSparseMatrix(Double[][] probMatrix) {
        int degree = probMatrix[0].length;
        StringBuffer str = new StringBuffer();
        for(int i=1;i<degree;i++){
            for(int j=1;j<degree;j++){
                if(probMatrix[i][j]>0){
                    str.append(i).append("  ").append(j).append("  ").append(probMatrix[i][j]).append("\n");
                }
            }
        }
        return str.toString();
    }

    private static Double[][] createProbMatrix(int[][] transMatrix) {
        int degree = transMatrix[0].length;
        Double[][] probMatrix = new Double[degree][degree];
        for (int i = 1; i < degree; i++) {
            int numOf1 = 0;
            for (int j = 1; j < degree; j++) {
                numOf1 += transMatrix[i][j];
            }
            if (numOf1 == 0){
                for (int j = 1; j < degree; j++) {
                    probMatrix[i][j] = 0.0;
                }
            }
            else {
                for (int j = 1; j < degree; j++) {
                    probMatrix[i][j] = (1.0 * transMatrix[i][j]) / numOf1;
                }
            }
        }
        return probMatrix;
    }

    private static void createDataAttrbute(Map<String, String> seriesId2NumMap, Map<String, String> attrName2AttrId, Map<String, String> attr2AttrIdMap, List<String[]> seriesId_attrList) throws IOException {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < seriesId_attrList.size(); i++) {
            String seriesId = seriesId_attrList.get(i)[0];
            String attr = seriesId_attrList.get(i)[1];
            String attrName = attr.substring(0, attr.indexOf("]") + 1);
            str.append(seriesId2NumMap.get(seriesId)).append("  ").append(attrName2AttrId.get(attrName)).append("  ").append(attr2AttrIdMap.get(attr)).append("\n");
        }
        IOProcess.writeFile("/home/llei/IdeaProjects/autohome/AutoHomeAnalysis/src/com/algorithm/SAcluster/inputdata/DataAttribute.txt", str.toString());
    }

    private static void createAttr2AttrIdMap() throws IOException {
        StringBuffer str = new StringBuffer();
        str.append("1,").append("[品牌]\n").append("2,").append("[车型]\n").append("3,").append("[价格]\n").append("4,").append("[工信部综合油耗(L/100km)]\n");
        IOProcess.writeFile("/home/llei/IdeaProjects/autohome/AutoHomeAnalysis/src/com/algorithm/SAcluster/inputdata/attrId_attrName", str.toString());
    }

}
