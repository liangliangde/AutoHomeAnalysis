package com.data.process;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.IO.IOProcess.readFile;
import static com.IO.IOProcess.writeFile;

/**
 * Created by llei on 16-3-4.
 */
public class MakeSeriesAttr {
    public static void main(String args[]) throws IOException {
        List<String[]> seriesList = readFile("/home/llei/IdeaProjects/autohome/auto_data/series.csv");
//        String attrName[] = {"车系id", "车系", "价格", "车型", "工信部综合油耗(L/100km)", "品牌"};
//        String attrList = makeAttrList(seriesList, attrName);
//        writeFile("/home/llei/IdeaProjects/autohome/auto_data/seriesAttr.csv", attrList);
        String seriesId_attr_List = makeSeriesId_Attr(seriesList);
        writeFile("/home/llei/IdeaProjects/autohome/auto_data/seriesId_attr.csv", seriesId_attr_List);
    }

    private static String makeSeriesId_Attr(List<String[]> seriesList) {
        StringBuffer seriesId_attr = new StringBuffer();
        for (String[] attr : seriesList) {
            String seriesId = attr[0];
            String price = attr[2];
            String type = attr[3];
            String oilCost = attr[4];
            String brand = attr[5];
            System.out.println(seriesId);
            if (!price.equals("null")) {
                seriesId_attr.append(categoryPrice(seriesId, price));
            }
            if (!type.equals("null")) {
                seriesId_attr.append(seriesId + ",[车型]"+type + "\n");
            }
            if (!oilCost.equals("null")) {
                seriesId_attr.append(categoryOil(seriesId, oilCost));
            }
            if (!brand.equals("null")) {
                seriesId_attr.append(seriesId+",[品牌]"+brand + "\n");
            }
        }
        return seriesId_attr.toString();
    }

    private static String categoryOil(String seriesId, String oilCost) {
        int priceCate[] = {0, 3, 6, 9, 12, 15, 10000};
        String priceCateStr[] = {"[工信部综合油耗(L/100km)]3以下", "[工信部综合油耗(L/100km)]3-6", "[工信部综合油耗(L/100km)]6-9", "[工信部综合油耗(L/100km)]9-12", "[工信部综合油耗(L/100km)]12-15", "[工信部综合油耗(L/100km)]15以上"};
        StringBuffer result = new StringBuffer();
        Double cost = Double.parseDouble(oilCost.substring(0, oilCost.indexOf("(")));
        for (int i = 0; i < priceCate.length; i++) {
            if (priceCate[i] <= cost && cost < priceCate[i + 1]) {
                result.append(seriesId + "," + priceCateStr[i] + "\n");
                break;
            }
        }
//        System.out.println(result.toString());
        return result.toString();
    }

    private static String categoryPrice(String seriesId, String price) {
        int priceCate[] = {0, 5, 8, 10, 15, 20, 25, 35, 50, 70, 100, 10000};
        String priceCateStr[] = {"[价格]5万以下", "[价格]5-8万", "[价格]8-10万", "[价格]10-15万", "[价格]15-20万", "[价格]20-25万", "[价格]25-35万", "[价格]35-50万", "[价格]50-70万", "[价格]70-100万", "[价格]100万以上"};
        StringBuffer result = new StringBuffer();
        if (price.indexOf("-") > -1) {
            Double min = Double.parseDouble(price.substring(0, price.indexOf("-")));
            Double max = Double.parseDouble(price.substring(price.indexOf("-") + 1, price.indexOf("万")));
            int left = 0, right = 0;
            for (int i = 0; i < priceCate.length; i++) {
                if (priceCate[i] <= min && min < priceCate[i + 1]) {
                    left = i;
                }
                if (priceCate[i] < max && max <= priceCate[i + 1]) {
                    right = i + 1;
                }
            }
            for (int i = left; i < right; i++) {
                result.append(seriesId + "," + priceCateStr[i] + "\n");
            }
        }
        else{
            Double price_double = Double.parseDouble(price.substring(0, price.indexOf("万")));
            for (int i = 0; i < priceCate.length; i++) {
                if (priceCate[i] <= price_double && price_double < priceCate[i + 1]) {
                    result.append(seriesId + "," + priceCateStr[i] + "\n");
                    break;
                }
            }
        }
        System.out.println(result.toString());
        return result.toString();
    }

    private static String makeAttrList(List<String[]> seriesList, String[] attrName) {
        StringBuffer attrList = new StringBuffer();
        Map<String, String> attrMap = new TreeMap<>();
        for (String[] attr : seriesList) {
            if (!attr[3].equals("null")) {
                attrMap.put("[" + attrName[3] + "]" + attr[3], "");
            }
            if (!attr[5].equals("null")) {
                attrMap.put("[" + attrName[5] + "]" + attr[5], "");
            }
        }
        for (Map.Entry<String, String> entry : attrMap.entrySet()) {
            attrList.append(entry.getKey() + "\n");
        }
        attrList.append("[价格]5万以下\n" +
                "[价格]5-8万\n" +
                "[价格]8-10万\n" +
                "[价格]10-15万\n" +
                "[价格]15-20万\n" +
                "[价格]20-25万\n" +
                "[价格]25-35万\n" +
                "[价格]35-50万\n" +
                "[价格]50-70万\n" +
                "[价格]70-100万\n" +
                "[价格]100万以上\n" +
                "[工信部综合油耗(L/100km)]3以下\n" +
                "[工信部综合油耗(L/100km)]3-6\n" +
                "[工信部综合油耗(L/100km)]6-9\n" +
                "[工信部综合油耗(L/100km)]9-12\n" +
                "[工信部综合油耗(L/100km)]12-15\n" +
                "[工信部综合油耗(L/100km)]15以上");
        return attrList.toString();
    }
}
