# 美君外卖系统 API 测试指南

本文档提供了如何使用 curl 命令行工具测试美君外卖系统 API 的详细说明。以下示例将帮助测试人员了解如何获取登录令牌，以及如何使用令牌进行 API 调用测试。

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

- **商家账户**: 
  - 用户名: merchant1
  - 密码: 123456

## 1. 用户登录测试

### 请求示例

```bash
curl -X POST http://localhost:8080/api/customer/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "17344402975",
    "password": "20050311"
  }' | json_pp
```

### 预期响应

```json
{
  "code": 0,
  "msg": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "username": "17344402975",
    "name": "shx",
    "avatar": "https://thispersondoesnotexist.com/image"
  }
}
```

### 保存Token

请将响应中的 token 值保存下来，用于后续 API 请求的认证。

```bash
# 将token保存到环境变量
export CUSTOMER_TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## 2. 获取用户订单列表

### 请求示例

```bash
curl -X GET http://localhost:8080/api/orders \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
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
      "total": 88.00,
      "status": "completed",
      "createTime": "2025-05-27 13:30:25",
      "dishes": [
        {
          "name": "鱼香肉丝",
          "quantity": 2,
          "price": 28.00
        },
        {
          "name": "红烧排骨",
          "quantity": 1,
          "price": 32.00
        }
      ]
    },
    {
      "id": 2,
      "total": 76.00,
      "status": "pending",
      "createTime": "2025-05-26 18:05:16",
      "dishes": [
        {
          "name": "宫保鸡丁",
          "quantity": 2,
          "price": 26.00
        },
        {
          "name": "麻婆豆腐",
          "quantity": 1,
          "price": 22.00
        },
        {
          "name": "干锅土豆片",
          "quantity": 1,
          "price": 28.00
        }
      ]
    },
    {
      "id": 1,
      "total": 114.00,
      "status": "completed",
      "createTime": "2025-05-24 18:05:03",
      "dishes": [
        {
          "name": "鱼香肉丝",
          "quantity": 2,
          "price": 28.00
        },
        {
          "name": "宫保鸡丁",
          "quantity": 1,
          "price": 26.00
        },
        {
          "name": "红烧排骨",
          "quantity": 1,
          "price": 32.00
        }
      ]
    }
  ]
}
```

## 3. 获取特定订单详情

### 请求示例

```bash
curl -X GET http://localhost:8080/api/orders/1 \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -H "Content-Type: application/json" | json_pp
```

### 预期响应

```json
{
  "code": 0,
  "msg": "获取订单详情成功",
  "data": {
    "id": 1,
    "customerId": 1,
    "merchantId": 1,
    "total": 114.00,
    "status": "completed",
    "createTime": "2025-05-24 18:05:03",
    "updateTime": "2025-05-24 18:05:03",
    "orderDetails": [
      {
        "id": 1,
        "dishId": 1,
        "dishName": "鱼香肉丝",
        "quantity": 2,
        "price": 28.00,
        "imageUrl": "/static/images/dish1.jpg"
      },
      {
        "id": 2,
        "dishId": 2,
        "dishName": "宫保鸡丁",
        "quantity": 1,
        "price": 26.00,
        "imageUrl": "/static/images/dish2.jpg"
      },
      {
        "id": 3,
        "dishId": 3,
        "dishName": "红烧排骨",
        "quantity": 1,
        "price": 32.00,
        "imageUrl": "/static/images/dish3.jpg"
      }
    ]
  }
}
```

## 4. 商家登录测试

### 请求示例

```bash
curl -X POST http://localhost:8080/api/merchant/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "merchant1",
    "password": "123456"
  }' | json_pp
```

### 预期响应

```json
{
  "code": 0,
  "msg": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "merchantId": 1,
    "username": "merchant1"
  }
}
```

### 保存商家Token

```bash
# 将商家token保存到环境变量
export MERCHANT_TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## 5. 获取商家订单列表

### 请求示例

```bash
curl -X GET http://localhost:8080/api/merchant/orders \
  -H "Authorization: Bearer $MERCHANT_TOKEN" \
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
      "customerId": 1,
      "customerName": "shx",
      "total": 88.00,
      "status": "completed",
      "createTime": "2025-05-27 13:30:25"
    },
    {
      "id": 2,
      "customerId": 1,
      "customerName": "shx",
      "total": 76.00,
      "status": "pending",
      "createTime": "2025-05-26 18:05:16"
    },
    {
      "id": 1,
      "customerId": 1,
      "customerName": "shx",
      "total": 114.00,
      "status": "completed",
      "createTime": "2025-05-24 18:05:03"
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
- Authorization header 是否格式正确 (应为 `Bearer <token>`)
- 是否使用了正确的 token（客户 token 不能用于商家 API，反之亦然）

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
- JSON 请求体是否格式正确
- 是否提供了所有必需的字段

## 测试完整流程

以下是一个完整测试流程的 shell 脚本示例：

```bash
#!/bin/bash

# 1. 用户登录
echo "========== 用户登录测试 =========="
USER_LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/customer/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "17344402975",
    "password": "20050311"
  }')

echo $USER_LOGIN_RESPONSE | json_pp

# 提取token
USER_TOKEN=$(echo $USER_LOGIN_RESPONSE | grep -o '"token":"[^"]*' | sed 's/"token":"//')
echo "用户Token: $USER_TOKEN"

# 2. 获取用户订单列表
echo -e "\n========== 获取用户订单列表 =========="
curl -s -X GET http://localhost:8080/api/orders \
  -H "Authorization: Bearer $USER_TOKEN" \
  -H "Content-Type: application/json" | json_pp

# 3. 商家登录
echo -e "\n========== 商家登录测试 =========="
MERCHANT_LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/merchant/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "merchant1",
    "password": "123456"
  }')

echo $MERCHANT_LOGIN_RESPONSE | json_pp

# 提取商家token
MERCHANT_TOKEN=$(echo $MERCHANT_LOGIN_RESPONSE | grep -o '"token":"[^"]*' | sed 's/"token":"//')
echo "商家Token: $MERCHANT_TOKEN"

# 4. 获取商家订单列表
echo -e "\n========== 获取商家订单列表 =========="
curl -s -X GET http://localhost:8080/api/merchant/orders \
  -H "Authorization: Bearer $MERCHANT_TOKEN" \
  -H "Content-Type: application/json" | json_pp
```

## 结论

本测试指南提供了使用 curl 命令行工具测试美君外卖系统 API 的基本方法。通过这些测试，您可以验证系统的核心功能是否正常工作。

如需更多帮助或遇到其他问题，请联系开发团队。 