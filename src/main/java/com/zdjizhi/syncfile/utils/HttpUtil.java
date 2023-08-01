package com.zdjizhi.syncfile.utils;

import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zdjizhi.syncfile.entity.PostFileResponse;
import com.zdjizhi.syncfile.monitor.MonitorProperties;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HttpUtil {
    private static Log log = LogFactory.get();

    @Autowired
    private CloseableHttpClient httpClient;
    @Autowired
    private RequestConfig requestConfig;
    @Autowired
    MonitorProperties monitorProperties;

    public byte[] httpGetFile(String url) {
        byte[] data = null;
        CloseableHttpResponse response = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(requestConfig);
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                data = IoUtil.readBytes(response.getEntity().getContent());
                log.info("get file success. current url: {}", url);
                monitorProperties.addDownloadFileSuccessCount();
                monitorProperties.addDownloadFileSize(data.length);
            } else if (response.getStatusLine().getStatusCode() == 500) {
                log.error("get file error. Hos service error, please check hos. current url: {}", url);
                monitorProperties.addHosError();
            } else {
                log.error("get file error. current url: {}, code: {}, msg: {}", url, response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity(), "UTF-8"));
                monitorProperties.addFileSyncError();
            }
        } catch (Exception e) {
            log.error("get file error. current url: {}, error: {}", url, e.toString());
            monitorProperties.addFileSyncError();
        } finally {
            IoUtil.close(response);
        }
        return data;
    }

    public boolean httpPostFile(String url, byte[] file) {
        boolean isSuccess = false;
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);
            httpPost.setEntity(new ByteArrayEntity(file));
            response = httpClient.execute(httpPost);
            String responseEntity = EntityUtils.toString(response.getEntity(), "UTF-8");
            log.info("post file response: " + responseEntity);
            if (response.getStatusLine().getStatusCode() == 200) {
                if (responseEntity.contains("\"code\":200")) {
                    if (JSONUtil.isJsonObj(responseEntity)) {
                        JSONObject jsonObj = JSONUtil.parseObj(responseEntity);
                        PostFileResponse postFileResponse = JSONUtil.toBean(jsonObj, PostFileResponse.class);
                        isSuccess = true;
                        log.info("post file success. current url: {}, msg: {}", url, responseEntity);
                        monitorProperties.addPostFileSuccessCount();
                        monitorProperties.addPostFileSize(postFileResponse.getData().getFileSize());
                    } else {
                        log.error("post file successfully response is not json data. current url: {}, msg: {}", url, responseEntity);
                    }
                } else {
                    log.error("post file error. current url: {}, msg: {}", url, responseEntity);
                    monitorProperties.addFileSyncError();
                }
            } else if (response.getStatusLine().getStatusCode() == 500) {
                log.error("post file error. Oss service error.current url: {}, code: 500, msg: {}", url, response.getStatusLine().getStatusCode(), responseEntity);
                monitorProperties.addOssError();
            } else {
                log.error("post file error. current url: {}, code: {}, msg: {}", url, response.getStatusLine().getStatusCode(), responseEntity);
                monitorProperties.addFileSyncError();
            }
        } catch (Exception e) {
            log.error("post file error. current url: " + url, e);
            monitorProperties.addFileSyncError();
        } finally {
            IoUtil.close(response);
        }
        return isSuccess;
    }
}
