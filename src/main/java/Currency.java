class Currency implements Website {
    private String headersUrl = "https://www.bloomberght.com/doviz";
    private String boxesAdress = "div[class='header'] div[class='widget-economy-data type1'] div[id='HeaderMarkets']";
    private String newsAdress = "ul li a[href^='/doviz'] span[class='data-info'] small[data-type='son_fiyat']";

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
