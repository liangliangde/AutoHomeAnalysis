package com.data.process;

import com.data.query.QueryFromNeo4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by llei on 16-3-4.
 */
public class ExtractKeyTerms {

    public static List<List<String>> extractTerm(List<List<String>> clusters) {
        List<List<String>> terms = new ArrayList<>();
        List<Map<String, Integer>> clusterTermsFreq = QueryFromNeo4j.querySeriesAttrById(clusters);
        Map<String, Integer> totalTermsFreq = clusterTermsFreq.get(clusterTermsFreq.size() - 1);
        for (int i = 0; i < clusterTermsFreq.size() - 1; i++) {
            Map<String, Integer> termsFreq = clusterTermsFreq.get(i);
            List<String> term = new ArrayList<>();
            for(Map.Entry<String,Integer> entry:termsFreq.entrySet()){
                String attr = entry.getKey();
                int freq = entry.getValue();
                Double ratio = (1.0 * freq)/totalTermsFreq.get(attr);
                if(freq > 3 && ratio >= 0.5){
                    term.add(attr + "," + ratio + "\n");
                }
            }
            terms.add(term);
        }
        return terms;
    }
}
