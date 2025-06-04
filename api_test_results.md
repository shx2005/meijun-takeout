# 美君外卖系统 API 测试结果

## 测试环境

- **测试时间**: 2025-05-02
- **测试工具**: curl 命令行工具
- **测试脚本**: test_script.sh
- **测试目标**: 验证美君外卖系统 API 的登录和授权流程

## 测试结果概述

测试过程中发现所有 API 接口均返回错误状态。具体情况如下：

| 接口 | 状态码 | 错误信息 |
|-----|-------|--------|
| 顾客登录 | 500 | Internal Server Error |
| 获取购物车 | 401 | Unauthorized |
| 商家登录 | 500 | Internal Server Error |
| 获取商家订单 | 401 | Unauthorized |
| 员工登录 | 500 | Internal Server Error |
| 获取订单视图 | 401 | Unauthorized |

## 详细测试结果

### 1. 顾客登录测试

**请求**:
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" \
  -H "userType: 3" \
  -d '{
    "username": "17344402975",
    "password": "20050311"
  }'
```

**响应**:
```json
{
   "error" : "Internal Server Error",
   "path" : "/api/v1/auth/login",
   "status" : 500,
   "timestamp" : 1748869612761
}
```

### 2. 获取顾客购物车

**请求**:
```bash
curl -X GET http://localhost:8080/api/v1/cart \
  -H "customerToken: $USER_TOKEN" \
  -H "Accept: application/json" \
  -H "userType: 3" \
  -H "Content-Type: application/json"
```

**响应**:
```json
{
   "error" : "Unauthorized",
   "path" : "/api/v1/cart",
   "status" : 401,
   "timestamp" : 1748869612832
}
```

### 3. 商家登录测试

**请求**:
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" \
  -H "userType: 1" \
  -d '{
    "username": "merchant1",
    "password": "123456"
  }'
```

**响应**:
```json
{
   "error" : "Internal Server Error",
   "path" : "/api/v1/auth/login",
   "status" : 500,
   "timestamp" : 1748869612853
}
```

### 4. 获取商家订单列表

**请求**:
```bash
curl -X GET http://localhost:8080/api/v1/merchants/orders \
  -H "merchantToken: $MERCHANT_TOKEN" \
  -H "Accept: application/json" \
  -H "userType: 1" \
  -H "Content-Type: application/json"
```

**响应**:
```json
{
   "error" : "Unauthorized",
   "path" : "/api/v1/merchants/orders",
   "status" : 401,
   "timestamp" : 1748869612886
}
```

### 5. 员工登录测试

**请求**:
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" \
  -H "userType: 2" \
  -d '{
    "username": "employee1",
    "password": "123456"
  }'
```

**响应**:
```json
{
   "error" : "Internal Server Error",
   "path" : "/api/v1/auth/login",
   "status" : 500,
   "timestamp" : 1748869612907
}
```

### 6. 获取员工订单视图

**请求**:
```bash
curl -X GET http://localhost:8080/api/v1/orders/overview \
  -H "employeeToken: $EMPLOYEE_TOKEN" \
  -H "Accept: application/json" \
  -H "userType: 2" \
  -H "Content-Type: application/json"
```

**响应**:
```json
{
   "error" : "Unauthorized",
   "path" : "/api/v1/orders/overview",
   "status" : 401,
   "timestamp" : 1748869612938
}
```

## 错误分析与解决方案

### 1. 登录接口返回 500 错误

**可能原因**:
- 服务器内部错误，可能是登录接口实现存在问题
- 数据库连接问题或服务未正确启动
- 用户凭据验证失败导致后端异常
- 请求格式不符合后端预期

**解决方案**:
- 检查服务器日志以获取详细的错误信息
- 验证数据库连接和测试账户是否正确配置
- 确保请求头和请求体格式符合API规范
- 如果后端代码可访问，检查登录接口的实现逻辑

### 2. 其他接口返回 401 未授权错误

**可能原因**:
- 由于登录失败，未能获取有效的认证令牌
- 令牌格式或传递方式不正确
- 后端认证拦截器配置问题
- userType 参数与令牌不匹配

**解决方案**:
- 确保先成功获取认证令牌再访问其他接口
- 验证请求头中令牌的格式和命名是否符合规范
- 检查后端认证拦截器的配置是否正确
- 确保 userType 参数与使用的令牌类型相匹配

## 后续测试建议

1. **分阶段测试**:
   - 先单独测试登录接口，确保能获取有效令牌
   - 成功获取令牌后，再测试其他接口

2. **检查系统配置**:
   - 验证数据库和应用配置是否正确
   - 确认测试环境和生产环境的差异

3. **调整请求格式**:
   - 尝试不同的请求头格式和命名约定
   - 参考开发文档中的确切API规范

4. **查看详细日志**:
   - 获取服务器端详细日志以定位具体错误
   - 使用调试工具或添加日志语句以跟踪请求处理流程

## 结论

本次测试发现美君外卖系统的API接口目前存在认证和授权问题。登录接口返回服务器内部错误（500），导致无法获取有效的认证令牌，从而使其他接口都返回未授权错误（401）。

建议与开发团队合作，检查服务器日志并修复登录接口问题，确保认证流程正常工作。在登录接口修复后，重新进行完整的API测试。 