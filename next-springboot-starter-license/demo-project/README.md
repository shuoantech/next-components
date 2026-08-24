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
| `/api/health` | GET | 健康检查 |
| `/api/license/status` | GET | 获取License验证状态 |
| `/api/license/info` | GET | 获取当前License信息 |
| `/api/license/remaining-days` | GET | 获取剩余天数 |
| `/api/license/valid` | GET | 验证License是否有效 |
| `/api/license/validate` | POST | 验证License内容 |
| `/api/license/revalidate` | POST | 重新验证当前License |
| `/api/license/generate` | POST | 生成新License |
| `/api/system/info` | GET | 获取系统信息 |
| `/api/keys/generate` | POST | 生成密钥对 |
| `/api/features/advanced-report` | GET | 高级报表（需授权） |
| `/api/features/api` | GET | API访问（宽松模式） |
| `/api/features/basic` | GET | 基础功能（无需授权） |
| `/api/business/user` | GET | **获取用户信息（普通业务接口）** |
| `/api/business/product` | GET | **获取商品信息（普通业务接口）** |
| `/api/business/order` | GET | **获取订单信息（普通业务接口）** |

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

## 全局拦截器（License失效时拦截所有接口）

### 启用方式

在 `application.yml` 中配置：

```yaml
next:
  license:
    validation:
      global-block-enabled: true  # 启用全局拦截
      exclude-paths:              # 排除不需要拦截的路径
        - /health
        - /api/license/**
        - /api/system/info
```

### 工作原理

```
请求 → LicenseInterceptor → LicenseManager.isLicenseValid()
         ↓
    License有效 → 放行
    License失效 → 返回403错误
```

### 拦截效果

当License失效时，所有接口请求（除排除路径外）会被拦截并返回：

```json
{
    "code": "LICENSE_INVALID",
    "message": "License已失效，请联系管理员获取有效License",
    "timestamp": "2024-01-15T10:30:00",
    "path": "/api/business/user",
    "detail": "签名验证失败"
}
```

### 测试步骤

1. 启动应用，先调用业务接口验证正常
2. 备份 `license/license.dat` 为 `license/license.dat.backup`
3. 编辑 `license/license.dat`，修改内容使签名失效
4. 调用业务接口，会被拦截并返回403错误
5. 调用排除路径（如 `/api/license/status`），可以正常访问
6. 恢复备份的 `license.dat`，验证恢复正常

### 两种拦截方式对比

| 方式 | 作用范围 | 适用场景 |
|------|----------|----------|
| **全局拦截器** | 所有接口（除排除路径） | License失效时系统不可用 |
| **@LicensedFeature注解** | 单个方法/类 | 功能级细粒度授权 |

### 业务接口（用于测试拦截）

| API | 方法 | 说明 |
|-----|------|------|
| `/api/business/user` | GET | 获取用户信息（普通业务接口） |
| `/api/business/product` | GET | 获取商品信息（普通业务接口） |
| `/api/business/order` | GET | 获取订单信息（普通业务接口） |

## 技术特性

- **自动配置**：Spring Boot自动装配，开箱即用
- **数字签名**：RSA-SHA384签名验证，确保License完整性
- **多维度绑定**：支持IP、MAC、硬件指纹绑定
- **宽限期机制**：License过期后可配置宽限期
- **AOP切面**：方法级授权检查，灵活控制功能访问
- **全局拦截器**：License失效时自动拦截所有接口