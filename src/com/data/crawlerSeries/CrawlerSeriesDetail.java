package com.data.crawlerSeries;

import java.io.*;

public class CrawlerSeriesDetail {

    public static void main(String[] args) throws IOException, InterruptedException {
        DetailParser conparser = new DetailParser();
        FileOutputStream outstyle = new FileOutputStream("series.csv", true);
        File file = new File("/home/llei/IdeaProjects/autohome/auto_data/series.csv");

        if (file.isFile() && file.exists()) { // �ж��ļ��Ƿ����
            InputStreamReader read = new InputStreamReader(new FileInputStream(
                    file), "UTF-8");// ���ǵ������ʽ ,���ļ��ж������ڴ�
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                String styleid = lineTxt.substring(0, lineTxt.indexOf(","));
                String styleName = lineTxt.substring(lineTxt.indexOf(",")+1);
                System.out.println(styleid);
                conparser.extracLinks(styleid, outstyle, styleName);
            }
        }
        outstyle.close();
        System.out.println("!job finish!");
    }
}
