#!/bin/bash

# 美君外卖系统 - 顾客登录测试脚本
# 此脚本根据前端实际实现调整请求格式，专注于测试顾客登录功能

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

green "========== 顾客登录测试 =========="
green "测试方法1: 使用正确的请求格式"

# 参考前端的登录实现来构建请求
USER_LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" \
  -H "userType: 3" \
  -d '{
    "username": "17344402975",
    "password": "20050311",
    "identity": "CUSTOMER"
  }')

echo "响应数据:"
echo $USER_LOGIN_RESPONSE | json_pp

# 测试可能的变体格式
green "\n========== 顾客登录测试 =========="
green "测试方法2: 使用phone字段替代username"

USER_LOGIN_RESPONSE_2=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" \
  -H "userType: 3" \
  -d '{
    "phone": "17344402975",
    "password": "20050311",
    "identity": "CUSTOMER"
  }')

echo "响应数据:"
echo $USER_LOGIN_RESPONSE_2 | json_pp

# 测试没有identity字段的情况
green "\n========== 顾客登录测试 =========="
green "测试方法3: 不包含identity字段"

USER_LOGIN_RESPONSE_3=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" \
  -H "userType: 3" \
  -d '{
    "username": "17344402975",
    "password": "20050311"
  }')

echo "响应数据:"
echo $USER_LOGIN_RESPONSE_3 | json_pp

# 使用debug/login_debug.js中的准确格式
green "\n========== 顾客登录测试 =========="
green "测试方法4: 使用debug/login_debug.js中的准确格式"

USER_LOGIN_RESPONSE_4=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "userType: 3" \
  -d '{
    "username": "17344402975",
    "password": "20050311",
    "identity": "CUSTOMER"
  }')

echo "响应数据:"
echo $USER_LOGIN_RESPONSE_4 | json_pp

# 测试直接使用登录接口不带userType
green "\n========== 顾客登录测试 =========="
green "测试方法5: 不带userType头"

USER_LOGIN_RESPONSE_5=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "17344402975",
    "password": "20050311",
    "identity": "CUSTOMER"
  }')

echo "响应数据:"
echo $USER_LOGIN_RESPONSE_5 | json_pp

green "\n========== 测试完成 ==========" 