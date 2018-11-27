package com.aojing.paytest.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author gexiao
 * @date 2018/11/27 14:39
 */
@Component
@Configuration
@PropertySource("classpath:config/zfbinfo.properties")
@Data
@ConfigurationProperties(prefix = "ali")
public class ZfbInfoProperties {
    private String open_api_domain;
    private String mcloud_api_domain;
    private String pid;
    private String appid;
    private String private_key;
    private String public_key;
    private String alipay_public_key;
    private String sign_type;
    private String max_query_retry;
    private String query_duration;
    private String max_cancel_retry;
    private String cancel_duration;
    private String heartbeat_delay;
    private String heartbeat_duration;
    private String callback_url;

}
