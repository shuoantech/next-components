# License 生产与部署指南

## 一、整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                     License管理流程                         │
├─────────────────────────────────────────────────────────────┤
│  1. 密钥对生成 (离线环境)                                   │
│     └─ 私钥(private.key) → 安全存储                        │
│     └─ 公钥(public.key)  → 随应用部署                      │
│                                                             │
│  2. License生成 (离线环境)                                   │
│     └─ 使用私钥签名License                                   │
│     └─ 生成license.dat文件                                 │
│                                                             │
│  3. License部署 (目标环境)                                   │
│     └─ 部署license.dat到目标服务器                          │
│     └─ 部署public.key到应用程序                            │
└─────────────────────────────────────────────────────────────┘
```

## 二、密钥对生成

### 2.1 生成方式

#### 方式一：使用API生成（开发/测试环境）

```bash
# 启动demo应用后调用
curl -X POST "http://localhost:8080/api/keys/generate?privateKeyPath=keys/private.key&publicKeyPath=keys/public.key"
```

#### 方式二：使用Java代码生成

```java
SignatureProvider provider = new SignatureProvider(properties);
provider.generateKeyPair("keys/private.key", "keys/public.key");
```

#### 方式三：使用OpenSSL命令行生成（推荐生产环境）

```bash
# 生成私钥（2048位）
openssl genrsa -out private.key 2048

# 从私钥提取公钥
openssl rsa -in private.key -pubout -out public.key
```

### 2.2 密钥管理规范

| 文件 | 位置 | 权限 | 安全要求 |
|------|------|------|----------|
| `private.key` | 安全服务器/硬件加密机 | 仅管理员可访问 | **严禁部署到生产服务器** |
| `public.key` | 随应用部署 | 只读 | 可公开分发 |

## 三、License生成

### 3.1 使用API生成

```bash
curl -X POST "http://localhost:8080/api/license/generate" \
  -H "Content-Type: application/json" \
  -d '{
    "subject": "企业版授权",
    "issuer": "QiuwuMind",
    "days": 365,
    "features": ["advanced-report", "api-access", "data-export"],
    "maxUsers": 100,
    "maxConnections": 1000,
    "bindToHardware": true
  }'
```

### 3.2 使用Java代码生成

```java
@Autowired
private LicenseGenerator generator;

LicenseInfo license = generator.generate(
    LicenseInfo.builder()
        .subject("企业版授权")
        .issuer("QiuwuMind")
        .expireDate(ZonedDateTime.now().plusDays(365))
        .features(Set.of("advanced-report", "api-access"))
        .limits(LicenseLimits.builder()
            .maxUsers(100)
            .maxConnections(1000)
            .build())
        .binding(LicenseBinding.builder()
            .hardwareFingerprint(HostInfoUtils.getHardwareFingerprint())
            .allowedIps(HostInfoUtils.getLocalIps())
            .allowedMacs(HostInfoUtils.getLocalMacs())
            .build())
);
```

### 3.3 License数据结构

```json
{
    "licenseId": "uuid-xxx",
    "subject": "企业版授权",
    "issuer": "QiuwuMind",
    "issueDate": "2024-01-01T00:00:00Z",
    "expireDate": "2025-01-01T00:00:00Z",
    "features": ["advanced-report", "api-access"],
    "limits": {
        "maxUsers": 100,
        "maxConnections": 1000
    },
    "binding": {
        "hardwareFingerprint": "xxx-xxx-xxx",
        "allowedIps": ["192.168.1.100"],
        "allowedMacs": ["00:11:22:33:44:55"]
    },
    "signature": "xxx"
}
```

## 四、生产环境部署

### 4.1 部署架构

```
生产服务器
├── /opt/app/
│   ├── application.yml      # 应用配置
│   ├── keys/
│   │   └── public.key       # 公钥（只读）
│   └── license/
│       └── license.dat      # License文件（只读）
```

### 4.2 部署步骤

#### 步骤1：准备部署文件

```bash
# 在生产服务器上创建目录结构
mkdir -p /opt/app/keys
mkdir -p /opt/app/license

# 设置权限（重要！）
chown -R appuser:appgroup /opt/app
chmod 600 /opt/app/keys/public.key
chmod 600 /opt/app/license/license.dat
```

#### 步骤2：部署公钥

```bash
# 将公钥复制到生产服务器
scp public.key user@prod-server:/opt/app/keys/
```

#### 步骤3：部署License文件

```bash
# 将生成的license.dat复制到生产服务器
scp license.dat user@prod-server:/opt/app/license/
```

#### 步骤4：配置application.yml

```yaml
next:
  license:
    enabled: true
    file:
      path: /opt/app/license/license.dat
      auto-create: false      # 生产环境禁用自动创建
    validation:
      strict-mode: true
      grace-period: 7d
      validate-on-startup: true
      global-block-enabled: true  # License失效时拦截所有接口
      exclude-paths:
        - /health
        - /actuator/health
    signing:
      public-key-path: /opt/app/keys/public.key
      algorithm: SHA384withRSA
    binding:
      require-ip-binding: true
      require-mac-binding: false
      require-hardware-binding: true
```

### 4.3 启动验证

```bash
# 启动应用
java -jar app.jar --spring.config.location=/opt/app/application.yml

# 验证License状态
curl http://localhost:8080/api/license/status
```

## 五、License更新流程

### 5.1 滚动更新（推荐）

```bash
# 1. 生成新License（离线环境）
curl -X POST "http://localhost:8080/api/license/generate" \
  -H "Content-Type: application/json" \
  -d '{"subject": "企业版授权", "days": 365}' > new_license.dat

# 2. 备份旧License
cp /opt/app/license/license.dat /opt/app/license/license.dat.backup

# 3. 部署新License（零停机）
scp new_license.dat user@prod-server:/opt/app/license/license.dat

# 4. 触发重新验证
curl -X POST "http://localhost:8080/api/license/revalidate"
```

### 5.2 自动检测更新

配置定期检查：

```yaml
next:
  license:
    validation:
      auto-reload-enabled: true    # 启用自动重载
      reload-interval: 5m          # 每5分钟检查一次
```

## 六、安全最佳实践

### 6.1 密钥安全

| 措施 | 说明 |
|------|------|
| 私钥离线存储 | 私钥必须存储在安全的离线环境，严禁部署到生产服务器 |
| 硬件加密机 | 推荐使用HSM（硬件安全模块）存储私钥 |
| 访问控制 | 私钥文件权限设置为600，仅管理员可访问 |

### 6.2 License安全

| 措施 | 说明 |
|------|------|
| 签名验证 | 所有License必须经过数字签名验证 |
| 绑定验证 | 生产环境建议启用硬件绑定 |
| 传输加密 | License传输使用HTTPS或加密通道 |

### 6.3 部署安全

| 措施 | 说明 |
|------|------|
| 文件权限 | License文件设置为只读，权限600 |
| 配置加密 | 敏感配置建议使用Spring Cloud Config加密 |
| 日志审计 | 记录License验证日志，便于审计 |

## 七、故障排除

### 7.1 常见错误

| 错误码 | 原因 | 解决方案 |
|--------|------|----------|
| `LICENSE_INVALID` | License签名验证失败 | 检查License文件是否被篡改 |
| `LICENSE_EXPIRED` | License已过期 | 联系管理员获取新License |
| `BINDING_MISMATCH` | 硬件绑定不匹配 | 检查服务器硬件信息或重新生成License |
| `PUBLIC_KEY_NOT_FOUND` | 公钥文件不存在 | 检查公钥路径配置 |

### 7.2 日志分析

```bash
# 查看License验证日志
grep "License" application.log

# 查看验证失败原因
grep "LICENSE_INVALID" application.log
```

## 八、部署清单

| 检查项 | 状态 |
|--------|------|
| [ ] 私钥已安全存储（离线环境） |
| [ ] 公钥已部署到生产服务器 |
| [ ] License文件已部署 |
| [ ] 文件权限已设置为600 |
| [ ] 配置文件中auto-create已禁用 |
| [ ] 绑定验证已启用 |
| [ ] 全局拦截已启用 |
| [ ] 排除路径已正确配置 |
| [ ] 应用启动成功 |
| [ ] License验证通过 |