# MyBatis Query Logger

> MyBatis query + parameter auto-logging interceptor<br/>
> 👉 [한국어 버전 보기 (Korean)](https://github.com/DongJu-Na/mybatis-query-logger/blob/master/README_KR.md)

## 📌 Overview

This is an interceptor for Spring Boot + MyBatis environments that automatically logs executed SQL queries, bound parameters, and execution time (including **slow query detection**).

* SQL query logging
* Bound parameter logging
* Execution time measurement (in ms)
* **Slow query detection**
* Option to choose between SLF4J and `System.out.println`
* Spring Boot auto-configuration support (zero setup required)

## 🧑‍💻 Installation

### Gradle Setup

```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.DongJu-Na:mybatis-query-logger:v0.0.8'
}
```

### ⚙️ Configuration (application.yml)

```yaml
mybatis-query-logger:
  enabled: true                # Enable query logging (default: true)
  use-slf4j: true              # Use SLF4J (default: true, set false for System.out)
  slow-query-threshold-ms: 1000  # Threshold for slow query detection (default: 1000ms)
  replace-parameter: true  # Replace '?' with actual parameter values in SQL logs for easy DB tool execution (default : false)
```

## 📟 Output Example

```vbnet
====== MyBatisQueryLogger ======
SQL:
SELECT * FROM USERS WHERE ID = ?

PARAMETERS:
id = 1 (Integer)

DURATION: 5ms
================================
```

## ✅ Requirements

* Spring Boot 3.0+
* MyBatis 3.5+
* Java 17+

### In Multi-DataSource Environments

If you are **not** using MyBatis auto-configuration (e.g., in multi-datasource setups), you must manually register the plugin:

```java
factoryBean.setPlugins(new Interceptor[]{new QueryLoggerInterceptor()});
```

## 📝 Contributions

This is an open-source project.
Feel free to submit pull requests or issues for feature improvements or bug reports.

## 📄 License

MIT License

---

