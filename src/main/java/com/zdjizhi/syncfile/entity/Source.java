package com.zdjizhi.syncfile.entity;

public class Source {

    private String source_oss_path;
    private String destination_oss_path;

    public Source(String source_oss_path, String destination_oss_path) {
        this.source_oss_path = source_oss_path;
        this.destination_oss_path = destination_oss_path;
    }

    public String getSource_oss_path() {
        return source_oss_path;
    }

    public void setSource_oss_path(String source_oss_path) {
        this.source_oss_path = source_oss_path;
    }

    public String getDestination_oss_path() {
        return destination_oss_path;
    }

    public void setDestination_oss_path(String destination_oss_path) {
        this.destination_oss_path = destination_oss_path;
    }
}
