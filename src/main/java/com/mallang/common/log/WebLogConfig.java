package com.mallang.common.log;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebLogConfig {

    @Bean
    public FilterRegistrationBean<RequestLoggingFilter> firstFilter() {
        FilterRegistrationBean<RequestLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestLoggingFilter(
                "/error",
                "/favicon.ico"
        ));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        registrationBean.setName("requestLoggingFilter");
        return registrationBean;
    }
}
