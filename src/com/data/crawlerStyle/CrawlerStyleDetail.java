package com.data.crawlerStyle;

import java.io.*;

public class CrawlerStyleDetail {

    public static void main(String[] args) throws IOException, InterruptedException {
        DetailParser conparser = new DetailParser();
        FileOutputStream outstyle = new FileOutputStream("auto_data/style.csv", true);
        File file = new File("auto_data/series_style2.csv");

        if (file.isFile() && file.exists()) { // �ж��ļ��Ƿ����
            InputStreamReader read = new InputStreamReader(new FileInputStream(
                    file), "UTF-8");// ���ǵ������ʽ ,���ļ��ж������ڴ�
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                String styleid = lineTxt.substring(lineTxt.indexOf(",") + 1);
                conparser.extracLinks(styleid, outstyle);
            }
        }
        outstyle.close();
        System.out.println("!job finish!");
    }
}
