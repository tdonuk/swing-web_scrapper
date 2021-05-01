class Haberturk implements Website {
    private String url = "https://www.haberturk.com";
    private String headersUrl = "https://www.haberturk.com/gundem";
    private String contentAdress = "div[class='box-xs-6 box-sm-6 box-md-6 box-lg-6']";
    private String boxesAdress = "div[class='box-row clear-box']";
    private String newsAdress = "div[class='box-xs-6 box-sm-6 box-md-6 box-lg-6'] a h3";
    private String[] headers;
    private String attribute = "href";
    private String contents;
    private String[] contentLinks;
    private String contentTextAdress = "article[class='content type1'] p";
    private String contentHeaderAdress = "h2[class='spot-title']";

    Haberturk() {
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
