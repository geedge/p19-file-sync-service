package com.zdjizhi.syncfile.monitor;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class LogChartMetricsFilter implements Filter {
    @Autowired
    private MonitorProperties monitorProperties;

    private MeterRegistry registry;
    private Map<String, Long> dashboardMap = new HashMap<>();
    private Map<String, Long> errorTypeMap = new HashMap<>();

    public LogChartMetricsFilter(MeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        filterChain.doFilter(servletRequest, servletResponse);
        if (req.getRequestURI().contains("/prometheus")) {
            registryDashboardInfo();
        }
    }

    @Override
    public void destroy() {
    }

    /**
     * 更新info指标
     */
    private void registryDashboardInfo() {
        Long downloadFileSuccessCount = monitorProperties.getDownloadFileSuccessCount();
        Long downloadFileErrorCount = monitorProperties.getDownloadFileErrorCount();
        Long postFileSuccessCount = monitorProperties.getPostFileSuccessCount();
        Long postFileErrorCount = monitorProperties.getPostFileErrorCount();
        Long downloadFileSize = monitorProperties.getDownloadFileSize();
        Long postFileSize = monitorProperties.getPostFileSize();

        Long fileSyncError = monitorProperties.getFileSyncError();
        Long hosError = monitorProperties.getHosError();
        Long ossError = monitorProperties.getOssError();

        Long kafkaRecordCount = monitorProperties.getKafkaRecordCount();

        dashboardMap.put("downloadFileSuccessCount", downloadFileSuccessCount);
        dashboardMap.put("downloadFileErrorCount",downloadFileErrorCount);
        dashboardMap.put("postFileSuccessCount", postFileSuccessCount);
        dashboardMap.put("postFileErrorCount", postFileErrorCount);
        dashboardMap.put("downloadFileSize", downloadFileSize);
        dashboardMap.put("postFileSize", postFileSize);
        dashboardMap.put("kafkaRecordCount",kafkaRecordCount);

        errorTypeMap.put("fileSyncError",fileSyncError);
        errorTypeMap.put("hosError",hosError);
        errorTypeMap.put("ossError",ossError);

        registryMetrics(dashboardMap,errorTypeMap);
    }

    private void registryMetrics(Map<String, Long> map ,Map<String, Long> errorTypeMap) {
        if (!ObjectUtils.isEmpty(map)) {
            for (Map.Entry<String, Long> entry : map.entrySet()) {
                //去除容器中值防止影响
                Gauge.builder("dashInfo", map, x -> x.get(entry.getKey()))
                        .tags("id", entry.getKey(), "name", entry.getKey(), "severity", entry.getKey())
                        .description("file sync service info")
                        .register(registry);
            }
        }
        if (!ObjectUtils.isEmpty(errorTypeMap)) {
            for (Map.Entry<String, Long> entry : errorTypeMap.entrySet()) {
                //去除容器中值防止影响
                Gauge.builder("error_type_total", errorTypeMap, x -> x.get(entry.getKey()))
                        .tags("id", entry.getKey(), "name", entry.getKey(), "severity", entry.getKey())
                        .description("Number of error type")
                        .register(registry);
            }
        }
    }
}