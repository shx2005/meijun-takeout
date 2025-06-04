# 美君外卖系统 API 测试指南（更新版）

本文档提供了使用 curl 命令行工具测试美君外卖系统 API 的详细说明。根据系统约定的请求格式，所有请求需要包含特定的请求头。

## 请求头格式说明

所有API请求需要包含以下请求头：

- `userType`: 用户类型，取值为0-3
  - 0: 管理员(admin)
  - 1: 商家(merchant)
  - 2: 员工(employee)
  - 3: 顾客(customer)
- `{userType}Token`: 根据用户类型设置对应的token请求头
  - 当userType=0时，使用`adminToken`
  - 当userType=1时，使用`merchantToken`
  - 当userType=2时，使用`employeeToken`
  - 当userType=3时，使用`customerToken`
- `Accept`: 指定接受的响应格式，通常为 `application/json`
- `Content-Type`: 指定请求体格式，通常为 `application/json`

## 环境准备

在开始测试之前，请确保:

1. 已安装 curl 命令行工具
2. 已导入测试数据库 (使用 `test_full.sql`)
3. 后端服务已正常启动

## 测试账户信息

本测试指南使用以下测试账户:

- **顾客账户**: 
  - 用户名: 17344402975
  - 密码: 20050311
  - userType: 3

- **商家账户**: 
  - 用户名: merchant1
  - 密码: 123456
  - userType: 1
  
- **员工账户**:
  - 用户名: employee1
  - 密码: 123456
  - userType: 2

## 1. 顾客登录测试

### 请求示例

> **重要提示**: 登录请求必须包含 `identity` 字段，并且值应该与相应的用户类型匹配。

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" \
  -H "userType: 3" \
  -d '{
    "username": "17344402975",
    "password": "20050311",
    "identity": "CUSTOMER"
  }' | json_pp
```

### 预期响应

```json
{
  "code": 200,
  "msg": "OK",
  "data": {
    "id": 1,
    "username": "17344402975",
    "name": "shx",
    "uuid": "79111e92-36ec-11f0-91e0-6121812639ae",
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9..."
  },
  "success": true
}
```

### 保存Token

请将响应中的 token 值保存下来，用于后续 API 请求的认证。

```bash
# 将token保存到环境变量
export CUSTOMER_TOKEN="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9..."
```

## 2. 获取顾客购物车信息

### 请求示例

```bash
curl -X GET http://localhost:8080/api/v1/cart \
  -H "customerToken: $CUSTOMER_TOKEN" \
  -H "Accept: application/json" \
  -H "userType: 3" \
  -H "Content-Type: application/json" | json_pp
```

### 预期响应

```json
{
  "code": 0,
  "msg": "获取购物车成功",
  "data": {
    "id": 1,
    "userId": 1,
    "items": [
      {
        "id": 1,
        "name": "鱼香肉丝",
        "cartId": 1,
        "userId": 1,
        "itemId": 1,
        "itemType": "DISH",
        "quantity": 2,
        "unitPrice": 28.00,
        "totalPrice": 56.00
      },
      {
        "id": 2,
        "name": "宫保鸡丁",
        "cartId": 1,
        "userId": 1,
        "itemId": 2,
        "itemType": "DISH",
        "quantity": 1,
        "unitPrice": 32.00,
        "totalPrice": 32.00
      }
    ],
    "createTime": "2023-05-27 10:30:00",
    "updateTime": "2023-05-27 10:30:00"
  },
  "success": true
}
```

## 3. 商家登录测试

### 请求示例

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" \
  -H "userType: 1" \
  -d '{
    "username": "merchant1",
    "password": "123456",
    "identity": "MERCHANT"
  }' | json_pp
```

### 预期响应

```json
{
  "code": 200,
  "msg": "OK",
  "data": {
    "id": 2,
    "username": "merchant1",
    "name": "商家1",
    "uuid": "merchant-uuid-123",
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9..."
  },
  "success": true
}
```

### 保存商家Token

```bash
# 将商家token保存到环境变量
export MERCHANT_TOKEN="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9..."
```

## 4. 获取商家订单列表

### 请求示例

```bash
curl -X GET http://localhost:8080/api/v1/merchants/orders \
  -H "merchantToken: $MERCHANT_TOKEN" \
  -H "Accept: application/json" \
  -H "userType: 1" \
  -H "Content-Type: application/json" | json_pp
```

### 预期响应

```json
{
  "code": 0,
  "msg": "获取订单列表成功",
  "data": [
    {
      "id": 3,
      "userId": 1,
      "merchantId": 2,
      "orderTime": "2023-05-27 13:30:25",
      "status": "COMPLETED",
      "payStatus": "PAID",
      "total": 88.00,
      "orderNumber": "ORD2023052713302501",
      "userName": "shx"
    },
    {
      "id": 2,
      "userId": 1,
      "merchantId": 2,
      "orderTime": "2023-05-26 18:05:16",
      "status": "PENDING",
      "payStatus": "UNPAID",
      "total": 76.00,
      "orderNumber": "ORD2023052618051602",
      "userName": "shx"
    },
    {
      "id": 1,
      "userId": 1,
      "merchantId": 2,
      "orderTime": "2023-05-24 18:05:03",
      "status": "COMPLETED",
      "payStatus": "PAID",
      "total": 114.00,
      "orderNumber": "ORD2023052418050303",
      "userName": "shx"
    }
  ]
}
```

## 5. 员工登录测试

### 请求示例

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" \
  -H "userType: 2" \
  -d '{
    "username": "employee1",
    "password": "123456",
    "identity": "EMPLOYEE"
  }' | json_pp
```

### 预期响应

```json
{
  "code": 200,
  "msg": "OK",
  "data": {
    "id": 3,
    "username": "employee1",
    "name": "员工1",
    "uuid": "employee-uuid-123",
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9..."
  },
  "success": true
}
```

### 保存员工Token

```bash
# 将员工token保存到环境变量
export EMPLOYEE_TOKEN="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9..."
```

## 6. 员工查看订单

### 请求示例

```bash
curl -X GET http://localhost:8080/api/v1/orders/overview \
  -H "employeeToken: $EMPLOYEE_TOKEN" \
  -H "Accept: application/json" \
  -H "userType: 2" \
  -H "Content-Type: application/json" | json_pp
```

### 预期响应

```json
{
  "code": 0,
  "msg": "获取订单列表成功",
  "data": [
    {
      "id": 3,
      "userId": 1,
      "merchantId": 2,
      "orderTime": "2023-05-27 13:30:25",
      "status": "COMPLETED",
      "payStatus": "PAID",
      "total": 88.00,
      "orderNumber": "ORD2023052713302501",
      "userName": "shx"
    },
    {
      "id": 2,
      "userId": 1,
      "merchantId": 2,
      "orderTime": "2023-05-26 18:05:16",
      "status": "PENDING",
      "payStatus": "UNPAID",
      "total": 76.00,
      "orderNumber": "ORD2023052618051602",
      "userName": "shx"
    },
    {
      "id": 1,
      "userId": 1,
      "merchantId": 2,
      "orderTime": "2023-05-24 18:05:03",
      "status": "COMPLETED",
      "payStatus": "PAID",
      "total": 114.00,
      "orderNumber": "ORD2023052418050303",
      "userName": "shx"
    }
  ]
}
```

## 常见问题与解决方案

### 1. 认证失败

如果您收到类似以下的错误：

```json
{
  "code": 401,
  "msg": "未授权，请登录",
  "data": null
}
```

请检查：
- Token 是否已过期
- 请求头中是否使用了正确的token类型（如customerToken, merchantToken等）
- userType值是否正确设置
- 是否使用了正确的 token（如顾客token不能用于商家API）

### 2. 请求格式错误

如果收到类似以下错误：

```json
{
  "code": 400,
  "msg": "请求格式错误",
  "data": null
}
```

请检查：
- 请求头格式是否正确
- JSON 请求体是否格式正确
- 是否提供了所有必需的字段

### 3. 登录时返回500错误

如果登录时收到500内部服务器错误：

```json
{
  "error": "Internal Server Error",
  "path": "/api/v1/auth/login",
  "status": 500,
  "timestamp": 1748869862589
}
```

请检查：
- 登录请求中是否包含了 `identity` 字段
- `identity` 字段的值是否与用户类型匹配（CUSTOMER, MERCHANT, EMPLOYEE, ADMIN）
- 用户名和密码是否正确

## 测试完整流程

以下是一个完整测试流程的 shell 脚本示例：

```bash
#!/bin/bash

# 1. 顾客登录
echo "========== 顾客登录测试 =========="
USER_LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" \
  -H "userType: 3" \
  -d '{
    "username": "17344402975",
    "password": "20050311",
    "identity": "CUSTOMER"
  }')

echo $USER_LOGIN_RESPONSE | json_pp

# 提取token
USER_TOKEN=$(echo $USER_LOGIN_RESPONSE | grep -o '"token":"[^"]*' | sed 's/"token":"//')
echo "用户Token: $USER_TOKEN"

# 2. 获取顾客购物车
echo -e "\n========== 获取顾客购物车 =========="
curl -s -X GET http://localhost:8080/api/v1/cart \
  -H "customerToken: $USER_TOKEN" \
  -H "Accept: application/json" \
  -H "userType: 3" \
  -H "Content-Type: application/json" | json_pp

# 3. 商家登录
echo -e "\n========== 商家登录测试 =========="
MERCHANT_LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" \
  -H "userType: 1" \
  -d '{
    "username": "merchant1",
    "password": "123456",
    "identity": "MERCHANT"
  }')

echo $MERCHANT_LOGIN_RESPONSE | json_pp

# 提取商家token
MERCHANT_TOKEN=$(echo $MERCHANT_LOGIN_RESPONSE | grep -o '"token":"[^"]*' | sed 's/"token":"//')
echo "商家Token: $MERCHANT_TOKEN"

# 4. 获取商家订单列表
echo -e "\n========== 获取商家订单列表 =========="
curl -s -X GET http://localhost:8080/api/v1/merchants/orders \
  -H "merchantToken: $MERCHANT_TOKEN" \
  -H "Accept: application/json" \
  -H "userType: 1" \
  -H "Content-Type: application/json" | json_pp

# 5. 员工登录
echo -e "\n========== 员工登录测试 =========="
EMPLOYEE_LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" \
  -H "userType: 2" \
  -d '{
    "username": "employee1",
    "password": "123456",
    "identity": "EMPLOYEE"
  }')

echo $EMPLOYEE_LOGIN_RESPONSE | json_pp

# 提取员工token
EMPLOYEE_TOKEN=$(echo $EMPLOYEE_LOGIN_RESPONSE | grep -o '"token":"[^"]*' | sed 's/"token":"//')
echo "员工Token: $EMPLOYEE_TOKEN"

# 6. 获取员工订单视图
echo -e "\n========== 获取员工订单视图 =========="
curl -s -X GET http://localhost:8080/api/v1/orders/overview \
  -H "employeeToken: $EMPLOYEE_TOKEN" \
  -H "Accept: application/json" \
  -H "userType: 2" \
  -H "Content-Type: application/json" | json_pp
```

## 结论

本测试指南提供了使用 curl 命令行工具测试美君外卖系统 API 的详细方法，特别是针对不同用户类型的登录和授权流程。通过这些测试，您可以验证系统的核心功能是否正常工作。

请注意，所有请求都必须包含正确的 userType 和对应的 token 请求头，否则将无法通过服务器的认证。登录请求必须包含正确的 `identity` 字段，否则会导致服务器错误。 