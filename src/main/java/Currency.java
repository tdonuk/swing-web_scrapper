class Currency {
    private String headersUrl = "https://kur.doviz.com/serbest-piyasa/";
    private String boxesAdress = "div[class='header-secondary'] div[class='header-container'] div[class='market-data']";
    private String newsAdress = "div[class='item'] span[class^='value']";
    private String url = "Bloomberg (Currency)";

    public String getHeadersUrl() {
        return this.headersUrl;
    }

    public String getBoxesAdress() {
        return this.boxesAdress;
    }

    public String getNewsAdress() {
        return this.newsAdress;
    }

}
