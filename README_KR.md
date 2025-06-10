# MyBatis Query Logger

> MyBatis 쿼리 + 파라미터 자동 로깅 인터셉터<br/>
👉 [View English Version (영문)](https://github.com/DongJu-Na/mybatis-query-logger/blob/master/README.md)

## 📌 개요

Spring Boot + MyBatis 환경에서 실행되는 SQL 쿼리와 파라미터, 실행 시간(SLOW QUERY 포함)을 로깅하는 인터셉터입니다.

- SQL 쿼리 로그 출력
- 바인딩된 파라미터 출력
- 실행 시간(ms) 측정
- **느린 쿼리(Slow Query)** 감지 기능
- SLF4J 또는 `System.out.println` 방식 선택 가능
- Spring Boot 자동 설정 지원 (별도 설정 없음)

## 🧑‍💻 설치 방법

### Gradle 설정

```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.DongJu-Na:mybatis-query-logger:v0.0.8'
}
```

### ⚙️ 설정 방법 (application.yml)
```yaml
mybatis-query-logger:
  enabled: true                # 쿼리 로깅 사용 여부 (기본값: true)
  use-slf4j: true              # SLF4J 사용 여부 (기본값: true, false면 System.out 출력)
  slow-query-threshold-ms: 1000  # SLOW QUERY 임계값 (기본값: 1000ms)
```

### 🧾 출력 예시
```vbnet
====== MyBatisQueryLogger ======
SQL:
SELECT * FROM USERS WHERE ID = ?

PARAMETERS:
id = 1 (Integer)

DURATION: 5ms
================================
```

## ✅ 사용 조건
- Spring Boot 3.0 이상
- MyBatis 3.5 이상
- Java 17 이상

### 멀티 데이터소스 환경

Spring Boot의 MyBatis 자동 설정을 사용하지 않는 경우 (예: 멀티 데이터소스 설정 시) 
다음과 같이 수동 등록이 필요합니다.

```java
factoryBean.setPlugins(new Interceptor[]{new QueryLoggerInterceptor()});

```

## 📝 기여
이 프로젝트는 오픈소스입니다.
기능 개선이나 버그 제보는 언제든 Pull Request 또는 Issue로 남겨주세요.

## 📄 라이선스
MIT License

---