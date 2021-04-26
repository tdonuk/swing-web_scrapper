class Cnn implements Website {
    private String url = "https://www.cnnturk.com";
    private String headersUrl = "https://www.cnnturk.com/sondakika/turkiye";
    private String contentAdress = "li[class='media']";
    private String boxesAdress = "div[class='row  sticky-container'] div[class='col-md-8 ']";
    private String[] headers;
    private String newsAdress = "ul li a div[class='media-body'] div";
    private String attribute = "href";
    private String contents;
    private String[] contentLinks;
    private String contentTextAdress = "div[data-io-article-url*='/'] p";
    private String contentHeaderAdress = "div[class='content-detail'] h2";

    Cnn() {
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
