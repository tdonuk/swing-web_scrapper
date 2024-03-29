package util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.dto.*;
import javax.swing.JOptionPane;

public class Connection {
    private Document contentsDoc,newsDoc,currencyDoc;
    private Elements mainList;
    private String[] currencies;
    private Header header;
    private float[] cur;

    public void getNews(Website site) {
        ArrayList<Header> news = new ArrayList<>();

        try {
            String url = site.getMainUrl() + site.getHeadersUrl();
            newsDoc =Jsoup.connect(url).get();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,"Corrupted Main Page URL", "Connection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        mainList = newsDoc.select(site.getMainListAddress());

        if(mainList.size() == 0) {
            JOptionPane.showMessageDialog(null,"Main list address is incorrect", "Parse error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String link = "";
        for(Element e : mainList) {
            header = new Header();
            //Parsing the header of the new
            header.setHeader(e.select(site.getHeaderAddress()).text());

            //Parsing the link for the corresponding header
            link = e.select(site.getHeaderLinkAddress()).attr("href");

            if(link.startsWith(site.getMainUrl())) { //then, the link is full: not including only the endpoint of the corresponding header
                header.setLink(link);
            } else{
                header.setLink(site.getMainUrl()+link);
            }
            if(header.getHeader().equals("") || link.equals("")) continue;
            news.add(header);
        }

        site.setHeaders(news);
    }

    public void getContent(Header header , Website site) {
        if(newsDoc.equals(null) || header.getLink().equals(null)) {
            return;
        }

        try {
            contentsDoc = Jsoup.connect(header.getLink()).get();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,"Corrupted link or no internet connection. \nThe link: "+header.getLink(),"Error",JOptionPane.ERROR_MESSAGE);
            return;
        }

        header.setContentHeader(contentsDoc.select(site.getContentHeaderAddress()).text());
        header.setContentDetails(contentsDoc.select(site.getContentDetailsAddress()).text());
    }

    public float[] getCurrency() throws IOException {
        Currency currencyService = new Currency();
        currencyDoc = Jsoup.connect(currencyService.getHeadersUrl()).get();
        Elements mainBox = currencyDoc.select(currencyService.getBoxesAdress());
        if (mainBox.size() > 0) {
            mainList = mainBox.select(currencyService.getNewsAdress());
            currencies = new String[5];
            cur = new float[currencies.length];

            currencies[0] = mainList.get(1).ownText();
            float parsed = Float.parseFloat(String.format(currencies[0].replaceAll(",", "."), "%.5f"));
            cur[0] = parsed;

            currencies[1] = mainList.get(2).ownText();
            parsed = Float.parseFloat(String.format(currencies[1].replaceAll(",", "."), "%.5f"));
            cur[1] = parsed;

            currencies[2] = mainList.get(7).ownText();
            parsed = Float.parseFloat(String.format(currencies[2].replaceAll(",", "."), "%.5f"));
            cur[2] = parsed;

            currencies[3] = mainList.get(3).ownText();
            parsed = Float.parseFloat(String.format(currencies[3].replaceAll(",", "."), "%.5f"));
            cur[3] = parsed;

            currencies[4] = mainList.get(0).ownText();
            parsed = Float.parseFloat(String.format(currencies[4].replaceAll(",", "."), "%.5f"));
            cur[4] = parsed;
        }

        return cur;
    }

    public void browse(Header header) throws IOException, URISyntaxException {
        Desktop.getDesktop().browse((new URL(header.getLink())).toURI());
    }
}
