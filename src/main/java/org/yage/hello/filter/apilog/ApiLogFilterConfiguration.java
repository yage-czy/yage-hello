package org.yage.hello.filter.apilog;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * @author 常振亚
 * @version 1.0
 * @date 2020/9/23
 * @date 22:54
 */
@Configuration
public class ApiLogFilterConfiguration {

    @Bean
    public FilterRegistrationBean<ApiLogFilter> apiLogFilter() {

        final FilterRegistrationBean<ApiLogFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        final ApiLogFilter apiLogFilter = new ApiLogFilter();
        filterRegistrationBean.setFilter(apiLogFilter);
        filterRegistrationBean.setOrder(Ordered.LOWEST_PRECEDENCE);

        return filterRegistrationBean;
    }
}
