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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListParser {
    // 获取�?��网站上的链接,filter 用来过滤链接
    public static void extracLinks(String seriesid, FileOutputStream outseries_style)
            throws IOException {

        try {
            URL oneurl = new URL("http://www.autohome.com.cn/" + seriesid);
            List<String> styleid_list = crawlerStyleList(oneurl, seriesid, outseries_style);
        } catch (ParserException e) {
            e.printStackTrace();
        }
    }

    private static List<String> crawlerStyleList(URL oneurl, String seriesid, FileOutputStream outseries_style) throws ParserException, IOException {
        HttpURLConnection connection = (HttpURLConnection) oneurl.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        connection.setRequestProperty("Cookie", "foo=bar");
        Parser parser = new Parser(connection);
        parser.setEncoding("utf-8");
        NodeFilter filter_cars = new AndFilter(new TagNameFilter("div"), new AndFilter(new HasAttributeFilter("class", "interval01"),
                new HasAttributeFilter("id", "speclist20")));
        NodeList list_cars = parser.extractAllNodesThatMatch(filter_cars);
//        System.out.println(seriesid + "=" + list_cars.size() + "rescords;");
        List<String> styleid_list = new ArrayList<>();
        for (int i = 0; i < list_cars.size(); i++) {
            Node tag = list_cars.elementAt(i);
            String con_cars = tag.toHtml();
            styleid_list = getStyleidList(con_cars, seriesid);
            writeSeries_style(seriesid, styleid_list, outseries_style);
        }
        connection.disconnect();
        return styleid_list;
    }

    private static List<String> getStyleidList(String con_cars, String seriesid) {
        List<String> styleid_list = new ArrayList<>();
        int begin = con_cars.indexOf("data-list") + 11;
        String styleidString = con_cars.substring(begin, con_cars.indexOf("\"", begin));
        if (styleidString.equals("0")) {
            System.out.println(seriesid);
            return styleid_list;
        }
        String[] styleid_arr = styleidString.split(",");
        styleid_list = Arrays.asList(styleid_arr);
        return styleid_list;
    }


    private static void writeSeries_style(String seriesid, List<String> styleid_list, FileOutputStream outseries_style) throws IOException {
        for (int i = 0; i < styleid_list.size(); i++) {
            StringBuffer series_style = new StringBuffer();
            series_style.append(seriesid).append(",").append(styleid_list.get(i)).append("\n");
            outseries_style.write(series_style.toString().getBytes());
        }
    }
}
