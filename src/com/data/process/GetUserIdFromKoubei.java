package com.data.process;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by llei on 16-1-25.
 */
public class GetUserIdFromKoubei {
    public static void main(String args[]) throws IOException {
        File dist = new File("/home/llei/pythonworkspace/myspider-spider/autohome/koubei");
        String seriesIds[] = {"882","403"};
        List<String> seriesIdsList = Arrays.asList(seriesIds);
        File[] files = dist.listFiles();
        Map<String, String> userMap = VariousMap.userMap();
        for (File file : files) {
            System.out.println(file.getName());
            if (!seriesIdsList.contains(file.getName()))
                continue;
            InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                getJSONContent(lineTxt, userMap);
            }
        }
        System.out.print("finish");
    }

    private static void getJSONContent(String JSONText, Map<String, String> userMap) throws IOException {
        FileOutputStream out = new FileOutputStream("/home/llei/IdeaProjects/autohome/AutoHomeAnalysis_new/auto_data/importantUsers.csv", true);
        JSONTokener jsonTokener = new JSONTokener(JSONText);
        JSONObject JSONObject;
        Map<String, String> map = new HashMap<>();
        try {
            JSONObject = (JSONObject) jsonTokener.nextValue();
            String uid = JSONObject.getString("uid").trim();
            if (!userMap.containsKey(uid))
                map.put(uid, "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            out.write((entry.getKey() + "\n").getBytes());
        }
        out.close();
    }
}
