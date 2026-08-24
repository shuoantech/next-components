# License Starter Demo

本项目展示如何在外部Spring Boot项目中使用 `next-springboot-starter-license` 二方库。

## 快速开始

### 1. 添加依赖

在 `pom.xml` 中添加以下依赖：

```xml
<dependency>
    <groupId>com.qiwumind</groupId>
    <artifactId>next-springboot-starter-license</artifactId>
    <version>1.1.0.jdk21-SNAPSHOT</version>
</dependency>
```

### 2. 配置License

在 `application.yml` 中配置License相关参数：

```yaml
next:
  license:
    enabled: true
    file:
      path: license/license.dat
      auto-create: true
    validation:
      strict-mode: true
      grace-period: 7d
      validate-on-startup: true
    signing:
      public-key-path: keys/public.key
      algorithm: SHA384withRSA
```

### 3. 创建密钥对

首次使用需要生成RSA密钥对：

```bash
# 创建密钥目录
mkdir -p keys

# 使用API生成密钥对（推荐）
curl -X POST "http://localhost:8080/api/keys/generate?privateKeyPath=keys/private.key&publicKeyPath=keys/public.key"
```

### 4. 启动应用

```bash
mvn spring-boot:run
```

## 使用示例

### 功能授权注解

```java
@RestController
@RequestMapping("/api")
public class FeatureController {

    @LicensedFeature(value = "advanced-report", message = "未授权使用高级报表功能")
    @GetMapping("/report/advanced")
    public String advancedReport() {
        return "高级报表功能 - 已授权访问";
    }

    @LicensedFeature(value = "api-access", strict = false, message = "API访问受限")
    @GetMapping("/data")
    public String apiAccess() {
        return "API数据访问";
    }
}
```

### License管理API

| API | 方法 | 说明 |
|-----|------|------|
| `/api/license/status` | GET | 获取License验证状态 |
| `/api/license/info` | GET | 获取当前License信息 |
| `/api/license/remaining-days` | GET | 获取剩余天数 |
| `/api/license/validate` | POST | 验证License内容 |
| `/api/license/generate` | POST | 生成新License |
| `/api/system/info` | GET | 获取系统信息 |
| `/api/keys/generate` | POST | 生成密钥对 |

## License配置说明

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `next.license.enabled` | 是否启用License模块 | `true` |
| `next.license.file.path` | License文件路径 | `license/license.dat` |
| `next.license.file.auto-create` | 是否自动创建试用License | `true` |
| `next.license.validation.strict-mode` | 严格模式 | `true` |
| `next.license.validation.grace-period` | 宽限期 | `7d` |
| `next.license.validation.validate-on-startup` | 启动时验证 | `true` |
| `next.license.validation.global-block-enabled` | **启用全局拦截**（License失效时拦截所有接口） | `false` |
| `next.license.validation.exclude-paths` | **全局拦截时排除的路径列表** | - |
| `next.license.signing.public-key-path` | 公钥路径 | `keys/public.key` |
| `next.license.signing.algorithm` | 签名算法 | `SHA384withRSA` |

## 核心API

```java
@Autowired
private LicenseManager licenseManager;

// 验证License
LicenseVerifyResult result = licenseManager.validateCurrentLicense();

// 检查功能授权
boolean hasFeature = licenseManager.hasFeature("advanced-report");

// 获取剩余天数
long remainingDays = licenseManager.getRemainingDays();

// 获取当前License信息
LicenseInfo license = licenseManager.getCurrentLicense();
```

## 技术特性

- **自动配置**：Spring Boot自动装配，开箱即用
- **数字签名**：RSA-SHA384签名验证，确保License完整性
- **多维度绑定**：支持IP、MAC、硬件指纹绑定
- **宽限期机制**：License过期后可配置宽限期
- **AOP切面**：方法级授权检查，灵活控制功能访问