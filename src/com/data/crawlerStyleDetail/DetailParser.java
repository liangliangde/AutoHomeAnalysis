package com.data.crawlerStyleDetail;

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
    public static void extracLinks(String seriesId)
            throws IOException {

        try {
            URL oneurl = new URL("http://car.autohome.com.cn/config/series/" + seriesId + ".html");
            crawlerPrice(oneurl, seriesId);
        } catch (ParserException e) {
            e.printStackTrace();
        }
    }

    private static void crawlerPrice(URL oneurl, String seriesId) throws ParserException, IOException {
        HttpURLConnection connection = (HttpURLConnection) oneurl.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        connection.setRequestProperty("Cookie", "foo=bar");
        Parser parser = new Parser(connection);
        parser.setEncoding("GB2312");
        NodeFilter filter = new AndFilter(new TagNameFilter("script"), new HasAttributeFilter("type", "text/javascript"));
        NodeList NodeList = parser.extractAllNodesThatMatch(filter);
        int flag = 0;
        String con = "";
        for (int i = 0; i < NodeList.size(); i++) {
            Node tag = NodeList.elementAt(i);
            con = tag.toHtml();
            if (con.contains("keyLink") && con.contains("config") && con.contains("option")) {
                flag++;
                break;
            }
        }
        if (flag != 1) {
            System.out.println("," + flag + ",error");
        } else {
            FileOutputStream outstyle = new FileOutputStream("/home/llei/IdeaProjects/autohome/styleDetail/" + seriesId, true);
            outstyle.write(con.getBytes());
            outstyle.close();
            System.out.println("");
        }
        connection.disconnect();
    }

}
