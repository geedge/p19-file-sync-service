package com.zdjizhi.syncfile.config;


import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
//@ConfigurationProperties(prefix = "thread")
@NacosConfigurationProperties(prefix = "thread", dataId = "${nacos.config.data-id}", groupId = "${nacos.config.group}", type = ConfigType.YAML, autoRefreshed = true)
public class ThreadPoolFactory {

    private Integer maxSize;

    public void setMaxSize(Integer maxSize) {
        this.maxSize = maxSize;
    }

    public Integer getMaxSize() {
        return maxSize;
    }

    @Bean(name = "threadPool")
    public ExecutorService getThreadPool() {
        return Executors.newFixedThreadPool(maxSize);
    }
}
