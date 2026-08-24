# Next Components 使用手册

> 基于 **Spring Boot 3.5.x** 自动装配（Auto-configuration）机制的模块化 Starter 组件库，要求 **Java 21**。
> 统一坐标：`groupId=com.qiwumind`，`version=${revision}`（当前 `1.1.0.jdk21-SNAPSHOT`）。
> 本手册说明如何引入、配置并使用 `next-components` 提供的各个组件。

## 目录

- [1. 工程结构](#1-工程结构)
- [2. 快速开始](#2-快速开始)
- [3. 模块使用手册](#3-模块使用手册)
- [4. Starter 自动加载清单（自动加载 + 使用方式）](#4-starter-自动加载清单自动加载--使用方式)
- [5. 配置前缀速查表](#5-配置前缀速查表)
- [6. 自定义 Starter 开发规范](#6-自定义-starter-开发规范)
- [7. 构建与验证](#7-构建与验证)

---

## 1. 工程结构

聚合 + 父 POM 管理版本（`revision`），按职责分为 **基础库 / 中间件 / 业务组件 / Web 与保障 / 工具生成** 五类。
> 注：早期 `next-core`、`next-util` 已移除，能力并入 `next-common`。

```
next-components/                                  # 聚合 + 父 POM（版本 ${revision}，统一依赖管理）
├── next-dependencies/                            # BOM：spring-boot / cloud / alibaba + 内部模块版本
│
├── 【基础公共库】                                 # 非 Starter，按需直接依赖或在 Starter 内复用
│   ├── next-common/                             # 公共包：统一异常、JSON/Bean/集合/日期/加解密工具、统一返回
│   ├── next-datasecure/                         # 数据脱敏 / 字段级加解密（注解 + MyBatis TypeHandler）
│   ├── next-oss/                                # 阿里云 OSS 封装（STS、上传下载、多渠道工厂）
│   ├── next-sequence/                           # 分布式序列号核心（非 Starter，需手动装配）
│   ├── next-groovy/                             # Groovy 脚本引擎核心（非 Starter）
│   ├── next-freemarker-generator/               # 基于 MBG + Freemarker 的代码生成器
│   ├── next-ons/                                # 阿里云 ONS（RocketMQ 商业版）封装
│   ├── next-rocketmq/                           # 火山云 RocketMQ 5.x 封装
│   └── next-feign-config/                       # Feign 相关依赖聚合（占位/纯依赖模块）
│
├── 【中间件 / 数据】
│   ├── next-springboot-starter-redis/           # Redis 缓存 / 分布式锁 / Jedis 封装
│   ├── next-springboot-starter-cache/           # 本地缓存 / 布隆过滤器
│   ├── next-springboot-starter-sequence/        # 分布式序列号 Starter（自动装配）
│   ├── next-springboot-starter-mybatis/         # MyBatis 数据源 / 事务 / 多语言翻译
│   ├── next-springboot-starter-hologres/        # Hologres 数据源
│   ├── next-springboot-starter-starrocks/       # StarRocks 数据源
│   └── next-springboot-starter-mq/              # Redis MQ（pub/sub + Stream）
│
├── 【安全 / 保障】
│   ├── next-springboot-starter-crypto/          # 加解密（SM2/SM4/RSA 等）
│   ├── next-springboot-starter-security/        # 认证鉴权 + 操作日志
│   └── next-springboot-starter-protection/      # 幂等 / 分布式锁 / 限流 / 签名
│
├── 【业务组件】
│   ├── next-springboot-starter-license/         # License 授权
│   ├── next-springboot-starter-pricing/         # 定价引擎
│   ├── next-springboot-starter-biz-tenant/      # 多租户（DB/Redis/MQ/Web 隔离）
│   ├── next-springboot-starter-biz-data-permission/ # 数据权限（MyBatis 行级过滤）
│   └── next-springboot-starter-biz-ip/          # IP 地理解析（ip2region）+ 行政区划查询
│
├── 【Web / 任务 / 监控】
│   ├── next-springboot-starter-web/             # 全局异常 / Swagger / XSS / API 加解密 / 访问日志 / Jackson
│   ├── next-springboot-starter-websocket/       # WebSocket 会话管理与多节点广播
│   ├── next-springboot-starter-monitor/         # 链路追踪 / 指标上报
│   ├── next-springboot-starter-job/             # 定时任务（Quartz）+ 异步线程池
│   ├── next-springboot-starter-excel/           # Excel 导入导出（fastexcel）
│   └── next-springboot-starter-quartz/          # Quartz 定时任务基础自动配置
│
└── 【计算 / 文件 / 其它】
    ├── next-springboot-starter-compute/         # 计算能力
    ├── next-springboot-starter-file/            # XML 文件处理
    ├── next-springboot-starter-groovy/          # Groovy 脚本引擎 Starter（自动装配）
    ├── next-springboot-starter-pdf/             # PDF 生成
    ├── next-springboot-starter-wechat/          # 微信支付 / 公众号
    ├── next-springboot-starter-context/         # Spring 上下文增强
    ├── next-springboot-starter-banner/          # 启动 Banner（ApplicationContextInitializer）
    └── next-springboot-starter-test/            # 测试支撑（测试切片 / 工具）
```

### 1.1 Spring Boot 3.x 自动装配机制

Spring Boot 2.7 起废弃 `META-INF/spring.factories` 中的 `EnableAutoConfiguration`，**Spring Boot 3.x 完全移除**。新方式：

```
src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

文件内每行一个自动配置类全限定名，例如 `next-springboot-starter-redis`：

```
com.qiwumind.next.components.redis.autoconfigure.RedisAutoConfiguration
```

对于 `ApplicationContextInitializer` 这类扩展点（如 Banner），使用专属文件：

```
src/main/resources/META-INF/spring/org.springframework.context.ApplicationContextInitializer.imports
```

> 历史重构已：删除冗余的 `spring.factories`、修复 `groovy` 包名错位、`mybatis` 注册路径错误、
> 修正 `banner` 将 `ApplicationContextInitializer` 误注册为 AutoConfiguration、统一 Spring Boot 版本为 3.5.x。

---

## 2. 快速开始

### 2.1 引入 BOM（依赖版本管理）

在应用 `pom.xml` 中导入 `next-dependencies` BOM：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.qiwumind</groupId>
            <artifactId>next-dependencies</artifactId>
            <version>1.1.0.jdk21-SNAPSHOT</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

> `next-dependencies` 已统一导入：`spring-boot-dependencies:3.5.15`、`spring-cloud-dependencies:2025.0.3`、
> `spring-cloud-alibaba-dependencies:2025.0.0.0` 及各三方版本。引入后各 Starter 可省略版本号。

### 2.2 引入 Starter

```xml
<dependencies>
    <dependency>
        <groupId>com.qiwumind</groupId>
        <artifactId>next-springboot-starter-redis</artifactId>
    </dependency>
    <dependency>
        <groupId>com.qiwumind</groupId>
        <artifactId>next-springboot-starter-license</artifactId>
    </dependency>
    <!-- 其它 Starter 同理 -->
</dependencies>
```

Spring Boot 3.5 启动时会自动扫描 `META-INF/spring/*.imports` 完成自动装配，**无需** `@EnableAutoConfiguration` 或 `spring.factories`。

### 2.3 基础库（非 Starter）引入

`next-common`、`next-datasecure`、`next-oss`、`next-sequence`、`next-groovy` 等为基础库，
直接作为普通依赖引入，按需调用其工具类 / 手动注册 Bean：

```xml
<dependency>
    <groupId>com.qiwumind</groupId>
    <artifactId>next-common</artifactId>
</dependency>
```

---

## 3. 模块使用手册

> 约定：`自动装配类` 指 `AutoConfiguration.imports` 中注册的类（Spring 启动时自动加载）；
> 标注「手动装配」的模块需自行 `@Bean` 注册或在代码中调用。

### 3.1 基础公共（`next-common`）

非 Starter。统一异常体系、JSON / Bean / 集合 / 日期 / 加解密 / HTTP 等通用工具。

```java
// 抛业务异常（错误码见 GlobalErrorCodeConstants：BAD_REQUEST(400)、TOO_MANY_REQUESTS(429)…）
throw new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST);

// JSON 序列化（基于 Jackson）
String json = JsonUtils.toJsonString(obj);
User user = JsonUtils.parseObject(json, User.class);

// 集合判断
boolean empty = CollectionUtils.isAnyEmpty(list1, list2);
```

常用类：`exception.{ServiceException,BusinessException,ErrorCode}`、`util.json.JsonUtils`、
`util.collection.CollectionUtils`、`util.bean.BeanMapperUtils`、`util.crypto.{AESUtil,RSAUtils,Sm4Utils}`、
`util.{DateUtils,HttpUtils,ServletUtils}`、`EventBusUtils`。无自动装配、无配置前缀。

### 3.2 Redis（`next-springboot-starter-redis`）

自动装配类：`RedisAutoConfiguration`、`QiwumindRedisAutoConfiguration`、`QiwumindCacheAutoConfiguration`
配置前缀：`next.redis`（需 `next.redis-open=true` 才启用）

```yaml
next:
  redis-open: true
  redis:
    host: 127.0.0.1
    port: 6379
    password: ""
    database: 0
```

```java
@Service
public class DemoService {
    @Autowired private RedisTemplate<String, Object> redisTemplate; // String key / JSON value
    @Autowired private JedisCache jedisCache;                       // 基于 Jedis 的缓存封装
    @Autowired private LockManager lockManager;                     // 分布式锁

    public void demo() {
        redisTemplate.opsForValue().set("k", "v");
        lockManager.tryLock("order:123", 3000L);
    }
}
```

核心 Bean：`jedisPoolManager`、`jedisLockService`、`lockManager`、`JedisCache`、
`redisTemplateCache`、`RedisConnectionManager`、`redisTemplate`。

### 3.3 本地缓存 / 布隆过滤器（`next-springboot-starter-cache`）

自动装配类：`CacheAutoConfiguration`、`BloomFilterAutoConfiguration`
配置前缀：`next.cache`、`next.bloomfilter`

```yaml
next:
  cache:
    enabled: true
  bloomfilter:
    enabled: true
```

```java
@Autowired private BloomFilter<String> bloomFilter; // 类型以模块实际暴露的 Bean 为准
```

### 3.4 分布式序列号

- **Starter（`next-springboot-starter-sequence`）** 自动装配：`SequenceGeneratorAutoConfigure`

  ```java
  @Autowired private SequenceGenerator sequenceGenerator;
  long id = sequenceGenerator.nextId("order");
  ```

- **基础库（`next-sequence`，非 Starter）**：基于数据库号段（segment）实现，需手动装配后使用。
  核心类：`handler.Sequence`（函数式接口 `long nextValue(...)`）、`SequenceFactory`、`SegmentSequence`。

> 两者区别：Starter 已自动装配并提供 `SequenceGenerator` Bean；基础库 `next-sequence` 仅提供实现，需自行注册 Bean。

### 3.5 定时任务（`next-springboot-starter-quartz` / `next-springboot-starter-job`）

- **`next-springboot-starter-quartz`** 自动装配：`QuartzAutoConfiguration`；配置前缀 `next.quartz`（另见 `spring.quartz.*`）。
- **`next-springboot-starter-job`** 自动装配：`QiwumindQuartzAutoConfiguration`、`QiwumindAsyncAutoConfiguration`。
  封装 Quartz 任务管理（增删改查 / 触发）与异步线程池（TTL 装饰）。核心类：`scheduler.SchedulerManager`、
  `handler.JobHandler`（`String execute(String param)`）、`service.JobLogFrameworkService`。

```java
@Component
public class DemoJob implements JobHandler {
    public String execute(String param) { return "ok"; }
}
// 注册调度
schedulerManager.addJob(jobId, "demoJob", param, "0 0/1 * * * ?", 0, 0);
```

> 注意：本项目定时任务基于 **Quartz + Spring Async**，非 XXL-JOB。

### 3.6 License 授权（`next-springboot-starter-license`）

自动装配类：`com.qiwumind.next.components.license.autoconfigure.LicenseAutoConfiguration`
配置前缀：`next.license`（加解密属性在 `next.license`）

```yaml
next:
  license:
    enabled: true
    file:
      path: license/license.dat
      backup-path: license/license.dat.bak
      auto-create: true
    validation:
      strict-mode: true
      grace-period: 15d
      validate-on-startup: true
      global-block-enabled: true
      exclude-paths:
        - /health
        - /api/license/**
    signing:
      private-key-path: keys/private.key
      public-key-path: keys/public.key
      algorithm: SHA384withRSA
```

```java
@Autowired private LicenseManager licenseManager;

@GetMapping("/api/license/valid")
public boolean valid() { return licenseManager.isLicenseValid(); }

@LicensedFeature(value = "advanced-report", message = "未授权使用高级报表功能")
@GetMapping("/features/advanced-report")
public String advancedReport() { return "ok"; }
```

> 完整可运行示例见 `next-springboot-starter-license/demo-project/`。

### 3.7 MyBatis（`next-springboot-starter-mybatis`）

自动装配类：`NextDataSourceAutoConfiguration`、`NextMybatisAutoConfiguration`、`NextTranslateAutoConfiguration`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/demo
    username: root
    password: root
mybatis:
  mapper-locations: classpath*:mapper/*.xml
```

```java
@Mapper
public interface UserMapper { User selectById(Long id); }

@Autowired private UserMapper userMapper;
```

### 3.8 Hologres（`next-springboot-starter-hologres`）

自动装配类：`HoloDataSourceAutoConfiguration`；配置前缀：`next.hologres`

```yaml
next:
  hologres:
    url: jdbc:postgresql://host:port/db
    username: xxx
    password: xxx
```

### 3.9 StarRocks（`next-springboot-starter-starrocks`）

自动装配类：`StarRocksDataSourceAutoConfiguration`；配置前缀：`next.starrocks.config`、`next.starrocks.cluster`

```yaml
next:
  starrocks:
    cluster:
      fe-host: 127.0.0.1
      http-port: 8030
      query-port: 9030
    config:
      user: root
      password: ""
```

### 3.10 数据脱敏 / 加解密（`next-datasecure`，基础库）

非 Starter。字段级脱敏注解、MyBatis 加解密 TypeHandler、日志脱敏、对称加解密工具。
核心类：`annotations.Sensitive`、配置 `DataSecureConfig(secretkey, secswitch)`、`utils.SensitiveProcessUtils`、
`SymmetryUtil`、`typehandlers.{StringSecureTypeHandler,AesTypeHandler}`。

```java
public class User {
    @Sensitive(format = SensitiveRulesEnum.NAME) // 序列化/toString 时自动脱敏
    private String name;
}
// MyBatis resultMap 指定 typeHandler=StringSecureTypeHandler 即可字段加密
```

### 3.11 对象存储（`next-oss`，基础库）

非 Starter。阿里云 OSS 封装：STS 临时凭证、上传/下载、多渠道工厂。
核心类：`OssUtils`、`OssStsUtils`、`FileOssFactory`、`OssStsConfig`。

```java
OssStsConfig cfg = new OssStsConfig(endpoint, bucket, ak, sk, token);
String etag = OssUtils.uploadFile(cfg, "avatar/1.png", new File("/tmp/1.png"));
OssUtils.downloadFile(cfg, "avatar/1.png", "/tmp/down.png");
```

### 3.12 消息中间件

- **`next-springboot-starter-mq`** 自动装配：`QiwumindRedisMQProducerAutoConfiguration`、`QiwumindRedisMQConsumerAutoConfiguration`。
  提供 Redis pub/sub + Stream 消息模板。核心类：`redis.core.RedisMQTemplate`（send）、
  `redis.core.stream.AbstractRedisStreamMessage(Listener)`、`redis.core.pubsub.AbstractRedisChannelMessage(Listener)`；
  Stream 消费组默认取 `spring.application.name`。

  ```java
  @Autowired RedisMQTemplate redisMQTemplate;
  redisMQTemplate.send(new DemoMessage().setFoo(1));

  @Component
  public class DemoListener extends AbstractRedisStreamMessageListener<DemoMessage> {
      public void onMessage(DemoMessage m) { /* ... */ }
  }
  ```

- **`next-ons`**（基础库）：阿里云 ONS（RocketMQ 商业版）封装，提供 `AbstractOnsSender` / `AbstractOnsConsumer` 抽象基类，继承后注入属性即可。
- **`next-rocketmq`**（基础库）：火山云 RocketMQ 5.x 封装，提供 `AbstractMqSender` / `AbstractMqConsumer`，`sendMsg(tag, msg)` / `sendMsg(tag, msg, delayMinutes)`。

### 3.13 授权与加解密（`next-springboot-starter-crypto`）

该 Starter 复用 License 自动配置类（`com.qiwumind.next.components.crypto.autoconfigure.LicenseAutoConfiguration`，
同样被 `next-springboot-starter-license` 注册），配置前缀同为 `next.license`（配置项见 3.6）。
除 License 校验外，额外提供对称 AES 加解密辅助工具：

- `com.qiwumind.next.components.crypto.core.Enc`（静态工具）：`String encryptData(String)` / `String decryptData(String)`，
  密文固定以 `enc_<DEPLOY_ENV>_` 前缀（如 `enc_test_...`，env 取自 `DEPLOY_ENV` 环境变量，默认 `test`）。
- `com.qiwumind.next.components.crypto.core.DecryptKeyUtil`：`decryptSecretKey(String)` / `encryptData(String)` 解密配置密钥。

```java
// 启动类可显式开启（已在 .imports 注册，不写也可）
@EnableLicense
@SpringBootApplication
public class Application { public static void main(String[] a) { SpringApplication.run(Application.class, a); } }

// 对称加密（无需注入，静态调用）
String cipher = Enc.encryptData("mySecret");
String plain  = Enc.decryptData(cipher);
```

> SM2 / SM4 / RSA 等算法工具位于 `next-common` 的 `util.crypto.{AESUtil,RSAUtils,Sm4Utils}`，按需作为基础库直接调用，
> 不依赖本 Starter 的自动装配。

核心 Bean：`LicenseServiceHelper`（`getValidateResult()` / `isValid()` / `getLicenseInfo()` / `refresh()`）、
`LicenseInterceptor`、`LicenseStartupValidator`。

#### 3.13.1 数据库字段加解密（`@CryptoField`）

在与 MyBatis / MyBatis-Plus 配合时，可零侵入地对指定实体字段做「入库加密、出库解密」，业务无需改动 SQL / XML。

- 自动装配类：`com.qiwumind.next.components.crypto.autoconfigure.CryptoDbAutoConfiguration`
  （`@ConditionalOnClass(MyBatis Interceptor)` + `next.crypto.db.enabled`，默认开启；MyBatis-Plus 会自动收集该拦截器 bean）。
- 开关（可选）：`next.crypto.db.enabled=true`。

**用法：在实体字段上标注 `@CryptoField` 即可**

```java
import com.qiwumind.next.components.crypto.core.db.annotation.CryptoField;

public class UserDO {
    private Long id;

    @CryptoField   // 入库自动 AES 加密（enc_<env>_ 前缀），出库自动解密
    private String phone;

    @CryptoField
    private String idCard;
}
```

```yaml
next:
  crypto:
    db:
      enabled: true   # 默认 true，可关闭
```

实现要点（与业务数据解耦）：

- `CryptoFieldService`：`encrypt` / `decrypt` 做了幂等封装——已加密内容（以 `enc_` 前缀）或空值原样返回；`decrypt` 遇到非密文或解密失败也不抛异常，原样返回，保证业务可读。
- `CryptoInterceptor`：拦截 `ParameterHandler.setParameters`（入库前加密）与 `ResultSetHandler.handleResultSets`（出库后解密）；**加密完成后会还原调用方实体为明文**，不污染业务持有的对象。
- 仅依赖字段注解，对 POJO / `Map` / `Collection` 参数与结果集均生效（含父类字段，`String` 类型）。

### 3.14 认证鉴权 + 操作日志（`next-springboot-starter-security`）

自动装配类：`QiwumindSecurityAutoConfiguration`、`QiwumindWebSecurityConfigurerAdapter`、`QiwumindOperateLogConfiguration`
配置前缀：`next.security`（tokenHeader 默认 `Authorization`、tokenParameter、mockEnable、mockSecret、permitAllUrls、passwordEncoderLength）

核心类：`core.LoginUser`、`core.util.SecurityFrameworkUtils`、`core.filter.TokenAuthenticationFilter`、
操作日志基于 `@LogRecord`（bizlog-sdk）。

```java
LoginUser u = SecurityFrameworkUtils.getLoginUser();
Long id = u != null ? u.getId() : null;
```

### 3.15 服务保护（`next-springboot-starter-protection`）

自动装配类：`QiwumindIdempotentConfiguration`、`QiwumindLock4jConfiguration`、`QiwumindRateLimiterConfiguration`、`QiwumindApiSignatureAutoConfiguration`
底层基于 Redis / Redisson，提供注解驱动的四大能力：

```java
@Idempotent(timeout = 10, keyResolver = UserIdempotentKeyResolver.class) // 幂等
@RateLimiter(count = 100, keyResolver = ClientIpRateLimiterKeyResolver.class) // 限流
@ApiSignature  // 接口签名
@Lock4j        // 分布式锁（baomidou lock4j，配置前缀 lock4j.*）
public void pay() { /* ... */ }
```

### 3.16 多租户（`next-springboot-starter-biz-tenant`）

自动装配类：`QiwumindTenantAutoConfiguration`；配置前缀：`next.tenant`

核心类：`core.context.TenantContextHolder`、`config.TenantProperties`、注解 `@TenantIgnore`、拦截器 `TenantDatabaseInterceptor`。

```java
TenantContextHolder.setTenantId(1L);
Long id = TenantContextHolder.getRequiredTenantId();

@TenantIgnore // 忽略某方法/类的租户过滤
public void noTenant() { /* ... */ }
```

配置项：`enable`(默认 true)、`ignoreUrls`、`ignoreTables`、`ignoreCaches`、`ignoreVisitUrls`。

### 3.17 数据权限（`next-springboot-starter-biz-data-permission`）

自动装配类：`QiwumindDataPermissionAutoConfiguration`、`QiwumindDeptDataPermissionAutoConfiguration`
基于 MyBatis-Plus 拦截器 + AOP 实现 SQL 行级过滤，无独立配置前缀。

核心类：`core.annotation.DataPermission`、`core.rule.DataPermissionRule`（实现 `getTableNames()` / `getExpression()`）、
`rule.dept.DeptDataPermissionRule`、`DataPermissionRuleHandler`。

```java
@DataPermission // 类/方法上声明（默认开启，可 excludeRules/includeRules）
public List<UserDO> list() { return mapper.selectList(); }
```

### 3.18 IP 地理解析（`next-springboot-starter-biz-ip`）

无自动装配（静态工具）。基于 ip2region（xdb）+ area.csv 提供 IP 归属与行政区划查询。
核心类：`core.utils.IPUtils`、`core.utils.AreaUtils`、`core.Area`。

```java
Area area = IPUtils.getArea("114.114.114.114");      // IP -> 地区
String name = AreaUtils.format(area.getId());         // 区域编号 -> 全称（如 上海 上海市 静安区）
Integer parent = AreaUtils.getParentIdByType(id, AreaTypeEnum.CITY);
```

### 3.19 Excel（`next-springboot-starter-excel`）

自动装配类：`QiwumindDictAutoConfiguration`（字典转换等）。基于 fastexcel 的导入导出工具。
核心类：`core.util.ExcelUtils`、`core.annotations.ExcelColumnSelect`、`core.annotations.DictFormat`、
`core.convert.{DictConvert,AreaConvert,MoneyConvert,JsonConvert}`。

```java
ExcelUtils.write(response, "用户.xlsx", "用户", UserExcelVO.class, list); // 导出
List<UserExcelVO> list = ExcelUtils.read(file, UserExcelVO.class);        // 导入
```

### 3.20 Web 聚合能力（`next-springboot-starter-web`）

自动装配类（7 个）：`QiwumindApiLogAutoConfiguration`、`QiwumindJacksonAutoConfiguration`、
`QiwumindSwaggerAutoConfiguration`、`QiwumindWebAutoConfiguration`、`QiwumindXssAutoConfiguration`、
`QiwumindBannerAutoConfiguration`、`QiwumindApiEncryptAutoConfiguration`

聚合 Web 通用能力，子能力配置前缀：

| 子能力 | 配置前缀 | 关键配置 |
|--------|----------|----------|
| Swagger / OpenAPI | `next.swagger` | title / description / author / version / url / email / license |
| XSS 防护 | `next.xss` | enable、excludeUrls |
| API 加解密 | `next.api-encrypt` | enable、header、algorithm、requestKey、responseKey |
| 访问日志 | `next.access-log.enable` | 默认 true |
| Jackson / 全局异常 / Banner | 自动装配 | — |

```java
@ApiAccessLog(operateType = OperateTypeEnum.CREATE) // 访问日志
@ApiEncrypt                                        // 接口级加解密
public void create() { /* ... */ }
```

核心类：`web.core.handler.GlobalExceptionHandler`、`web.core.util.WebFrameworkUtils`、注解 `@ApiAccessLog`。

> 文档/接口说明由 Swagger 生成；若使用 `requiredMode = Schema.RequiredMode.REQUIRED` 注解字段，
> 需确保 `swagger-annotations-jakarta` 版本与 springdoc 锁定版本（2.2.36）一致，否则编译期报「找不到 Schema$RequiredMode」。

### 3.21 WebSocket（`next-springboot-starter-websocket`）

自动装配类：`QiwumindWebSocketAutoConfiguration`；配置前缀：`next.websocket`

配置项：`path`（默认 `/ws`）、`senderType`（默认 `local`，可选 `redis` / `kafka` / `rocketmq`）。

核心类：`core.sender.WebSocketMessageSender`、`core.session.WebSocketSessionManager`、
`core.listener.WebSocketMessageListener`、`core.message.JsonWebSocketMessage`。

```java
@Autowired WebSocketMessageSender sender;
sender.sendObject(userType, userId, "notify", new NotifyMessage());
```

### 3.22 链路追踪 / 指标（`next-springboot-starter-monitor`）

自动装配类：`QiwumindTracerAutoConfiguration`、`QiwumindMetricsAutoConfiguration`
配置前缀：`next.tracer`、`next.metrics.enable`（默认 true）

核心类：`core.annotation.BizTrace`、`core.aop.BizTraceAspect`、`core.filter.TraceFilter`、`core.util.TracerFrameworkUtils`。

```java
@BizTrace(operationName = "创建用户", id = "#user.id", type = "user")
public void create(User user) { /* ... */ }
```

### 3.23 启动 Banner（`next-springboot-starter-banner`）

注册方式：`ApplicationContextInitializer`（文件 `org.springframework.context.ApplicationContextInitializer.imports`），
类 `com.qiwumind.next.components.banner.BannerApplicationContextInitializer`。应用启动即自动打印组件版本 / 框架信息，
无需任何配置或代码。

### 3.24 定价引擎（`next-springboot-starter-pricing`）

自动装配类：`com.qiwumind.next.components.pricing.autoconfigure.PricingAutoConfiguration`
配置前缀：`next.pricing`

```xml
<dependency>
    <groupId>com.qiwumind</groupId>
    <artifactId>next-springboot-starter-pricing</artifactId>
</dependency>
```

```yaml
next:
  pricing:
    enabled: true                  # 默认 true
    aviator-cache-expressions: true  # 默认 true，缓存编译后的 Aviator 表达式
```

```java
@Autowired
private com.qiwumind.next.components.pricing.core.engine.ComputeService computeService;

// 组装定价上下文与活动，执行完整定价管线（优惠券→促销→赠品→运费→礼品卡）
ComputeRespBO resp = computeService.compute(priceBO, allActivityList);
// resp.getCanUseCouponList() / getUsedPromotionList() / getGoodsList() / getShippingFee() ...
```

核心类：`core.engine.ComputeService`、`core.bo.{PriceBO,ActivityBO,ComputeRespBO,GoodsBO}`。

---

### 3.25 计算能力（`next-springboot-starter-compute`）

自动装配类：`com.qiwumind.next.components.compute.autoconfigure.ComputeAutoConfiguration`
配置前缀：`next.compute`（开关 `next.compute.enable=true`）

```xml
<dependency>
    <groupId>com.qiwumind</groupId>
    <artifactId>next-springboot-starter-compute</artifactId>
</dependency>
```

```yaml
next:
  compute:
    enable: true
```

```java
@Autowired
private com.qiwumind.next.components.compute.core.plugin.DEBXService debxService;

ComputeInstallmentResp resp = new ComputeInstallmentResp();
ComputeConfigDTO dto = new ComputeConfigDTO();
dto.setAmount(new BigDecimal("10000"));
dto.setInstallmentNo(12);
ArrayList<ComputeInstallmentResp.InstallmentData> plan =
        debxService.calcCore(resp, dto, new BigDecimal("0.005"), true); // 等额本息
```

类似 Bean 还包含 `FixedRepayDayService`、`DEBXDailyService`、`AprRateComputeService`、`IrrRateComputeService`（bean 名 `irrRateComputeService`）等。

---

### 3.26 XML 文件处理（`next-springboot-starter-file`）

自动装配类：`com.qiwumind.next.components.file.autoconfigure.XmlFileAutoConfigure`（无独立配置前缀，直接装配）

```xml
<dependency>
    <groupId>com.qiwumind</groupId>
    <artifactId>next-springboot-starter-file</artifactId>
</dependency>
```

```java
@Autowired
private com.qiwumind.next.components.file.core.XmlFileBean xmlFileBean;

List<XmlFile> files = xmlFileBean.getXmlFiles();
for (XmlFile f : files) {
    System.out.println(f.getXmlFileName());
}
// 或直接用工具类：XmlFileUtil.xmlread(Reader)
```

---

### 3.27 Groovy 脚本引擎（`next-springboot-starter-groovy` / `next-groovy`）

自动装配类（Starter 已注册 4 个）：`GroovyClasspathLoaderAutoConfiguration`、
`GroovyEngineCoreAutoConfiguration`、`GroovyRedisLoaderAutoConfiguration`、`GroovyMysqlLoaderAutoConfiguration`
配置前缀：`next.groovy.engine`（含子项 `classpath-loader`、`redis-loader`、`mysql-loader`）

```xml
<dependency>
    <groupId>com.qiwumind</groupId>
    <artifactId>next-springboot-starter-groovy</artifactId>
</dependency>
```

```yaml
next:
  groovy:
    engine:
      enable: true
      cache-expire-after-write: 600      # 脚本缓存过期（秒）
      classpath-loader:
        enable: true
        directory: classpath:groovy/      # classpath 下 .groovy 脚本目录
```

```java
@Autowired
private com.qiwumind.next.components.groovy.executor.EngineExecutor engineExecutor; // bean 名 defaultEngineExecutor

public Object run() {
    ScriptQuery query = new ScriptQuery("demoScript"); // 对应脚本的 uniqueKey
    ExecuteParams params = new ExecuteParams();         // 继承 HashMap<String,Object>
    params.put("input", 123);
    EngineExecutorResult result = engineExecutor.execute(query, params);
    return result.isSuccess() ? result.getResult() : null;
}
```

引擎依据 `ScriptQuery.uniqueKey` 从 `ScriptRegistry`（由 Classpath / Redis / Mysql 的 `ScriptLoader` 加载并编译成 `ScriptEntry`）查找脚本，
再基于 `ExecuteParams` 构建 Groovy `Binding` 执行 `script.run()`，`EngineExecutorResult` 封装结果/异常。

---

### 3.28 PDF 生成（`next-springboot-starter-pdf`）

自动装配类：`com.qiwumind.next.components.pdf.autoconfigure.PdfAutoConfiguration`
配置前缀：`next.pdf`（开关 `next.pdf.enabled=true`，默认启用）

```xml
<dependency>
    <groupId>com.qiwumind</groupId>
    <artifactId>next-springboot-starter-pdf</artifactId>
</dependency>
```

```yaml
next:
  pdf:
    enabled: true
    fonts:
      server-dir: /opt/server/fonts
    export:
      page-size: A4
```

```java
@Autowired
private com.qiwumind.next.components.pdf.core.PdfGenerateService pdfGenerateService;

Map<String, Object> data = Map.of("name", "张三");
pdfGenerateService.generateToFile("report.ftl", "/data/reports/report.pdf", data);
// 另支持 generateToStream(...) / generateToResponse(...) / renderHtml(...)
```

---

### 3.29 微信（`next-springboot-starter-wechat`）

> ⚠️ `AutoConfiguration.imports` **仅注册了微信支付** `WechatPayAutoConfiguration`；公众号 `WxMpAutoConfiguration` 存在但**未默认注册**，
> 需手动 `@Import(WxMpAutoConfiguration.class)` 或自行注册才生效。

配置前缀：支付 `wechat.pay`（默认开启）、公众号 `wechat.mp`。

```xml
<dependency>
    <groupId>com.qiwumind</groupId>
    <artifactId>next-springboot-starter-wechat</artifactId>
</dependency>
```

```yaml
wechat:
  pay:
    enabled: true
    api-version: V3
    app-id: wx123
    mch-id: 190000
    mch-key: xxxxxx
    notify-url: https://api.example.com/cb
    cert-path: /cert/apiclient_cert.p12
    api-v3-key: v3key
    private-key-path: /cert/apiclient_key.pem
  mp:
    app-id: wxmp
    secret: mpsecret
    token: mptoken
    aes-key: mpasekey
```

```java
// 微信支付：JSAPI 下单
@Autowired
private com.qiwumind.next.components.wechat.pay.service.WechatPayService wechatPayService;

PayRequest req = new PayRequest();
req.setPayType(PayType.JSAPI);
req.setOutTradeNo("ORD20240101001");
req.setTotalAmount(new BigDecimal("9.90"));
req.setBody("订单");
req.setOpenId("oABC");
Result<PayResult> r = wechatPayService.unifiedOrder(req);
// 其它：createJsapiOrder/createAppOrder/createNativeOrder、createRefund、queryOrder/closeOrder、handleNotify

// 微信公众号（需先 @Import(WxMpAutoConfiguration.class)）
@Autowired
private com.qiwumind.next.components.wechat.mp.service.WechatUserService wechatUserService;
boolean subscribed = wechatUserService.isUserSubscribed("oABC");
```

核心 Bean：支付侧 `WxPayConfig` / `WxPayService` / `WechatPayService`；公众号侧 `WxMpService` / `WxMpTemplateMsgService` / `WechatUserService`。

---

### 3.30 Spring 上下文增强（`next-springboot-starter-context`）

自动装配类：`com.qiwumind.next.components.context.autoconfigure.SpringAutoConfiguration`
配置前缀：`next.context`（开关 `next.context.enable=true`）

```xml
<dependency>
    <groupId>com.qiwumind</groupId>
    <artifactId>next-springboot-starter-context</artifactId>
</dependency>
```

```yaml
next:
  context:
    enable: true
```

```java
// 静态工具，无需注入，任意位置取 Bean
MyService svc = SpringContextHelper.getBean(MyService.class);
svc.doWork();
```

核心类：`context.core.SpringContextHelper`（实现 `ApplicationContextAware`），提供 `getBean(String/Class)`、
`getBeansOfType(Class)`、`containsBean(String)`、`getContext()` 等静态方法。

---

### 3.31 其它基础库 / 占位模块

| 模块 | 说明 / 使用方式 |
|------|-----------------|
| `next-freemarker-generator`（基础库） | MBG + Freemarker 代码生成；在 `generatorConfig.xml` 中配置 `FreemarkerGeneratorPlugin`，手动装配 |
| `next-feign-config`（占位） | Feign + LoadBalancer + OkHttp + Retry 依赖聚合，纯依赖模块，无自动装配 |
| `next-springboot-starter-test` | 测试支撑（测试切片 / 工具），不参与自动加载 |

---

## 4. Starter 自动加载清单（自动加载 + 使用方式）

> 全部 **29 个 Starter**。除 `banner`（用 `ApplicationContextInitializer.imports` 注册）与 `biz-ip`、`test`（无自动加载）外，
> 其余均通过 `org.springframework.boot.autoconfigure.AutoConfiguration.imports` 自动加载。

| # | Starter | 注册方式 | 自动加载类 | 配置前缀 | 使用方式 / 关键入口 |
|---|---------|---------|-----------|---------|---------------------|
| 1 | redis | AutoConfiguration | `RedisAutoConfiguration`、`QiwumindRedisAutoConfiguration`、`QiwumindCacheAutoConfiguration` | `next.redis`（`redis-open=true`） | 注入 `redisTemplate` / `JedisCache` / `LockManager` |
| 2 | cache | AutoConfiguration | `BloomFilterAutoConfiguration`、`CacheAutoConfiguration` | `next.cache`、`next.bloomfilter` | 注入 `BloomFilter` 等本地缓存 Bean |
| 3 | sequence | AutoConfiguration | `SequenceGeneratorAutoConfigure` | — | 注入 `SequenceGenerator.nextId("biz")` |
| 4 | quartz | AutoConfiguration | `QuartzAutoConfiguration` | `next.quartz`（`spring.quartz.*`） | Quartz 定时任务 |
| 5 | license | AutoConfiguration | `license.autoconfigure.LicenseAutoConfiguration` | `next.license` / `next.license` | 注入 `LicenseManager`、`@LicensedFeature` |
| 6 | crypto | AutoConfiguration | `crypto.autoconfigure.LicenseAutoConfiguration` | `next.license` | License 校验 + AES 辅助工具（详见 3.13；SM2/SM4/RSA 算法工具见 `next-common` `util.crypto.*`） |
| 7 | mybatis | AutoConfiguration | `NextDataSourceAutoConfiguration`、`NextMybatisAutoConfiguration`、`NextTranslateAutoConfiguration` | `spring.datasource`+`mybatis` | `@Mapper` 接口 + `UserMapper` |
| 8 | hologres | AutoConfiguration | `HoloDataSourceAutoConfiguration` | `next.hologres` | 数据源注入 |
| 9 | starrocks | AutoConfiguration | `StarRocksDataSourceAutoConfiguration` | `next.starrocks.config`/`.cluster` | 数据源注入 |
| 10 | banner | ApplicationContextInitializer | `banner.BannerApplicationContextInitializer` | — | 启动自动打印，无需代码 |
| 11 | file | AutoConfiguration | `XmlFileAutoConfigure` | — | XML 文件处理 |
| 12 | groovy | AutoConfiguration | `Groovy{ClasspathLoader,EngineCore,RedisLoader,MysqlLoader}AutoConfiguration` | `next.groovy.engine` | 脚本引擎自动装配 |
| 13 | compute | AutoConfiguration | `ComputeAutoConfiguration` | — | 计算能力 |
| 14 | pdf | AutoConfiguration | `PdfAutoConfiguration` | `pdf` | PDF 生成 |
| 15 | wechat | AutoConfiguration | `WechatPayAutoConfiguration` | `wechat.pay` / `wx.mp` | 微信支付 / 公众号 |
| 16 | pricing | AutoConfiguration | `PricingAutoConfiguration` | `next.pricing` | 定价引擎 |
| 17 | context | AutoConfiguration | `SpringAutoConfiguration` | — | Spring 上下文增强 |
| 18 | biz-tenant | AutoConfiguration | `QiwumindTenantAutoConfiguration` | `next.tenant` | `TenantContextHolder`、`@TenantIgnore` |
| 19 | biz-data-permission | AutoConfiguration | `QiwumindDataPermissionAutoConfiguration`、`QiwumindDeptDataPermissionAutoConfiguration` | — | `@DataPermission`、实现 `DataPermissionRule` |
| 20 | biz-ip | 无（静态工具） | — | — | `IPUtils.getArea(...)` / `AreaUtils` |
| 21 | excel | AutoConfiguration | `QiwumindDictAutoConfiguration` | — | `ExcelUtils.write/read(...)` |
| 22 | job | AutoConfiguration | `QiwumindQuartzAutoConfiguration`、`QiwumindAsyncAutoConfiguration` | `spring.quartz.*` | 实现 `JobHandler` + `SchedulerManager.addJob(...)` |
| 23 | monitor | AutoConfiguration | `QiwumindTracerAutoConfiguration`、`QiwumindMetricsAutoConfiguration` | `next.tracer`、`next.metrics.enable` | `@BizTrace` |
| 24 | mq | AutoConfiguration | `QiwumindRedisMQProducerAutoConfiguration`、`QiwumindRedisMQConsumerAutoConfiguration` | — | `RedisMQTemplate.send(...)`、继承 `AbstractRedisStreamMessageListener` |
| 25 | security | AutoConfiguration | `QiwumindSecurityAutoConfiguration`、`QiwumindWebSecurityConfigurerAdapter`、`QiwumindOperateLogConfiguration` | `next.security` | `SecurityFrameworkUtils.getLoginUser()`、`@LogRecord` |
| 26 | web | AutoConfiguration（7 个） | `QiwumindApiLog/Jackson/Swagger/Web/Xss/Banner/ApiEncryptAutoConfiguration` | `next.swagger`/`xss`/`api-encrypt`/`access-log.enable` | `@ApiAccessLog`、`@ApiEncrypt`、Swagger 文档 |
| 27 | websocket | AutoConfiguration | `QiwumindWebSocketAutoConfiguration` | `next.websocket` | `WebSocketMessageSender.sendObject(...)` |
| 28 | protection | AutoConfiguration（4 个） | `QiwumindIdempotent/Lock4j/RateLimiter/ApiSignatureConfiguration` | `lock4j.*`（锁） | `@Idempotent`/`@RateLimiter`/`@ApiSignature`/`@Lock4j` |
| 29 | test | 无（无 `.imports`） | — | — | 测试切片 / 工具，不参与自动加载 |

---

## 5. 配置前缀速查表

| 前缀 | 模块 | 关键项 |
|------|------|--------|
| `next.redis` / `next.redis-open` | redis | host/port/password/database；`redis-open=true` 启用 |
| `next.cache` / `next.bloomfilter` | cache | enabled |
| `next.quartz` + `spring.quartz.*` | quartz / job | 数据源、线程池 |
| `next.license` / `next.license` | license / crypto | file / validation / signing |
| `next.hologres` | hologres | url/username/password |
| `next.starrocks.config` / `.cluster` | starrocks | user/password、fe-host/ports |
| `next.tenant` | biz-tenant | enable、ignoreUrls/Tables/Caches/VisitUrls |
| `next.tracer` / `next.metrics.enable` | monitor | tracer 配置、metrics 开关 |
| `next.security` | security | tokenHeader、permitAllUrls、mockEnable… |
| `next.swagger` | web(swagger) | title/description/author/version… |
| `next.xss` | web(xss) | enable、excludeUrls |
| `next.api-encrypt` | web(api-encrypt) | enable、algorithm、requestKey、responseKey |
| `next.access-log.enable` | web(apilog) | 默认 true |
| `next.websocket` | websocket | path(`/ws`)、senderType(`local`) |
| `next.groovy.engine` | groovy(starter) | enable、pollingCycle、cacheExpireAfterWrite、cacheMaximumSize |
| `lock4j.*` | protection(分布式锁) | baomidou lock4j 原生配置 |
| `pdf` | pdf | PDF 生成配置 |
| `wechat.pay` / `wx.mp` | wechat | 微信支付 / 公众号 |

---

## 6. 自定义 Starter 开发规范（遵循本项目模板）

新增组件请按以下方式接入 Spring Boot 3.5 自动装配：

1. **目录约定**
   ```
   next-springboot-starter-xxx/
   ├── src/main/java/.../xxx/autoconfigure/XxxAutoConfiguration.java
   └── src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
   ```

2. **自动配置类**
   ```java
   @AutoConfiguration
   @ConditionalOnProperty(prefix = "next.xxx", name = "enabled", havingValue = "true", matchIfMissing = true)
   @EnableConfigurationProperties(XxxProperties.class)
   public class XxxAutoConfiguration {

       @Bean
       @ConditionalOnMissingBean
       public XxxService xxxService(XxxProperties properties) {
           return new XxxService(properties);
       }
   }
   ```

3. **注册文件**（`src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`）
   ```
   com.qiwumind.next.components.xxx.autoconfigure.XxxAutoConfiguration
   ```

4. **提交 BOM**：在 `next-dependencies/pom.xml` 的 `<dependencyManagement>` 中追加该 Starter 条目，版本用 `${revision}`。

> ⚠️ 切勿再使用 `META-INF/spring.factories` 注册 `EnableAutoConfiguration`，Spring Boot 3.x 不会读取。

---

## 7. 构建与验证

```bash
# 安装 BOM 与所有 Starter 到本地仓库（BOM 模式下生成依赖管理 POM）
cd next-components
mvn clean install

# 仅验证某个模块
cd next-springboot-starter-redis
mvn -o compile
```

验证自动装配是否生效：

```bash
# 启动应用后查看日志，应包含各 Starter 的自动配置加载信息
# 例如 Redis：******load redisTemplate ******
# 或使用 actuator：
curl http://localhost:8080/actuator/conditions
```
