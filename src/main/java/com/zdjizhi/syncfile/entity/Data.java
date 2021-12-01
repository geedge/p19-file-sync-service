package com.zdjizhi.syncfile.entity;

public class Data {
    private int fileSize;
    private String sign;
    private String state;

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Data{" +
                "fileSize=" + fileSize +
                ", sign='" + sign + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}