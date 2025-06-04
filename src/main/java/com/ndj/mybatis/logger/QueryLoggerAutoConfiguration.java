package com.ndj.mybatis.logger;

import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LoggerProperties.class)
@ConditionalOnProperty(name = "mybatis-query-logger.enabled", havingValue = "true", matchIfMissing = true)
public class QueryLoggerAutoConfiguration {

    @Bean
    public Interceptor queryLoggerInterceptor(LoggerProperties loggerProperties) {
        QueryLoggerInterceptor interceptor = new QueryLoggerInterceptor();
        interceptor.setLoggerProperties(loggerProperties);
        return interceptor;
    }

    @Bean
    public ConfigurationCustomizer configurationCustomizer(Interceptor queryLoggerInterceptor) {
        return configuration -> configuration.addInterceptor(queryLoggerInterceptor);
    }
}
