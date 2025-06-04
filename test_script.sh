#!/bin/bash

# 美君外卖系统 API 测试脚本
# 此脚本用于测试系统的核心API功能

# 彩色输出函数
green() {
  echo -e "\033[32m$1\033[0m"
}

yellow() {
  echo -e "\033[33m$1\033[0m"
}

red() {
  echo -e "\033[31m$1\033[0m"
}

# 1. 顾客登录测试
green "========== 顾客登录测试 =========="
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
yellow "用户Token: $USER_TOKEN"

# 2. 获取顾客购物车
green "\n========== 获取顾客购物车 =========="
curl -s -X GET http://localhost:8080/api/v1/cart \
  -H "customerToken: $USER_TOKEN" \
  -H "Accept: application/json" \
  -H "userType: 3" \
  -H "Content-Type: application/json" | json_pp

# 3. 商家登录
green "\n========== 商家登录测试 =========="
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
yellow "商家Token: $MERCHANT_TOKEN"

# 4. 获取商家订单列表
green "\n========== 获取商家订单列表 =========="
curl -s -X GET http://localhost:8080/api/v1/merchants/orders \
  -H "merchantToken: $MERCHANT_TOKEN" \
  -H "Accept: application/json" \
  -H "userType: 1" \
  -H "Content-Type: application/json" | json_pp

# 5. 员工登录
green "\n========== 员工登录测试 =========="
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
yellow "员工Token: $EMPLOYEE_TOKEN"

# 6. 获取员工订单视图
green "\n========== 获取员工订单视图 =========="
curl -s -X GET http://localhost:8080/api/v1/orders/overview \
  -H "employeeToken: $EMPLOYEE_TOKEN" \
  -H "Accept: application/json" \
  -H "userType: 2" \
  -H "Content-Type: application/json" | json_pp

green "\n========== 测试完成 ==========" 