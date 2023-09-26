package com.zdjizhi.syncfile.entity;

import java.util.List;

public class SysFileSync {
    private List<Source> source_list;
    private long common_log_id;
    private long common_recv_time;
    private String common_schema_type;
    private long processing_time;

    public List<Source> getSource_list() {
        return source_list;
    }

    public void setSource_list(List<Source> source_list) {
        this.source_list = source_list;
    }

    public long getCommon_log_id() {
        return common_log_id;
    }

    public void setCommon_log_id(long common_log_id) {
        this.common_log_id = common_log_id;
    }

    public String getCommon_schema_type() {
        return common_schema_type;
    }

    public void setCommon_schema_type(String common_schema_type) {
        this.common_schema_type = common_schema_type;
    }

    public long getCommon_recv_time() {
        return common_recv_time;
    }

    public void setCommon_recv_time(long common_recv_time) {
        this.common_recv_time = common_recv_time;
    }

    public long getProcessing_time() {
        return processing_time;
    }

    public void setProcessing_time(long processing_time) {
        this.processing_time = processing_time;
    }
}
