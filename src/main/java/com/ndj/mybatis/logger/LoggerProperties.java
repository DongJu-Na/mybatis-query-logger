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

    /**
     * 슬로우 쿼리 임계값 (ms 단위, 기본값: 1000)
     */
    private long slowQueryThresholdMs = 1000;

    /**
     * 파라미터 값을 SQL에 치환해서 보여줄지 여부 (기본값: false)
     */
    private boolean replaceParameter = false;

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
            this.slowQueryThresholdMs = Long.parseLong(
                    properties.getProperty("mybatis-query-logger.slow-query-threshold-ms", String.valueOf(this.slowQueryThresholdMs))
            );

            this.replaceParameter = Boolean.parseBoolean(
                    properties.getProperty("mybatis-query-logger.replace-parameter", String.valueOf(this.replaceParameter))
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

    public long getSlowQueryThresholdMs() {
        return slowQueryThresholdMs;
    }

    public void setSlowQueryThresholdMs(long slowQueryThresholdMs) {
        this.slowQueryThresholdMs = slowQueryThresholdMs;
    }

    public boolean isReplaceParameter() {
        return replaceParameter;
    }

    public void setReplaceParameter(boolean replaceParameter) {
        this.replaceParameter = replaceParameter;
    }

}
