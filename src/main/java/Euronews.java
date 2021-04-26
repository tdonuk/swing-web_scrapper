class Euronews implements Website {
    private String url = "https://tr.euronews.com";
    private String headersUrl = "https://tr.euronews.com/haber/avrupa/turkiye";
    private String contentAdress = "article div[class='m-object__body'] h3";
    private String boxesAdress = "div[class='o-block-listing__articles']";
    private String[] headers;
    private String newsAdress = "article div[class='m-object__body'] h3 a";
    private String attribute = "href";
    private String contents;
    private String[] contentLinks;
    private String contentTextAdress = "div[class='c-article-content  js-article-content article__content'] p";
    private String contentHeaderAdress = "h1[class='c-article-title']";

    Euronews() {
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
