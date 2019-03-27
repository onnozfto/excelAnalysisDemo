package com.example.demo.model;

public class RptTemplate {
    private String url;

    private String fileName;

    private byte[] excelTemplate;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName == null ? null : fileName.trim();
    }

    public byte[] getExcelTemplate() {
        return excelTemplate;
    }

    public void setExcelTemplate(byte[] excelTemplate) {
        this.excelTemplate = excelTemplate;
    }
}