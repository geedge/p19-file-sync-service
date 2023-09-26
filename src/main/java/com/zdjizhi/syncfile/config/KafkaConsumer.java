package com.zdjizhi.syncfile.config;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;
import com.zdjizhi.syncfile.consumer.KafkaConsumerListener;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

@Configuration
//@ConfigurationProperties(prefix = "kafka.consumer")
@NacosConfigurationProperties(prefix = "kafka.consumer", dataId = "${nacos.config.data-id}", groupId = "${nacos.config.group}", type = ConfigType.YAML, autoRefreshed = true)
@EnableKafka
public class KafkaConsumer {

    private String servers;
    private boolean enable_auto_commit;
    private String session_timeout;
    private String auto_commit_interval;
    private String group_id;
    private String auto_offset_reset;
    private int poll_timeout;
    private int concurrency;
    private boolean batch_listener;
    private int pool_record;
    private String sasl_username;
    private String sasl_password;

    public void setServers(String servers) {
        this.servers = servers;
    }

    public void setEnable_auto_commit(boolean enable_auto_commit) {
        this.enable_auto_commit = enable_auto_commit;
    }

    public void setSession_timeout(String session_timeout) {
        this.session_timeout = session_timeout;
    }

    public void setAuto_commit_interval(String auto_commit_interval) {
        this.auto_commit_interval = auto_commit_interval;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public void setAuto_offset_reset(String auto_offset_reset) {
        this.auto_offset_reset = auto_offset_reset;
    }

    public void setPoll_timeout(int poll_timeout) {
        this.poll_timeout = poll_timeout;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    public void setBatch_listener(boolean batch_listener) {
        this.batch_listener = batch_listener;
    }

    public void setPool_record(int pool_record) {
        this.pool_record = pool_record;
    }

    public void setSasl_username(String sasl_username) {
        this.sasl_username = sasl_username;
    }

    public void setSasl_password(String sasl_password) {
        this.sasl_password = sasl_password;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(consumerConfigs()));
        factory.setConcurrency(concurrency);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setBatchListener(batch_listener);//设置为批量消费
        factory.getContainerProperties().setPollTimeout(poll_timeout);
        return factory;
    }

    private Map<String, Object> consumerConfigs() {
        Map<String, Object> propsMap = new HashMap<>();
        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enable_auto_commit);
        propsMap.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, auto_commit_interval);
        propsMap.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, pool_record);//每一批数量
        propsMap.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, session_timeout);
        propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, group_id);
        propsMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, auto_offset_reset);
        if(servers.contains("9094")){
            propsMap.put("security.protocol", "SASL_PLAINTEXT");
            propsMap.put("sasl.mechanism", "PLAIN");
            propsMap.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username="+sasl_username+" password="+sasl_password+";");
        }
        return propsMap;
    }

    @Bean
    public KafkaConsumerListener listener() {
        return new KafkaConsumerListener();
    }
}