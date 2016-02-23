package com.data.process;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;

/**
 * Created by llei on 16-1-25.
 */
public class KoubeiJasonParser {
    public static void main(String args[]) throws IOException {
        File dist = new File("/home/llei/pythonworkspace/myspider-spider/autohome/koubei");
        File[] files = dist.listFiles();
        for(File file : files){
            InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                getJSONContent(lineTxt);
            }
        }
        System.out.print("finish");
    }

    private static void getJSONContent(String JSONText) throws IOException {
        FileOutputStream out = new FileOutputStream("auto_data/koubei.csv", true);
        JSONTokener jsonTokener = new JSONTokener(JSONText);
        JSONObject JSONObject;
        try {
            JSONObject = (JSONObject) jsonTokener.nextValue();
            String cid = JSONObject.getString("cid").trim();
            String kid = JSONObject.getString("kid").trim();
            String styleid = JSONObject.getString("sid").trim();
            String uid = JSONObject.getString("uid").trim();
            String interior = JSONObject.getString("内饰").trim();
            String power = JSONObject.getString("动力").trim();
            String appearence = JSONObject.getString("外观").trim();
            String costPerformance = JSONObject.getString("性价比").trim();
            String control = JSONObject.getString("操控").trim();
            String oil = JSONObject.getString("油耗").trim();
            String space = JSONObject.getString("空间").trim();
            String comfort = JSONObject.getString("舒适性").trim();
            String price = JSONObject.getString("裸车购买价").replace('\n', ' ').trim();
            String boughtSite = JSONObject.getString("购买地点").replace('\n', ' ').replace(' ',' ').trim();
            String boughtTime = JSONObject.getString("购买时间").replace('\n', ' ').trim();
            String boughtSeries = JSONObject.getString("购买车型").replace('\n', ' ').trim();
            String aim = JSONObject.getString("购车目的").replace('\n', ' ').trim();

            String content = JSONObject.getString("content").trim();
            JSONTokener contentTokener = new JSONTokener(content);
            JSONObject contentObject;
            contentObject = (JSONObject) contentTokener.nextValue();

            String koubei = contentObject.getString("1").trim();
            JSONTokener koubeiTokener = new JSONTokener(koubei);
            JSONObject koubeiObject;
            koubeiObject = (JSONObject) koubeiTokener.nextValue();
            String koubeiText = koubeiObject.getString("text").replace(",", "，").replace('\n', ' ').replace('\r', ' ').trim();
            String koubeiTime = koubeiObject.getString("time").trim();
            String koubeiType = koubeiObject.getString("type").trim();

            String addedKoubei = null;
            String addedkoubeiText = null;
            String addedkoubeiTime = null;
            String addedkoubeiType = null;
            if (contentObject.has("2")) {
                addedKoubei = contentObject.getString("2").trim();
                JSONTokener addedkoubeiTokener = new JSONTokener(addedKoubei);
                JSONObject addedkoubeiObject;
                addedkoubeiObject = (JSONObject) addedkoubeiTokener.nextValue();
                addedkoubeiText = addedkoubeiObject.getString("text").replace(",", "，").replace('\n', ' ').replace('\r', ' ').trim();
                addedkoubeiTime = addedkoubeiObject.getString("time").trim();
                addedkoubeiType = addedkoubeiObject.getString("type").trim();
            }
            StringBuffer koubeiRecord = new StringBuffer();
            koubeiRecord.append(kid).append(",")
//                    .append(cid).append(",")
                    .append(styleid).append(",")
                    .append(uid).append(",")
                    .append(interior).append(",")
                    .append(power).append(",")
                    .append(appearence).append(",")
                    .append(costPerformance).append(",")
                    .append(control).append(",")
                    .append(oil).append(",")
                    .append(space).append(",")
                    .append(comfort).append(",")
                    .append(price).append(",")
                    .append(boughtSite).append(",")
                    .append(boughtTime).append(",")
                    .append(boughtSeries).append(",")
                    .append(aim).append(",")
                    .append(koubeiText).append(",")
                    .append(koubeiTime).append(",")
                    .append(koubeiType).append(",")
                    .append(addedkoubeiText).append(",")
                    .append(addedkoubeiTime).append(",")
                    .append(addedkoubeiType).append("\n");
            out.write(koubeiRecord.toString().getBytes());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        out.close();
    }
}
