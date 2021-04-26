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
        this.doc = Jsoup.connect(this.site.getHeadersUrl()).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36").get();
        Elements mainBox = this.doc.select(this.site.getBoxesAdress());
        if (mainBox.size() > 0) {
            this.boxes = mainBox.select(this.site.getContentAdress());
            this.contentUrls = new String[this.boxes.size()];
            int i = 0;

            Iterator var4;
            Element e;
            for(var4 = this.boxes.iterator(); var4.hasNext(); ++i) {
                e = (Element)var4.next();
                this.contentUrls[i] = e.selectFirst("a").attr(this.site.getAttribute());
            }

            this.site.setContentUrl(this.contentUrls);
            this.boxes = mainBox.select(this.site.getNewsAdress());
            this.news = new String[this.boxes.size()];
            i = 0;

            for(var4 = this.boxes.iterator(); var4.hasNext(); ++i) {
                e = (Element)var4.next();
                this.news[i] = e.text();
            }

            this.site.setHeaders(this.news);
        }

    }

    public void getContents(int index) throws IOException {
        String[] urls = this.site.getContentUrl();
        int i = 0;
        this.doc = Jsoup.connect(this.site.getUrl() + urls[index]).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36").get();
        Elements textBox = this.doc.select(this.site.getContentTextAdress());
        Elements headerBox = this.doc.select(this.site.getContentHeaderAdress());
        String contents;
        if (!(this.site instanceof Euronews) && !(this.site instanceof Reuters)) {
            contents = "\t\tHEADER\t\t\n\n" + headerBox.text() + "\n\n\t\tDETAILS\t\t\n\n" + textBox.text();
            this.site.setContents(contents);
        } else {
            contents = "\t\tHEADER\t\t\n\n" + headerBox.text() + "\n\n\t\tDETAILS\t\t\n\n" + textBox.first().ownText() + textBox.next().next().first().ownText() + textBox.next().next().next().first().ownText();
            this.site.setContents(contents);
        }
    }

    public float[] getCurrency() throws IOException {
        this.doc = Jsoup.connect(this.site.getHeadersUrl()).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36").get();
        Elements mainBox = this.doc.select(this.site.getBoxesAdress());
        if (mainBox.size() > 0) {
            this.boxes = mainBox.select(this.site.getNewsAdress());
            this.currencies = new String[2];
            this.cur = new float[2];
            this.currencies[0] = ((Element)this.boxes.get(0)).ownText();
            float parsed = Float.parseFloat(String.format(this.currencies[0].replaceAll(",", "."), "%1.4f"));
            this.cur[0] = parsed;
            this.currencies[1] = ((Element)this.boxes.get(1)).ownText();
            parsed = Float.parseFloat(this.currencies[1].replaceAll(",", "."));
            this.cur[1] = parsed;
        }

        return this.cur;
    }

    public void connect(String url) throws IOException, URISyntaxException {
        Desktop.getDesktop().browse((new URL(url)).toURI());
    }
}
