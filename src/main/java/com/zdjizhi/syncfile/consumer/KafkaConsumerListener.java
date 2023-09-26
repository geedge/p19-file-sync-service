package com.zdjizhi.syncfile.consumer;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.zdjizhi.syncfile.core.SyncFiles;
import com.zdjizhi.syncfile.entity.Source;
import com.zdjizhi.syncfile.entity.SysFileSync;
import com.zdjizhi.syncfile.monitor.MonitorProperties;
import com.zdjizhi.syncfile.utils.HttpUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Component
public class KafkaConsumerListener {
    private Log log = LogFactory.get();

    @Autowired
    SyncFiles syncFiles;
    @Autowired
    MonitorProperties monitorProperties;
    @Autowired
    HttpUtil httpUtil;
    @Autowired
    ThreadPoolExecutor threadPool;

    @KafkaListener(topics = {"${kafka.consumer.topic}"}, containerFactory = "kafkaListenerContainerFactory")
    public void listen(List<ConsumerRecord<?, ?>> records, Acknowledgment ack) {
        try {
            List<List<Source>> fileList = new ArrayList<>();
            for (ConsumerRecord<?, ?> record : records) {
                log.info("消费kafka的数据的value: " + record.value().toString());
                JSONObject jsonObj = (JSONObject) JSON.parse(record.value().toString());
                SysFileSync sysFileSync = JSON.toJavaObject(jsonObj, SysFileSync.class);
                if (sysFileSync != null) {
                    List<Source> sourceList = sysFileSync.getSource_list();
                    if (sourceList.size() < 1) {
                        log.error("kafka data error, sourceList is null. kafka data: " + record.value().toString());
                        monitorProperties.addFileSyncError();
                    } else {
                        fileList.add(sourceList);
                    }
                } else {
                    log.error("parse kafka data error. kafka data: " + record.value().toString());
                    monitorProperties.addFileSyncError();
                }
                monitorProperties.addKafkaRecordCount();
            }
            while (true) {
                if (threadPool.getActiveCount() < threadPool.getMaximumPoolSize()) {
                    threadPool.submit(() -> syncFiles.syncFiles(fileList));
                    break;
                }
            }
            ack.acknowledge();
        } catch (Exception e) {
            log.error("consume kafka data error.", e);
            monitorProperties.addFileSyncError();
        } finally {
            ack.acknowledge();
        }
    }
}
