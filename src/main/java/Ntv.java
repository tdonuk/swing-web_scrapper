class Ntv implements Website {
    private String url = "https://www.ntv.com.tr";
    private String headersUrl = "https://www.ntv.com.tr/son-dakika";
    private String contentAdress = "div div p";
    private String boxesAdress = "div[class='ntv-content-inner'] ul[class^='gallery-page-video-list-items']";
    private String[] headers;
    private String newsAdress = "li div div p a";
    private String attribute = "href";
    private String contents;
    private String[] contentLinks;
    private String contentTextAdress = "div[class='category-detail-content-inner'] p";
    private String contentHeaderAdress = "h2[class='category-detail-sub-title']";

    Ntv() {
    }

    public String getContentHeaderAdress() {
        return this.contentHeaderAdress;
    }

    public String getContentTextAdress() {
        return this.contentTextAdress;
    }

    public String getUrl() {
        return this.url;
    }

    public void setContentUrl(String[] urls) {
        this.contentLinks = urls;
    }

    public String[] getContentUrl() {
        return this.contentLinks;
    }

    public String getContent() {
        return this.contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getHeadersUrl() {
        return this.headersUrl;
    }

    public String getContentAdress() {
        return this.contentAdress;
    }

    public String[] getHeaders() {
        return this.headers;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }

    public String getBoxesAdress() {
        return this.boxesAdress;
    }

    public String getNewsAdress() {
        return this.newsAdress;
    }

    public String getAttribute() {
        return this.attribute;
    }
}
