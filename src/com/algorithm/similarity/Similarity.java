package com.algorithm.similarity;

/**
 * Created by llei on 16-3-27.
 */
public class Similarity {
    public static void main(String[] args){
        Double v1[] = new Double[]{0.7,0.2,0.1};
        Double v2[] = new Double[]{0.1,0.2,0.3};
        System.out.print(cosSimilarity(v1, v2));
    }
    public static Double cosSimilarity(Double[] v1, Double[] v2) {
        Double fenzi = 0.0;
        Double fenmu1 = 0.0;
        Double fenmu2 = 0.0;
        for (int i = 0; i < v1.length; i++) {
            fenzi += v1[i] * v2[i];
            fenmu1 += Math.pow(v1[i], 2);
            fenmu2 += Math.pow(v2[i], 2);
        }
        return fenzi/(Math.sqrt(fenmu1)*(Math.sqrt(fenmu2)));
    }
}
