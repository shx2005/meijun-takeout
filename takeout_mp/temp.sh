#!/bin/bash

# 第一步：登录获取token
echo "========== 顾客登录测试 =========="
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" \
  -H "userType: 3" \
  -d '{
    "username": "17344402975",
    "password": "20050311",
    "identity": "CUSTOMER"
  }')

# 提取token
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | sed 's/"token":"//')
echo "获取到的Token: $TOKEN"

# 指定要查询的订单ID (这里假设订单ID为1，请根据实际情况修改)
ORDER_ID=1

# 第二步：获取特定订单详情
echo -e "\n========== 获取订单详情 =========="
curl -s -X GET "http://localhost:8080/api/v1/orders/${ORDER_ID}" \
  -H "customerToken: $TOKEN" \
  -H "Accept: application/json" \
  -H "userType: 3" \
  -H "Content-Type: application/json" | json_pp