package com.data.crawlerStyleDetail;

import java.io.*;

public class CrawlerStyleDetail {

    public static void main(String[] args) throws IOException, InterruptedException {
        DetailParser conparser = new DetailParser();
        File file = new File("auto_data/series.csv");

        if (file.isFile() && file.exists()) { // �ж��ļ��Ƿ����
            InputStreamReader read = new InputStreamReader(new FileInputStream(
                    file), "UTF-8");// ���ǵ������ʽ ,���ļ��ж������ڴ�
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                String seriesId = lineTxt.substring(0, lineTxt.indexOf(","));
                System.out.print(seriesId);

                conparser.extracLinks(seriesId);
            }
        }
        System.out.println("!job finish!");
    }
}
