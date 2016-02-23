package com.data.crawlerStyle;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CrawlerStyleList2 {
	public static List<String> existStyleidList;
	public static void main(String[] args) throws IOException, InterruptedException{
		ListParser2 conparser=new ListParser2();
		FileOutputStream outseries_style= new FileOutputStream("auto_data/series_style2.csv",true);
		FileOutputStream outstyle_error= new FileOutputStream("auto_data/style_error.csv",true);
		File file=new File("auto_data/series.csv");
		existStyleidList = getExistStyleid();

		if (file.isFile() && file.exists()) { // �ж��ļ��Ƿ����
			InputStreamReader read = new InputStreamReader(new FileInputStream(
					file), "UTF-8");// ���ǵ������ʽ ,���ļ��ж������ڴ�
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			while((lineTxt = bufferedReader.readLine()) != null){
				String seriesid = lineTxt.substring(0, lineTxt.indexOf(","));
				conparser.extracLinks(seriesid, outseries_style);
			}
		}
		outseries_style.close();
		outstyle_error.close();
		System.out.println("!job finish!");
	}

	private static List<String> getExistStyleid() throws IOException {
		List<String> existStyleid = new ArrayList<>();
		File file = new File("auto_data/series_style.csv");
		InputStreamReader read = new InputStreamReader(new FileInputStream(
				file), "UTF-8");// ���ǵ������ʽ ,���ļ��ж������ڴ�
		BufferedReader bufferedReader = new BufferedReader(read);
		String lineTxt = null;
		while ((lineTxt = bufferedReader.readLine()) != null) {
			existStyleid.add(lineTxt.substring(lineTxt.indexOf(",") + 1));
		}
		return existStyleid;
	}
}
