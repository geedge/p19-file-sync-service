package com.zdjizhi.syncfile.core;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zdjizhi.syncfile.config.ThreadPoolFactory;
import com.zdjizhi.syncfile.entity.Source;
import com.zdjizhi.syncfile.monitor.MonitorProperties;
import com.zdjizhi.syncfile.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

@Component
public class SyncFiles {
    private Log log = LogFactory.get();

    @Autowired
    private HttpUtil httpUtil;
    @Autowired
    private ExecutorService threadPool;
    @Autowired
    private ThreadPoolFactory threadPoolFactory;
    @Autowired
    MonitorProperties monitorProperties;

    public void syncFiles(List<List<Source>> fileList) {
        List<Callable<Boolean>> callableList = new ArrayList<>();
        try {
            for (List<Source> sourceList : fileList) {
                callableList.add(() -> {
                    boolean status = false;
                    try {
                        for (Source source : sourceList) {
                            String source_oss_path = source.getSource_oss_path();
                            String destination_oss_path = source.getDestination_oss_path();
                            if (source_oss_path != null && !"".equals(source_oss_path)
                                    && destination_oss_path != null && !"".equals(destination_oss_path)) {
                                byte[] file = httpUtil.httpGetFile(source_oss_path);
                                if (file != null) {
                                    boolean isSuccess = httpUtil.httpPostFile(destination_oss_path, file);
                                    if (!isSuccess) {
                                        log.error("Sync file failed, post oss file error. destination_oss_path: {}", destination_oss_path);
                                        monitorProperties.addPostFileErrorCount();
                                        return false;
                                    } else {
                                        status = true;
                                    }
                                } else {
                                    log.error("Sync file failed, get hos file error. source_oss_path: {}", source_oss_path);
                                    monitorProperties.addDownloadFileErrorCount();
                                    return false;
                                }
                            } else {
                                log.error("Sync file failed, source_oss_path or destination_oss_path is incorrect. source_oss_path: {} ,destination_oss_path: {}", source_oss_path, destination_oss_path);
                                monitorProperties.addFileSyncError();
                                return false;
                            }
                        }
                    } catch (Exception e) {
                        log.error("Sync file failed.", e);
                        monitorProperties.addFileSyncError();
                        status = false;
                    }
                    return status;
                });
                if (callableList.size() == threadPoolFactory.getMaxSize()) {
                    threadPool.invokeAll(callableList);
                    callableList.clear();
                }
            }
            if (callableList.size() > 0) {
                threadPool.invokeAll(callableList);
                callableList.clear();
            }
        } catch (InterruptedException e) {
            log.error("Sync files failed.", e);
            monitorProperties.addFileSyncError();
        }
    }
}
