package com.IO;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by llei on 16-2-26.
 */
public class IOProcess {
    public static void writeFile(String filePath, String content) throws IOException {
        FileOutputStream out= new FileOutputStream(filePath,false);
        out.write(content.getBytes());
        out.close();
    }

    public static List<String[]> readFile(String path) throws IOException {
        File brandFile = new File(path);
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(brandFile), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt;
        List<String[]> list = new ArrayList<>();
        while ((lineTxt = bufferedReader.readLine()) != null) {
            list.add(lineTxt.split(","));
        }
        inputStreamReader.close();
        bufferedReader.close();
        return list;
    }

    public static List<String> readFileWithoutSplit(String path) throws IOException {
        File brandFile = new File(path);
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(brandFile), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt;
        List<String> list = new ArrayList<>();
        while ((lineTxt = bufferedReader.readLine()) != null) {
            list.add(lineTxt);
        }
        inputStreamReader.close();
        bufferedReader.close();
        return list;
    }
}
