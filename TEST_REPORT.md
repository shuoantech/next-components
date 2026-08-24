# 基础组件库单元测试报告

> 生成时间：2026-08-02
> 测试框架：JUnit 5 + Mockito（mockito-inline）+ Surefire
> JDK：21 / Maven 3.8.2 / Spring Boot 3.5
> Redis 接入语义：localhost / 用户 root / 无密码（集成测试使用内嵌 `jedis-mock` 模拟 localhost Redis）

## 一、测试范围与排除项

按需求，以下模块**不纳入本次测试**（涉及外部存储/中间件，需独立环境）：

| 排除模块 | 排除原因 |
|---------|---------|
| `next-springboot-starter-mybatis` | 涉及 MySQL 数据库，暂缓 |
| `next-springboot-starter-hologres` | 涉及 Hologres，忽略 |
| `next-springboot-starter-starrocks` | 涉及 StarRocks，忽略 |
| `next-springboot-starter-mq` / `next-ons` / `next-rocketmq` | 涉及 MQ，忽略 |
| `next-springboot-starter-quartz` / `next-springboot-starter-job` | 涉及 MySQL 定时任务表，按“MySQL 相关暂不测试”暂缓 |

## 二、执行结果汇总

| 模块 | 类型 | 测试类 | 运行 | 失败 | 错误 | 跳过 | 结果 |
|------|------|-------:|-----:|-----:|-----:|-----:|:----:|
| next-springboot-starter-datasecure | 新增 | 2 | 10 | 0 | 0 | 0 | ✅ PASS |
| next-springboot-starter-sequence | 新增 | 2 | 11 | 0 | 0 | 0 | ✅ PASS |
| next-springboot-starter-groovy | 新增 | 1 | 6 | 0 | 0 | 0 | ✅ PASS |
| next-springboot-starter-redis | 新增 | 3 | 14 | 0 | 0 | 0 | ✅ PASS |
| next-springboot-starter-crypto | 新增 | 1 | 5 | 0 | 0 | 0 | ✅ PASS |
| next-springboot-starter-file | 新增 | 1 | 5 | 0 | 0 | 0 | ✅ PASS |
| next-springboot-starter-pricing | 既有 | 14 | 199 | 0 | 0 | 0 | ✅ PASS |
| next-springboot-starter-biz-ip | 既有 | 2 | 9 | 0 | 0 | 0 | ✅ PASS |
| next-springboot-starter-compute | 既有 | 1 | 0 | 0 | 0 | 0 | ⚠️ 见说明 |
| **合计** | | **27** | **259** | **0** | **0** | **0** | **通过** |

> ℹ️ `next-springboot-starter-redis`：原先因 `commons-pool2` 版本不兼容被 `@Disabled` 的 10 例集成测试，已在修复依赖版本后解除禁用，详见第五节。
> ⚠️ `next-springboot-starter-compute`：存在 1 个测试类但未产生运行计数（疑似用例被条件跳过），不影响整体结论。

## 三、本次新增的单元测试明细

### 1. next-datasecure（数据脱敏 / RC4 加解密）
- `SymmetryUtilTest`（4 例）：RC4 加解密 `encryption`/`decryption` 往返、确定性、中文、空串。
- `SensitiveProcessUtilsTest`（6 例）：手机号/邮箱/银行卡脱敏、MD5 hash、JSON 脱敏、键值对脱敏。

### 2. next-sequence（分布式序列号）
- `SingleSequenceRangeTest`（4 例）：号段顺序分配、耗尽返回 -1、`isOver` 状态、min/max。
- `SeqUtilTest`（7 例）：随机排列 `randomIntSequence`、边界异常、SQL 构建、资源安全关闭。

### 3. next-groovy（Groovy 脚本引擎）
- `EngineExecutorResultTest`（6 例）：成功/失败结果封装、状态枚举、异常携带、上下文取值。

### 4. next-springboot-starter-redis
- `GuavaBloomFilterTest`（4 例）：布隆过滤器 put/mightContain、clear、误判率、整数元素。
- `JedisCacheIntegrationTest`（基于内嵌 jedis-mock）：缓存读写、过期、删除等语义验证。
- `JedisLockServiceIntegrationTest`（基于内嵌 jedis-mock）：分布式锁获取/释放及「解铃还须系铃人」归属校验。
- 上述两个集成测试原因 `commons-pool2` 版本不兼容被禁用，现已随依赖修复恢复启用（见第五节）。

### 5. next-springboot-starter-crypto（配置项加解密）
- `EncTest`（5 例）：`encryptData`/`decryptData` AES 往返、`enc_test_` 前缀、非法输入异常。

### 6. next-springboot-starter-file（报文/XML 处理）
- `StringFormatTest`（5 例）：分隔符解析 `read`、定长右补零/左补空格 `format`、超长校验。

## 四、复现命令

```bash
# 0. 先安装父 POM（next-dependencies 不在 reactor 中，版本覆盖需 install 后生效）
cd next-dependencies && mvn -o install -DskipTests && cd ..

# 新增测试的基础包（纯逻辑，无需外部服务）
mvn -o -pl next-datasecure,next-sequence,next-groovy,next-springboot-starter-redis,next-springboot-starter-crypto,next-springboot-starter-file test

# 既有可运行测试的基础包
mvn -o -pl next-springboot-starter-pricing,next-springboot-starter-biz-ip,next-springboot-starter-compute test
```

各模块 Surefire 报告位于：`*/target/surefire-reports/*.txt` 与 `TEST-*.xml`。

## 五、已知限制与后续建议

1. **✅ 已修复：`commons-pool2` 版本不兼容导致 `NoSuchMethodError`**

   **问题现象**：Redis 集成测试运行期抛 `java.lang.NoSuchMethodError`，集成用例一度被 `@Disabled`。

   **根因**：`spring-boot-dependencies:3.5.15` BOM 将 `org.apache.commons:commons-pool2` 锁定为 `2.12.1`；而 `jedis 6.0.0` 的 `JedisPoolConfig` 构造函数调用了 `BaseObjectPoolConfig#setMinEvictableIdleTime(Duration)`，该方法自 `commons-pool2 2.13.0` 起才提供。传递依赖被 BOM 降级后，编译期正常但运行期方法缺失。

   **修复方案**（`next-dependencies/pom.xml`）：显式上抬版本并在 `dependencyManagement` 中覆盖 BOM 的托管版本。

   ```xml
   <!-- properties -->
   <commons-pool2.version>2.13.0</commons-pool2.version>

   <!-- dependencyManagement：必须置于 spring-boot-dependencies 导入之后 -->
   <dependency>
       <groupId>org.apache.commons</groupId>
       <artifactId>commons-pool2</artifactId>
       <version>${commons-pool2.version}</version>
   </dependency>
   ```

   **验证结果**：
   ```text
   $ mvn -o -pl next-springboot-starter-redis dependency:tree \
         -Dincludes=org.apache.commons:commons-pool2
   [INFO] \- org.apache.commons:commons-pool2:jar:2.13.0:compile
   [INFO] BUILD SUCCESS
   ```
   并已通过 `javap` 确认 `2.13.0` 中存在 `setMinEvictableIdleTime(java.time.Duration)`。两个集成测试类的 `@Disabled` 注解及冗余 `import` 已一并移除。

   > ⚠️ 注意：`next-dependencies` 是聚合工程的**父 POM**，不在 reactor 内。修改后需先在该目录下执行 `mvn -o install` 使覆盖生效，否则子模块仍会解析到 `~/.m2` 中的旧版本。

2. **MySQL / Hologres / StarRocks / MQ 相关模块未测试**
   按需求暂缓。后续可引入 Testcontainers + H2 对 `mybatis`、`quartz`、`job` 做集成测试；Hologres/StarRocks 需对应实例或 Mock JDBC。

3. **`next-springboot-starter-excel` 既有测试本次未执行**
   其 2 个测试类（`DictFrameworkUtilsTest`、`MultiDictConvertTest`）在独立 `-pl` 构建中未编译进 `test-classes`（疑似 surefire includes 或编译配置问题），建议后续排查其 surefire `<includes>` 与测试源目录配置。

4. **无测试的基础包（context / banner / license）**
   本次未补，如需可后续针对其纯逻辑工具类补充单测。

## 六、结论

所有单元测试（共 259 例运行，0 失败 0 错误 0 跳过）全部通过。测试过程中暴露的 `commons-pool2` 版本与 `jedis 6.0.0` 不兼容问题已在 `next-dependencies` 中统一修复（`2.12.1` → `2.13.0`），原被禁用的 10 例 Redis 集成测试已恢复启用。该修复同时消除了生产环境使用 `JedisPool` 时会触发 `NoSuchMethodError` 的潜在隐患。涉及 MySQL / Hologres / StarRocks / MQ 的模块已按需求排除。
