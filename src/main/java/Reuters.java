class Reuters implements Website {
    private String url = "https://www.reuters.com";
    private String headersUrl = "https://www.reuters.com/breakingviews";
    private String contentAdress = "article div[class='story-content']";
    private String boxesAdress = "div[class='news-headline-list  ']";
    private String[] headers;
    private String newsAdress = "article div[class='story-content'] a h3";
    private String attribute = "href";
    private String contents;
    private String[] contentLinks;
    private String contentTextAdress = "div[class='StandardArticleBody_body'] p";
    private String contentHeaderAdress = "h1[class='ArticleHeader_headline']";

    Reuters() {
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
