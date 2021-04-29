import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Connection {
    private Website site;
    private Document doc;
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
        doc = Jsoup.connect(site.getHeadersUrl()).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36").get();
        Elements mainBox = doc.select(site.getBoxesAdress());
        if (mainBox.size() > 0) {
            boxes = mainBox.select(site.getContentAdress());
            contentUrls = new String[boxes.size()];
            int i = 0;

            Iterator iterator;
            Element e;
            for(iterator = boxes.iterator(); iterator.hasNext(); ++i) {
                e = (Element)iterator.next();
                contentUrls[i] = e.selectFirst("a").attr(site.getAttribute());
            }

            site.setContentUrl(contentUrls);
            boxes = mainBox.select(site.getNewsAdress());
            news = new String[boxes.size()];
            i = 0;

            for(iterator = boxes.iterator(); iterator.hasNext(); ++i) {
                e = (Element)iterator.next();
                news[i] = e.text();
            }

            site.setHeaders(news);
        }

    }

    public void getContents(int index) throws IOException {
        String[] urls = site.getContentUrl();
        int i = 0;
        doc = Jsoup.connect(site.getUrl() + urls[index]).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36").get();
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
        doc = Jsoup.connect(site.getHeadersUrl()).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36").get();
        Elements mainBox = doc.select(site.getBoxesAdress());
        if (mainBox.size() > 0) {
            boxes = mainBox.select(site.getNewsAdress());
            currencies = new String[4];
            cur = new float[4];

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
        }

        return cur;
    }

    public void connect(String url) throws IOException, URISyntaxException {
        Desktop.getDesktop().browse((new URL(url)).toURI());
    }
}
