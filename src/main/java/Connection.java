import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;

public class Connection {
    private Website site;
    private Document newsDoc,currencyDoc,doc;
    private Elements boxes;
    private String[] news;
    private String[] currencies;
    private String[] contents;
    private String[] contentUrls;
    private float[] cur;

    public Connection(Website site) {
        this.site = site;
    }

    public void getNews() throws IOException {
        newsDoc = Jsoup.connect(site.getHeadersUrl()).get();
        Elements mainBox = newsDoc.select(site.getBoxesAdress());
        if (mainBox.size() > 0) {
            boxes = mainBox.select(site.getContentAdress());
            contentUrls = new String[boxes.size()];
            int i = 0;
            
            for(Element e : boxes) {
                contentUrls[i] = e.selectFirst("a").attr(site.getAttribute());
                i++;
            }

            site.setContentUrl(contentUrls);
            boxes = mainBox.select(site.getNewsAdress());
            news = new String[boxes.size()];

            i = 0;
            for(Element e : boxes) {
                news[i] = e.text();
                i++;
            }

            site.setHeaders(news);
        }

    }

    public void getContents(int index) throws IOException {
        String[] urls = site.getContentUrl();
        int i = 0;
        doc = Jsoup.connect(site.getUrl() + urls[index]).get();
        Elements textBox = doc.select(site.getContentTextAdress());
        Elements headerBox = doc.select(site.getContentHeaderAdress());
        String contents;
        if (!(site instanceof Euronews) && !(site instanceof Reuters)) {
            contents = "HEADER\n\n" + headerBox.text() + "\n\nDETAILS\n\n" + textBox.text();
            site.setContents(contents);
        } else {
            contents = "HEADER\n\n" + headerBox.text() + "\n\nDETAILS\n\n" + textBox.first().ownText() + textBox.next().next().first().ownText() + textBox.next().next().next().first().ownText();
            site.setContents(contents);
        }
    }

    public float[] getCurrency() throws IOException {
        currencyDoc = Jsoup.connect(site.getHeadersUrl()).get();
        Elements mainBox = currencyDoc.select(site.getBoxesAdress());
        if (mainBox.size() > 0) {
            boxes = mainBox.select(site.getNewsAdress());
            currencies = new String[5];
            cur = new float[currencies.length];

            currencies[0] = boxes.get(1).ownText();
            float parsed = Float.parseFloat(String.format(currencies[0].replaceAll(",", "."), "%.4f"));
            cur[0] = parsed;

            currencies[1] = boxes.get(2).ownText();
            parsed = Float.parseFloat(String.format(currencies[1].replaceAll(",", "."), "%.4f"));
            cur[1] = parsed;

            currencies[2] = boxes.get(7).ownText();
            parsed = Float.parseFloat(String.format(currencies[2].replaceAll(",", "."), "%.4f"));
            cur[2] = parsed;

            currencies[3] = boxes.get(3).ownText();
            parsed = Float.parseFloat(String.format(currencies[3].replaceAll(",", "."), "%.4f"));
            cur[3] = parsed;

            currencies[4] = boxes.get(0).ownText();
            parsed = Float.parseFloat(String.format(currencies[4].replaceAll(",", "."), "%.4f"));
            cur[4] = parsed;
        }

        return cur;
    }

    public void connect(String url) throws IOException, URISyntaxException {
        Desktop.getDesktop().browse((new URL(url)).toURI());
    }
}
