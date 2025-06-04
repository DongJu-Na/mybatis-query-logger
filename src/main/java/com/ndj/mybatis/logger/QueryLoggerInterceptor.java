package com.ndj.mybatis.logger;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Intercepts({
        @Signature(
                type = StatementHandler.class,
                method = "prepare",
                args = {Connection.class, Integer.class}
        )
})
public class QueryLoggerInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(QueryLoggerInterceptor.class);
    private LoggerProperties loggerProperties = new LoggerProperties();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (!loggerProperties.isEnabled()) {
            return invocation.proceed();
        }

        long start = System.currentTimeMillis();

        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = boundSql.getSql().replaceAll("\\s+", " ").trim();

        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();

        StringBuilder params = new StringBuilder();
        if (parameterMappings != null && parameterObject != null) {
            for (ParameterMapping mapping : parameterMappings) {
                String name = mapping.getProperty();
                Object value = resolveParamValue(parameterObject, name);
                params.append(name)
                        .append(" = ")
                        .append(value)
                        .append(" (")
                        .append(value != null ? value.getClass().getSimpleName() : "null")
                        .append(")\n");
            }
        }

        Object result = invocation.proceed();
        long duration = System.currentTimeMillis() - start;

        String output = "\n====== MyBatisQueryLogger ======\nSQL:\n" + sql +
                "\nPARAMETERS:\n" + params +
                "DURATION: " + duration + "ms\n================================\n";

        if (loggerProperties.isUseSlf4j()) {
            log.info(output);
        } else {
            System.out.println(output);
        }

        return result;
    }

    private Object resolveParamValue(Object parameterObject, String fieldName) {
        if (parameterObject == null) return null;
        if (parameterObject instanceof Map<?, ?> map) {
            return map.get(fieldName);
        }
        try {
            Field field = parameterObject.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(parameterObject);
        } catch (Exception e) {
            return "[Unavailable]";
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        this.loggerProperties = new LoggerProperties(properties);
    }

    public void setLoggerProperties(LoggerProperties loggerProperties) {
        this.loggerProperties = loggerProperties;
    }
}
