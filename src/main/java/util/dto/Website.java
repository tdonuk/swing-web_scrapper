package util.dto;

import java.awt.*;
import java.util.ArrayList;

//util.dao.Website DTO
//The data this file containing will be parsed from a txt file
public class Website {
    private String name;
    private String mainUrl;
    private Image img;
    private String ImageFileName;
    private String headersUrl;
    private String mainListAddress;
    private String headerAddress;
    private String headerLinkAddress;
    private String contentHeaderAddress;
    private String contentDetailsAddress;
    private ArrayList<Header> headers;

    public String getImageFileName() {
        return ImageFileName;
    }

    public void setImageFileName(String imageFileName) {
        ImageFileName = imageFileName;
    }

    public Image getImageFile() {
        return img;
    }

    public void setImageFile(Image image) {
        this.img = image;
    }

    public String getContentHeaderAddress() {
        return contentHeaderAddress;
    }

    public void setContentHeaderAddress(String contentHeaderAddress) {
        this.contentHeaderAddress = contentHeaderAddress;
    }

    public String getContentDetailsAddress() {
        return contentDetailsAddress;
    }

    public void setContentDetailsAddress(String contentDetailsAddress) {
        this.contentDetailsAddress = contentDetailsAddress;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMainUrl(String mainUrl) {
        this.mainUrl = mainUrl;
    }

    public void setHeadersUrl(String headersUrl) {
        this.headersUrl = headersUrl;
    }

    public void setMainListAddress(String mainListAddress) {
        this.mainListAddress = mainListAddress;
    }

    public void setHeaderAddress(String headerAddress) {
        this.headerAddress = headerAddress;
    }

    public void setHeaderLinkAddress(String headerLinkAddress) {
        this.headerLinkAddress = headerLinkAddress;
    }

    public void setHeaders(ArrayList<Header> headers) {
        this.headers = headers;
    }

    public String getName() {
        return name;
    }

    public String getMainUrl() {
        return mainUrl;
    }

    public String getHeadersUrl() {
        return headersUrl;
    }

    public String getMainListAddress() {
        return mainListAddress;
    }

    public String getHeaderAddress() {
        return headerAddress;
    }

    public String getHeaderLinkAddress() {
        return headerLinkAddress;
    }

    public ArrayList<Header> getHeaders() {
        return headers;
    }

}
