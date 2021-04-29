class Currency implements Website {
    private String headersUrl = "https://kur.doviz.com/serbest-piyasa/";
    private String boxesAdress = "div[class='header-secondary'] div[class='header-container'] div[class='market-data']";
    private String newsAdress = "div[class='item'] span[class^='value']";
    private String url = "Bloomberg (Currency)";

    Currency() {
    }

    public String getContentHeaderAdress() {
        return null;
    }

    public String getContentTextAdress() {
        return null;
    }

    public String getContent() {
        return null;
    }

    public void setContents(String content) {
    }

    public String getUrl() {
        return null;
    }

    public void setContentUrl(String[] urls) {
    }

    public String[] getContentUrl() {
        return null;
    }

    public String getHeadersUrl() {
        return this.headersUrl;
    }

    public String getContentAdress() {
        return null;
    }

    public String[] getHeaders() {
        return null;
    }

    public void setHeaders(String[] headers) {
    }

    public String getBoxesAdress() {
        return this.boxesAdress;
    }

    public String getNewsAdress() {
        return this.newsAdress;
    }

    public String getAttribute() {
        return null;
    }
}
