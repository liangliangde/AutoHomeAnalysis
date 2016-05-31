package com.data.process;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.*;

/**
 * Created by llei on 16-1-25.
 */
public class UserMerge {
    public static void main(String args[]) throws IOException {
        Map<String,String> userMap = getExistUsers("/home/llei/IdeaProjects/autohome/AutoHomeAnalysis_new/auto_data/user.csv");
        FileOutputStream outUserInfo = new FileOutputStream("/home/llei/IdeaProjects/autohome/AutoHomeAnalysis_new/auto_data/user.csv", true);
        FileOutputStream outUser_style = new FileOutputStream("/home/llei/IdeaProjects/autohome/AutoHomeAnalysis_new/auto_data/user_style.csv", true);
        FileOutputStream outUser_series = new FileOutputStream("/home/llei/IdeaProjects/autohome/AutoHomeAnalysis_new/auto_data/user_series.csv", true);

        Set<String> seriesOfUserSet;
        File[] dists = new File[1];
        dists[0] = new File("/home/llei/IdeaProjects/autohome/user_yage");
        List<File> files_all = new ArrayList<>();
        for (File dist : dists) {
            File[] files = dist.listFiles();
            List<File> files2 = Arrays.asList(files);
            files_all.addAll(files2);
        }

        for (File file : files_all) {
            if (userMap.containsKey(file.getName())) {
                continue;
            }
            System.out.println(file.getName());
            seriesOfUserSet = new HashSet<>();
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                JSONTokener jsonTokener = new JSONTokener(lineTxt);
                JSONObject JSONObject;
                try {
                    JSONObject = (JSONObject) jsonTokener.nextValue();
                    String uid = JSONObject.getString("uid");
                    String info = JSONObject.getString("info");
                    String car = JSONObject.getString("car");

                    JSONTokener infoTokener = new JSONTokener(info);
                    JSONObject infoObject = (JSONObject) infoTokener.nextValue();
                    String username = infoObject.has("用户名") ? infoObject.getString("用户名") : null;
                    String gender = infoObject.has("性别") ? infoObject.getString("性别") : null;
                    String location = infoObject.has("所在地") ? infoObject.getString("所在地").split(" ")[0] : null;
                    String birthday = infoObject.has("生日") ? infoObject.getString("生日") : null;
                    String age_category;
                    if (birthday == null) {
                        age_category = null;
                    } else {
                        int age = 2016 - Integer.parseInt(birthday.substring(0, 4));
                        if (age < 20) {
                            age_category = "<20";
                        } else if (age < 30) {
                            age_category = "20~29";
                        } else if (age < 40) {
                            age_category = "30~39";
                        } else if (age < 50) {
                            age_category = "40~49";
                        } else {
                            age_category = ">=50";
                        }
                    }
                    String verification = infoObject.getString("手机认证").equals("未认证") ? "false" : "true";
                    StringBuffer userInfo = new StringBuffer();
                    userInfo.append(uid).append(",")
                            .append(username).append(",")
                            .append(gender).append(",")
                            .append(location).append(",")
                            .append(birthday).append(",")
                            .append(age_category).append(",")
                            .append(verification).append("\n");
                    outUserInfo.write(userInfo.toString().getBytes());
                    userMap.put(uid, "");

                    JSONTokener carTokener = new JSONTokener(car);
                    JSONObject carObject = (JSONObject) carTokener.nextValue();
                    Iterator it = carObject.keys();
                    StringBuffer user_style = new StringBuffer();
                    while (it.hasNext()) {
                        String sid = it.next().toString().trim();
                        String style = carObject.getString(sid);
                        String seriesid = sid.substring(0, sid.indexOf('_'));
                        String styleid = sid.substring(sid.indexOf('_') + 1);
                        JSONTokener styleTokener = new JSONTokener(style);
                        JSONObject styleObject = (JSONObject) styleTokener.nextValue();
                        String type = styleObject.getString("tpye").trim();
                        if (!seriesOfUserSet.contains(seriesid)) {
                            seriesOfUserSet.add(seriesid);
                            StringBuffer user_series = new StringBuffer();
                            user_series.append(uid).append(",").append(seriesid).append("\n");
                            outUser_series.write(user_series.toString().getBytes());
                        }
                        if (!sid.substring(sid.indexOf('_') + 1).equals("0")) {
                            user_style.append(uid).append(",").append(styleid).append(",").append(type).append("\n");
                        }
                    }
                    outUser_style.write(user_style.toString().getBytes());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            inputStreamReader.close();
            bufferedReader.close();
        }
        outUserInfo.close();
        outUser_style.close();
        outUser_series.close();
    }

    public static Map<String, String> getExistUsers(String path) throws IOException {
        Map<String, String> userMap = new HashMap<>();
        File brandFile = new File(path);
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(brandFile), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            int split = lineTxt.indexOf(",");
            String userId = lineTxt.substring(0, split);
            userMap.put(userId, "");
        }
        inputStreamReader.close();
        bufferedReader.close();
        return userMap;
    }
}
