package com.data.process;

import com.IO.IOProcess;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.io.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by llei on 16-3-29.
 */
public class CreateStyle2Aspect {

    private static Map<String, Integer> aspect2NumMap;
    private static Map<Integer, String> num2AspectMap;

    public static void main(String args[]) throws IOException {
        createAspectAndNumMap();
        Map<String, String> style2SeriesMap = VariousMap.styleId2SeriesId();
        Map<String, Double[]> keyword2Vecmap = VariousMap.keyword2Vector();
        Map<String, Double[]> style2Vec = new HashMap<>();
        Map<String, Integer> style2Num = new HashMap<>();
        File brandFile = new File("auto_data/koubei.csv");
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(brandFile), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            String[] line = lineTxt.split(",");
            String seriesId = style2SeriesMap.get(line[1]);
            String koubei = line[16] + " " + line[19].replace("null", "");
            Double vec[] = parseKoubei(koubei, keyword2Vecmap);

            if (!style2Num.containsKey(seriesId)) {
                style2Num.put(seriesId, 1);
                style2Vec.put(seriesId, vec);
            } else {
                style2Num.put(seriesId, style2Num.get(seriesId) + 1);
                Double existVec[] = style2Vec.get(seriesId);
                for (int i = 0; i < 8; i++) {
                    existVec[i] += vec[i];
                }
                style2Vec.put(seriesId, existVec);
            }
        }
        inputStreamReader.close();
        bufferedReader.close();

        StringBuffer str = new StringBuffer();
        DecimalFormat df = new DecimalFormat("0.00");
        for (Map.Entry<String, Integer> entry : style2Num.entrySet()) {
            String styleId = entry.getKey();
            int koubeiNum = entry.getValue();
            Double[] totalVec = style2Vec.get(styleId);
            for (int i = 0; i < 8; i++) {
                str.append(styleId).append(",").append(num2AspectMap.get(i)).append(",").append(df.format(totalVec[i] / koubeiNum)).append("\n");
            }
        }
        IOProcess.writeFile("auto_data/series_aspect.csv", str.toString());
    }

    private static Double[] parseKoubei(String koubei, Map<String, Double[]> keyword2Vecmap) {
        Double[] vec = new Double[8];
        for (int i = 0; i < 8; i++) {
            vec[i] = 0.0;
        }
        List<Term> parse = ToAnalysis.parse(koubei);
        for (Term term : parse) {
            if (term.getName().length() < 2) {
                continue;
            }
            String str = term.toString();
            String word = term.toString().substring(0, term.toString().indexOf("/"));
            if (str.indexOf("/") > -1 && (str.substring(str.indexOf("/")).equals("/n") || str.substring(str.indexOf("/")).equals("/v"))
                    && keyword2Vecmap.containsKey(word)) {
                Double wordvec[] = keyword2Vecmap.get(word);
                for (int i = 0; i < 8; i++) {
                    vec[i] += wordvec[i];
                }
            }
        }
        return vec;
    }

    private static void createAspectAndNumMap() {
        aspect2NumMap = new HashMap<>();
        aspect2NumMap.put("外观", 0);
        aspect2NumMap.put("舒适性", 1);
        aspect2NumMap.put("操控", 2);
        aspect2NumMap.put("性价比", 3);
        aspect2NumMap.put("内饰", 4);
        aspect2NumMap.put("油耗", 5);
        aspect2NumMap.put("动力", 6);
        aspect2NumMap.put("空间", 7);

        num2AspectMap = new HashMap<>();
        num2AspectMap.put(0, "外观");
        num2AspectMap.put(1, "舒适性");
        num2AspectMap.put(2, "操控");
        num2AspectMap.put(3, "性价比");
        num2AspectMap.put(4, "内饰");
        num2AspectMap.put(5, "油耗");
        num2AspectMap.put(6, "动力");
        num2AspectMap.put(7, "空间");
    }
}
