package com.zdjizhi.syncfile.utils;

import cn.hutool.core.io.IoUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zdjizhi.syncfile.entity.PostFileResponse;
import com.zdjizhi.syncfile.monitor.MonitorProperties;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class HttpUtil {
    private static Log log = LogFactory.get();

    @Autowired
    private CloseableHttpClient httpClient;
    @Autowired
    private RequestConfig requestConfig;
    @Autowired
    MonitorProperties monitorProperties;

    public InputStream httpGetInputStream(String url) {
        InputStream result = null;
        CloseableHttpResponse response = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(requestConfig);
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                result = IOUtils.toBufferedInputStream(response.getEntity().getContent());
                log.info("get file success. current url: {}", url);
                monitorProperties.addDownloadFileSuccessCount();
                monitorProperties.addDownloadFileSize(Integer.parseInt(response.getFirstHeader("Content-Length").getValue()));
            }else if (response.getStatusLine().getStatusCode() == 500){
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
        return result;
    }

    public boolean httpPostInputStream(String url, InputStream data) {
        boolean isSuccess = false;
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);
            httpPost.setEntity(new InputStreamEntity(data));
            response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                String responseEntity = EntityUtils.toString(response.getEntity(), "UTF-8");
                JSONObject jsonObj = (JSONObject) JSON.parse(responseEntity);
                if(jsonObj!=null){
                    if (responseEntity.contains("\"code\": 200")) {
                        PostFileResponse postFileResponse = JSON.toJavaObject(jsonObj, PostFileResponse.class);
                        isSuccess = true;
                        log.info("post file success. current url: {}, msg: {}", url, responseEntity);
                        monitorProperties.addPostFileSuccessCount();
                        monitorProperties.addPostFileSize(postFileResponse.getData().getFileSize());
                    } else {
                        log.error("post file error. current url: {}, msg: {}", url,responseEntity);
                        monitorProperties.addFileSyncError();
                    }
                }else {
                    log.error("post file error, response body error. current url: {}", url);
                    monitorProperties.addOssError();
                }
            } else if(response.getStatusLine().getStatusCode() == 500){
                log.error("post file error. Oss service error.current url: {}, code: 500, msg: {}", url, response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity(), "UTF-8"));
                monitorProperties.addOssError();
            }else {
                log.error("post file error. current url: {}, code: {}, msg: {}", url, response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity(), "UTF-8"));
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
