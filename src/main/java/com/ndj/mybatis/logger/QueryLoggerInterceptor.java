package com.ndj.mybatis.logger;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

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

        // 1. 파라미터 값 추출 리스트 (OUT 파라미터 제외, 순서 보장)
        List<Object> paramValues = new ArrayList<>();

        // 2. 파라미터 이름별 값 (중복 없는 출력용, printed로 중복 체크)
        Map<String, Object> printed = new HashMap<>();
        StringBuilder params = new StringBuilder();

        // 디버깅: 전체 파라미터 이름 나열
        if (parameterMappings != null && parameterObject != null) {
            for (ParameterMapping mapping : parameterMappings) {
                String name = mapping.getProperty();
                if (mapping.getMode() == ParameterMode.OUT) continue;

                Object value = resolveParamValue(parameterObject, name);

                paramValues.add(value);

                if (!printed.containsKey(name)) {
                    printed.put(name, value);
                    params.append(name)
                            .append(" = ")
                            .append(value)
                            .append(" (")
                            .append(value != null ? value.getClass().getSimpleName() : "null")
                            .append(")\n");
                }
            }
        }

        // 디버깅: 치환 대상 ? 개수와 paramValues 크기 비교
        long questionCount = sql.chars().filter(ch -> ch == '?').count();
        if (questionCount != paramValues.size()) {
            log.warn("MyBatisQueryLogger - SQL 내 ? 개수와 paramValues 크기가 다릅니다. ? 개수: {}, paramValues.size(): {}", questionCount, paramValues.size());
        }

        Object result = invocation.proceed();
        long duration = System.currentTimeMillis() - start;

        StringBuilder output = new StringBuilder("\n====== MyBatisQueryLogger ======\n");

        // 파라미터 치환 플래그
        if (loggerProperties.isReplaceParameter() && !paramValues.isEmpty()) {
            String replacedSql = replaceSqlParameters(sql, paramValues);
            output.append("SQL (WITH PARAMS):\n").append(replacedSql).append("\n")
                    .append("PARAMETERS:\n").append(params);
        } else {
            output.append("SQL:\n").append(sql).append("\n")
                    .append("PARAMETERS:\n").append(params);
        }

        output.append("DURATION: ").append(duration).append("ms\n");

        if (duration > loggerProperties.getSlowQueryThresholdMs()) {
            output.append("SLOW QUERY DETECTED! Threshold: ")
                    .append(loggerProperties.getSlowQueryThresholdMs()).append("ms\n");
        }

        output.append("================================\n");

        if (loggerProperties.isUseSlf4j()) {
            log.info(output.toString());
        } else {
            System.out.println(output);
        }

        return result;
    }

    /**
     * ?를 paramValues 순서대로 치환
     */
    private String replaceSqlParameters(String sql, List<Object> paramValues) {
        StringBuilder sb = new StringBuilder();
        int paramIndex = 0;
        int length = sql.length();

        for (int i = 0; i < length; i++) {
            char c = sql.charAt(i);
            if (c == '?' && paramIndex < paramValues.size()) {
                Object value = paramValues.get(paramIndex++);
                sb.append(formatParamValue(value));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 파라미터 값의 SQL 표기 변환 (문자열/날짜는 '로 감쌈)
     */
    private String formatParamValue(Object value) {
        if (value == null) {
            return "'null'";
        }
        if (value instanceof String || value instanceof Character) {
            return "'" + value + "'";
        }
        if (value instanceof java.util.Date date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return "'" + sdf.format(date) + "'";
        }
        // 숫자/boolean 등 기본형은 그냥 toString
        return value.toString();
    }

    private Object resolveParamValue(Object parameterObject, String fieldName) {
        if (parameterObject == null) return null;
        if (parameterObject instanceof Map<?, ?> map) {
            return map.get(fieldName);
        }
        // MyBatis의 ParamMap(파라미터 래퍼)도 대응
        try {
            Field field = parameterObject.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(parameterObject);
        } catch (Exception e) {
            // org.apache.ibatis.binding.MapperProxy$ParamMap 등
            try {
                return parameterObject.getClass()
                        .getMethod("get", Object.class)
                        .invoke(parameterObject, fieldName);
            } catch (Exception ignored) {}
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
