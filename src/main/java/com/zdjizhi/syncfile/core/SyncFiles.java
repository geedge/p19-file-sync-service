package com.zdjizhi.syncfile.core;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zdjizhi.syncfile.entity.Source;
import com.zdjizhi.syncfile.monitor.MonitorProperties;
import com.zdjizhi.syncfile.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SyncFiles {
    private Log log = LogFactory.get();

    @Autowired
    private HttpUtil httpUtil;
    @Autowired
    MonitorProperties monitorProperties;

    public void syncFiles(List<List<Source>> fileList) {
        try {
            for (List<Source> sourceList : fileList) {
                for (Source source : sourceList) {
                    String source_oss_path = source.getSource_oss_path();
                    String destination_oss_path = source.getDestination_oss_path();
                    if (source_oss_path != null && !"".equals(source_oss_path) && destination_oss_path != null && !"".equals(destination_oss_path)) {
                        byte[] file = httpUtil.httpGetFile(source_oss_path);
                        if (file != null) {
                            boolean isSuccess = httpUtil.httpPostFile(destination_oss_path, file);
                            if (!isSuccess) {
                                log.error("Sync file failed, post oss file error. destination_oss_path: {}", destination_oss_path);
                                monitorProperties.addPostFileErrorCount();
                            }
                        } else {
                            log.error("Sync file failed, get hos file error. source_oss_path: {}", source_oss_path);
                            monitorProperties.addDownloadFileErrorCount();
                        }
                    } else {
                        log.error("Sync file failed, source_oss_path or destination_oss_path is incorrect. source_oss_path: {} ,destination_oss_path: {}", source_oss_path, destination_oss_path);
                        monitorProperties.addFileSyncError();
                    }
                }
            }
        } catch (Exception e) {
            log.error("Sync files failed.", e);
            monitorProperties.addFileSyncError();
        }
    }
}
