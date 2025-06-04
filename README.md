# MyBatis Query Logger

> MyBatis 쿼리 + 파라미터 자동 로깅 인터셉터

## 📌 개요

이 라이브러리는 MyBatis의 SQL 실행 시점에 쿼리문과 바인딩된 파라미터 값을 자동으로 출력해주는 인터셉터입니다.

- ✅ `application.yml`만으로 자동 적용
- ✅ `Slf4j` 또는 `System.out` 출력 선택 가능
- ✅ 실행 시간(ms)까지 로깅
- ✅ 간편한 종속성 추가로 빠른 적용 가능

## 🧑‍💻 설치 방법

### Gradle 설정

```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.깃허브아이디:mybatis-query-logger:버전'
}
```

### ⚙️ 설정 방법 (application.yml)
```yaml
mybatis-query-logger:
  enabled: true           # 기본값 true, false로 비활성화 가능
  use-slf4j: true         # true: 로그로 출력 / false: System.out 출력
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
- Spring Boot 3.x 이상
- MyBatis
- Java 17 이상

### 📝 기여
이 프로젝트는 오픈소스입니다.
기능 개선이나 버그 제보는 언제든 Pull Request 또는 Issue로 남겨주세요.

### 📄 라이선스
MIT License