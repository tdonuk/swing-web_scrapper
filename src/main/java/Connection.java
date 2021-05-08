import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;

public class Connection {
    private Document contentsDoc,newsDoc,currencyDoc;
    private Elements mainList;
    private String[] currencies;
    private Header header;
    private JOptionPane errorPane;
    private float[] cur;

    public Connection() {
        errorPane = new JOptionPane();
        errorPane.createDialog(null,"Connection error");
    }

    public void getNews(Website site) {
        ArrayList<Header> news = new ArrayList<>();

        try {
            newsDoc =Jsoup.connect(site.getMainUrl()+site.getHeadersUrl()).get();
        } catch (IOException e) {
            errorPane.setMessage("Not possible to connect: "+site.getMainUrl());
            errorPane.setMessageType(JOptionPane.ERROR_MESSAGE);
            errorPane.setVisible(true);
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

            news.add(header);
        }
        site.setHeaders(news);
    }

    public void getContent(Header header , Website site) {
        if(newsDoc.equals(null) || header.getLink().equals(null)) {
            errorPane.setMessage("URL not found");
            errorPane.setVisible(true);
            return;
        }

        try {
            contentsDoc = Jsoup.connect(header.getLink()).get();
        } catch (IOException e) {
            errorPane.setMessage("Corrupted link or no internet connection. \nThe link: "+header.getLink());
            errorPane.setVisible(true);
            return;
        }

        header.setContentHeader(contentsDoc.select(site.getContentHeaderAddress()).text());
        header.setContentDetails(contentsDoc.select(site.getContentDetailsAddress()).text());
    }

    public float[] getCurrency() throws IOException {
        Currency currencyService = new Currency();
        Currency curService = new Currency();
        currencyDoc = Jsoup.connect(currencyService.getHeadersUrl()).get();
        Elements mainBox = currencyDoc.select(curService.getBoxesAdress());
        if (mainBox.size() > 0) {
            mainList = mainBox.select(curService.getNewsAdress());
            currencies = new String[5];
            cur = new float[currencies.length];

            currencies[0] = mainList.get(1).ownText();
            float parsed = Float.parseFloat(String.format(currencies[0].replaceAll(",", "."), "%.4f"));
            cur[0] = parsed;

            currencies[1] = mainList.get(2).ownText();
            parsed = Float.parseFloat(String.format(currencies[1].replaceAll(",", "."), "%.4f"));
            cur[1] = parsed;

            currencies[2] = mainList.get(7).ownText();
            parsed = Float.parseFloat(String.format(currencies[2].replaceAll(",", "."), "%.4f"));
            cur[2] = parsed;

            currencies[3] = mainList.get(3).ownText();
            parsed = Float.parseFloat(String.format(currencies[3].replaceAll(",", "."), "%.4f"));
            cur[3] = parsed;

            currencies[4] = mainList.get(0).ownText();
            parsed = Float.parseFloat(String.format(currencies[4].replaceAll(",", "."), "%.4f"));
            cur[4] = parsed;
        }

        return cur;
    }

    public void browse(Header header) throws IOException, URISyntaxException {
        Desktop.getDesktop().browse((new URL(header.getLink())).toURI());
    }
}