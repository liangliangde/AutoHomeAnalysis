package com.data.process;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by llei on 16-1-25.
 */
public class UserParser {
    public static void main(String args[]) throws IOException {
        FileOutputStream outUserInfo = new FileOutputStream("auto_data/user.csv");
        FileOutputStream outUser_user = new FileOutputStream("auto_data/user_followedBy_user.csv");
        FileOutputStream outUser_style = new FileOutputStream("auto_data/user_style.csv");
        FileOutputStream outUser_series = new FileOutputStream("auto_data/user_series.csv");
        File dist = new File("/home/llei/pythonworkspace/myspider-spider/autohome/user");
        Set<String> seriesOfUserSet;
        File[] files = dist.listFiles();
        for(File file : files){
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
                    String follower = JSONObject.getString("followers");
                    String car = JSONObject.getString("car");

                    JSONTokener infoTokener = new JSONTokener(info);
                    JSONObject infoObject = (JSONObject) infoTokener.nextValue();
                    String username = infoObject.has("用户名") ? infoObject.getString("用户名") : null;
                    String gender = infoObject.has("性别") ? infoObject.getString("性别") : null;
                    String location = infoObject.has("所在地") ? infoObject.getString("所在地") : null;
                    String birthday = infoObject.has("生日") ? infoObject.getString("生日") : null;
                    String age_category;
                    if(birthday == null) {
                        age_category = null;
                    }
                    else {
                        int age = 2016 - Integer.parseInt(birthday.substring(0, 4));
                        if (age < 14) {
                            age_category = "少年";
                        } else if (age < 30) {
                            age_category = "青年";
                        } else if (age < 50) {
                            age_category = "中年";
                        } else {
                            age_category = "老年";
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

                    JSONTokener followerTokener = new JSONTokener(follower);
                    JSONObject followerObject = (JSONObject) followerTokener.nextValue();
                    Iterator it = followerObject.keys();
                    StringBuffer user_user = new StringBuffer();
                    while (it.hasNext()) {
                        user_user.append(uid).append(",").append(it.next().toString()).append("\n");
                    }
                    outUser_user.write(user_user.toString().getBytes());

                    JSONTokener carTokener = new JSONTokener(car);
                    JSONObject carObject = (JSONObject) carTokener.nextValue();
                    it = carObject.keys();
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
        outUser_user.close();
        outUser_style.close();
        outUser_series.close();
    }
}
