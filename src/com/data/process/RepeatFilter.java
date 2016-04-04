package com.data.process;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by llei on 16-4-1.
 */
public class RepeatFilter {
    public static void main(String[] args) throws IOException {
        Set<String> set = new HashSet<>();
        File file = new File("/home/llei/IdeaProjects/autohome/AutoHomeAnalysis_new/auto_data/koubei.csv");
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        FileOutputStream out = new FileOutputStream("/home/llei/IdeaProjects/autohome/AutoHomeAnalysis_new/auto_data/koubei2.csv");
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            String id = lineTxt.substring(0,lineTxt.indexOf(","));
            if(!set.contains(id)){
                out.write((lineTxt+"\n").getBytes());
                set.add(id);
            }
        }
        inputStreamReader.close();
        bufferedReader.close();
        out.close();
    }
}
