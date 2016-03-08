package com.data.process;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by llei on 16-3-3.
 */
public class MakeStyleAttr {
    public static void main(String args[]) throws IOException {
        createStyle_Attr();
//        createAttrList();

    }

    private static void createStyle_Attr() throws IOException {
        File file = new File("/home/llei/IdeaProjects/autohome/AutoHomeAnalysis/auto_data/styleId_attr.txt");
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        FileOutputStream out = new FileOutputStream("/home/llei/IdeaProjects/autohome/AutoHomeAnalysis/auto_data/styleId_attr_new.csv");
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            String value = lineTxt.substring(lineTxt.indexOf("]") + 1);
            if (!value.equals("null") && !value.contains("未知"))
                out.write((lineTxt.trim().replace("&nbsp;/&nbsp;", "")+"\n").getBytes());
        }
        inputStreamReader.close();
    }

    private static void createAttrList() throws IOException {
        File file = new File("/home/llei/IdeaProjects/autohome/AutoHomeAnalysis/auto_data/styleId_attr.txt");
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        FileOutputStream out = new FileOutputStream("/home/llei/IdeaProjects/autohome/AutoHomeAnalysis/auto_data/Attribution.csv");
        String lineTxt = null;
        Map<String, String> map = new TreeMap<>();
        while ((lineTxt = bufferedReader.readLine()) != null) {
            String value = lineTxt.substring(lineTxt.indexOf("]") + 1);
            if (!value.equals("null") && !value.contains("未知"))
                map.put(lineTxt.substring(lineTxt.indexOf(",") + 1).trim().replace("&nbsp;/&nbsp;", ""), "");
        }
        inputStreamReader.close();
        bufferedReader.close();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            out.write((entry.getKey() + "\n").getBytes());
        }
    }
}
