package com.data.crawlerSeries;

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
    public static void extracLinks(String seriesId, FileOutputStream outseries, String seriesName)
            throws IOException {

        try {
            URL oneurl = new URL("http://www.autohome.com.cn/" + seriesId);
            String price = crawlerPrice(oneurl, seriesId);
            String detail = crawlerType(oneurl, seriesId);
            StringBuffer seriesInfo = new StringBuffer();
            seriesInfo.append(seriesId+","+seriesName+","+price+","+detail+"\n");
            outseries.write(seriesInfo.toString().getBytes());
        } catch (ParserException e) {
            e.printStackTrace();
        }
    }

    private static String crawlerPrice(URL oneurl, String seriesId) throws ParserException, IOException {
        String seriesPrice = "null";
        HttpURLConnection connection = (HttpURLConnection) oneurl.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        connection.setRequestProperty("Cookie", "foo=bar");
        Parser parser = new Parser(connection);
        parser.setEncoding("GB2312");
        NodeFilter filter_price = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "autoseries-info"));
        NodeList NodeList_price = parser.extractAllNodesThatMatch(filter_price);
//        System.out.println(NodeList_price.size() + " records;");
        if (NodeList_price.size() == 1) {
            for (int i = 0; i < NodeList_price.size(); i++) {
                Node tag = NodeList_price.elementAt(i);
                String con_price = tag.toHtml().replace(" ", "").replace("\n", "").replace("\r", "");
                seriesPrice = getSeriesPrice(con_price);
                System.out.println(seriesPrice);
            }
        } else {
            System.out.println(seriesId + "price error");
        }
        connection.disconnect();
        return seriesPrice;
    }

    private static String getSeriesPrice(String con_price) {
        int begin = con_price.indexOf("red\">")+5;
        if(begin < 5)
            return "null";
        int end = con_price.indexOf("万",begin)+1;
        String price = con_price.substring(begin,end);
        return price;
    }

    private static String crawlerType(URL oneurl, String seriesId) throws ParserException, IOException {
        String seriesType = "null";
        HttpURLConnection connection = (HttpURLConnection) oneurl.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        connection.setRequestProperty("Cookie", "foo=bar");
        Parser parser = new Parser(connection);
        parser.setEncoding("GB2312");
        NodeFilter filter_type = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "subnav-title-rank"));
        NodeList NodeList_type = parser.extractAllNodesThatMatch(filter_type);
//        System.out.println(NodeList_type.size() + " records;");
        if (NodeList_type.size() == 1) {
            for (int i = 0; i < NodeList_type.size(); i++) {
                Node tag = NodeList_type.elementAt(i);
                String con_type = tag.toHtml().replace(" ", "").replace("\n", "").replace("\r", "");
                seriesType = getSeriesType(con_type);
                System.out.println(seriesType);
            }
        } else {
            System.out.println(seriesId + "type error");
        }
        connection.disconnect();
        return seriesType;
    }

    private static String getSeriesType(String con_type) {
        int begin = con_type.indexOf("rank\"")+6;
        int end = con_type.indexOf("关注",begin);
        return con_type.substring(begin, end);
    }

}
