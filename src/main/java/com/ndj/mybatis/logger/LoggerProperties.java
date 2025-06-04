package com.ndj.mybatis.logger;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

@ConfigurationProperties(prefix = "mybatis-query-logger")
public class LoggerProperties {

    /**
     * 로거 사용 여부 (기본값: true)
     */
    private boolean enabled = true;

    /**
     * SLF4J 사용 여부 (기본값: true, false일 경우 System.out.println 사용)
     */
    private boolean useSlf4j = true;

    public LoggerProperties() {
    }

    public LoggerProperties(Properties properties) {
        if (properties != null) {
            this.enabled = Boolean.parseBoolean(
                    properties.getProperty("mybatis-query-logger.enabled", String.valueOf(this.enabled))
            );
            this.useSlf4j = Boolean.parseBoolean(
                    properties.getProperty("mybatis-query-logger.use-slf4j", String.valueOf(this.useSlf4j))
            );
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isUseSlf4j() {
        return useSlf4j;
    }

    public void setUseSlf4j(boolean useSlf4j) {
        this.useSlf4j = useSlf4j;
    }
}
