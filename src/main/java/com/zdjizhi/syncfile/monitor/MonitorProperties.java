package com.zdjizhi.syncfile.monitor;

import org.springframework.context.annotation.Configuration;

@Configuration
public class MonitorProperties {
    private static Long downloadFileSuccessCount = 0L;
    private static Long downloadFileErrorCount = 0L;
    private static Long postFileSuccessCount = 0L;
    private static Long postFileErrorCount = 0L;
    private static Long downloadFileSize = 0L;
    private static Long postFileSize = 0L;

    private static Long fileSyncError = 0L;
    private static Long hosError = 0L;
    private static Long ossError = 0L;

    public void addDownloadFileSuccessCount() {
        downloadFileSuccessCount = downloadFileSuccessCount + 1;
    }
    public void addDownloadFileErrorCount() {
        downloadFileErrorCount = downloadFileErrorCount + 1;
    }
    public void addPostFileSuccessCount() {
        postFileSuccessCount = postFileSuccessCount + 1;
    }
    public void addPostFileErrorCount() {
        postFileErrorCount = postFileErrorCount + 1;
    }
    public void addDownloadFileSize(long fileSize) {
        downloadFileSize = downloadFileSize + fileSize;
    }
    public void addPostFileSize(long fileSize) {
        postFileSize = postFileSize + fileSize;
    }
    public void addFileSyncError() {
        fileSyncError = fileSyncError + 1;
    }
    public void addHosError() {
        hosError = hosError + 1;
    }
    public void addOssError() {
        ossError = ossError + 1;
    }

    public Long getDownloadFileSuccessCount() {
        return downloadFileSuccessCount;
    }

    public Long getDownloadFileErrorCount() {
        return downloadFileErrorCount;
    }

    public Long getPostFileSuccessCount() {
        return postFileSuccessCount;
    }

    public Long getPostFileErrorCount() {
        return postFileErrorCount;
    }

    public Long getDownloadFileSize() {
        return downloadFileSize;
    }

    public Long getPostFileSize() {
        return postFileSize;
    }

    public Long getFileSyncError() {
        return fileSyncError;
    }

    public Long getHosError() {
        return hosError;
    }

    public Long getOssError() {
        return ossError;
    }
}
