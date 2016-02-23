package com.data.crawlerStyle;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailParser {
    // 获取�?��网站上的链接,filter 用来过滤链接
    public static void extracLinks(String styleid, FileOutputStream outstyle)
            throws IOException {

        try {
            URL oneurl = new URL("http://www.autohome.com.cn/spec/" + styleid);
            String styleName = crawlerName(oneurl, styleid);
            String guidence = crawlerPrice(oneurl, styleid);
            String detail = crawlerDetail(oneurl, styleid);
            StringBuffer styleInfo = new StringBuffer();
            styleInfo.append(styleid).append(",").append(styleName).append(",").append(guidence).append(",").append(detail).append("\n");
            outstyle.write(styleInfo.toString().getBytes());
        } catch (ParserException e) {
            e.printStackTrace();
        }
    }

    private static String crawlerName(URL oneurl, String styleid) throws ParserException, IOException {
        String styleName = "null";
        HttpURLConnection connection = (HttpURLConnection) oneurl.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        connection.setRequestProperty("Cookie", "foo=bar");
        Parser parser = new Parser(connection);
        parser.setEncoding("GB2312");
//        NodeFilter filter_name = new AndFilter(new TagNameFilter("span"), new HasAttributeFilter("data-price"));
        NodeFilter filter_name = new TagNameFilter("h1");
        NodeList NodeList_name = parser.extractAllNodesThatMatch(filter_name);
//        System.out.println(NodeList_name.size() + " records;");
        if (NodeList_name.size() == 1) {
            for (int i = 0; i < NodeList_name.size(); i++) {
                Node tag = NodeList_name.elementAt(i);
                String con_name = tag.toHtml();
                styleName = getStyleName(con_name);
//            System.out.println(styleName);
            }
        } else {
            System.out.println(styleid + "name error");
        }
        connection.disconnect();
        return styleName;
    }

    private static String getStyleName(String con_name) {
        int begin = con_name.indexOf('>') + 1;
        String stylePrice = con_name.substring(begin, con_name.indexOf("</"));
        return stylePrice;
    }

    private static String crawlerPrice(URL oneurl, String styleid) throws ParserException, IOException {
        String stylePrice = "null";
        HttpURLConnection connection = (HttpURLConnection) oneurl.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        connection.setRequestProperty("Cookie", "foo=bar");
        Parser parser = new Parser(connection);
        parser.setEncoding("GB2312");
        NodeFilter filter_price = new AndFilter(new TagNameFilter("span"), new HasAttributeFilter("data-price"));
        NodeList NodeList_price = parser.extractAllNodesThatMatch(filter_price);
//        System.out.println(NodeList_price.size() + " records;");
        if (NodeList_price.size() == 1) {
            for (int i = 0; i < NodeList_price.size(); i++) {
                Node tag = NodeList_price.elementAt(i);
                String con_price = tag.toHtml();
                stylePrice = getStylePrice(con_price);
//            System.out.println(stylePrice);
            }
        } else {
            System.out.println(styleid + "price error");
        }
        connection.disconnect();
        return stylePrice;
    }

    private static String getStylePrice(String con_price) {
        int begin = con_price.indexOf('"') + 1;
        String stylePrice = con_price.substring(begin, con_price.indexOf("万"));
        if (stylePrice.indexOf('-') >= 0) {
            Double min = Double.parseDouble(stylePrice.substring(0, stylePrice.indexOf('-')));
            Double max = Double.parseDouble(stylePrice.substring(stylePrice.indexOf('-') + 1));
            stylePrice = ("" + (min + max) / 2 + "0000").substring(0, 4);
        }
        return stylePrice;
    }

    private static String crawlerDetail(URL oneurl, String styleid) throws ParserException, IOException {
        String styleDetail = "null";
        HttpURLConnection connection = (HttpURLConnection) oneurl.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        connection.setRequestProperty("Cookie", "foo=bar");
        Parser parser = new Parser(connection);
        parser.setEncoding("GB2312");
        NodeFilter filter_detail = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "cardetail-infor-car"));
        NodeList NodeList_detail = parser.extractAllNodesThatMatch(filter_detail);
//        System.out.println(NodeList_detail.size() + " records;");
        if (NodeList_detail.size() == 1) {
            for (int i = 0; i < NodeList_detail.size(); i++) {
                Node tag = NodeList_detail.elementAt(i);
                String con_detail = tag.toHtml().replace(" ", "").replace("\n", "").replace("\r", "");
                styleDetail = getStyleDetail(con_detail);
            }
        } else {
            System.out.println(styleid + "detail error");
        }
        connection.disconnect();
        return styleDetail;
    }

    private static String getStyleDetail(String con_detail) {
        return getStyleScore(con_detail) + "," + getStyleOil(con_detail);
    }

    private static String getStyleOil(String con_detail) {
        int ren = con_detail.indexOf("人均");
        if(ren < 0)
            return "null";
        else {
            int end = con_detail.indexOf("<", ren);
            int begin = con_detail.substring(0, ren).lastIndexOf(">") + 1;
            return con_detail.substring(begin, end);
        }
    }

    private static String getStyleScore(String con_detail) {
        int begin = con_detail.indexOf("bold\">") + 6;
        if (begin < 6)
            return "null";
        else {
            int end = con_detail.indexOf("分<");
            return con_detail.substring(begin, end);
        }
    }

}
