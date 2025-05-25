#!/bin/bash

# 定义颜色
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 定义登录用户名 (手机号) - 请根据需要修改
USER_LOGIN_PHONE_NUMBER="17344402975"
USER_LOGIN_PASSWORD="20050311" # 请根据需要修改

# 定义JWT密钥 - 这应该与后端配置相匹配
CUSTOMER_SECRET_KEY="CUSTOMER-32-BYTE-LONG-SECRET-SECURE-KEY"

echo -e "${BLUE}=== 开始API测试 ===${NC}"

# 1. 登录测试
echo -e "\n${BLUE}1. 测试登录接口${NC}"
LOGIN_PAYLOAD=$(cat <<EOF
{
  "username": "$USER_LOGIN_PHONE_NUMBER",
  "password": "$USER_LOGIN_PASSWORD",
  "identity": "CUSTOMER"
}
EOF
)

LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "userType: 3" \
  -d "$LOGIN_PAYLOAD")

echo "登录响应:"
echo $LOGIN_RESPONSE | jq

# 提取原始JWT, 用户ID和用户名
ORIGINAL_JWT=$(echo $LOGIN_RESPONSE | jq -r '.data.token')
LOGGED_IN_USER_ID=$(echo $LOGIN_RESPONSE | jq -r '.data.id')
LOGGED_IN_USERNAME=$(echo $LOGIN_RESPONSE | jq -r '.data.username') # 通常是登录时用的手机号

echo -e "获取到原始JWT: $ORIGINAL_JWT"
echo -e "获取到用户ID: $LOGGED_IN_USER_ID"
echo -e "获取到用户名: $LOGGED_IN_USERNAME"

if [ -z "$ORIGINAL_JWT" ] || [ "$ORIGINAL_JWT" == "null" ]; then
  echo -e "${RED}登录失败，无法获取原始JWT${NC}"
  exit 1
fi

echo -e "${GREEN}登录成功！${NC}"

# 不需要Base64编码，直接使用原始JWT
TOKEN=$ORIGINAL_JWT

# 2. 获取菜品列表接口
echo -e "\n${BLUE}2. 测试获取菜品列表接口${NC}"
DISHES_RESPONSE=$(curl -s -X GET "http://localhost:8080/api/v1/dishes/page" \
  -H "customerToken: $TOKEN" \
  -H "customerSecretKey: $CUSTOMER_SECRET_KEY" \
  -H "userId: $LOGGED_IN_USER_ID" \
  -H "username: $LOGGED_IN_USERNAME" \
  -H "userType: 3" \
  -H "X-Requested-With: XMLHttpRequest" \
  -H "Accept: application/json")

echo "菜品列表响应:"
echo $DISHES_RESPONSE | jq
if [[ $(echo $DISHES_RESPONSE | jq -r '.code') == "200" ]]; then
  echo -e "${GREEN}获取菜品列表成功！${NC}"
else
  echo -e "${RED}获取菜品列表失败 (HTTP Status: $(echo $DISHES_RESPONSE | jq -r '.status // .code'), Message: $(echo $DISHES_RESPONSE | jq -r '.message // .msg'))${NC}"
fi

# 3. 测试获取用户信息接口
echo -e "\n${BLUE}3. 测试获取用户信息接口${NC}"
USER_INFO_RESPONSE=$(curl -s -X GET "http://localhost:8080/api/v1/user/info" \
  -H "customerToken: $TOKEN" \
  -H "customerSecretKey: $CUSTOMER_SECRET_KEY" \
  -H "userId: $LOGGED_IN_USER_ID" \
  -H "username: $LOGGED_IN_USERNAME" \
  -H "userType: 3" \
  -H "X-Requested-With: XMLHttpRequest" \
  -H "Accept: application/json")

echo "用户信息响应:"
echo $USER_INFO_RESPONSE | jq
if [[ $(echo $USER_INFO_RESPONSE | jq -r '.code') == "200" ]]; then
  echo -e "${GREEN}获取用户信息成功！${NC}"
else
  echo -e "${RED}获取用户信息失败 (HTTP Status: $(echo $USER_INFO_RESPONSE | jq -r '.status // .code'), Message: $(echo $USER_INFO_RESPONSE | jq -r '.message // .msg'))${NC}"
fi

# 4. 测试获取购物车接口
echo -e "\n${BLUE}4. 测试获取购物车接口${NC}"
CART_RESPONSE=$(curl -s -X GET "http://localhost:8080/api/v1/cart" \
  -H "customerToken: $TOKEN" \
  -H "customerSecretKey: $CUSTOMER_SECRET_KEY" \
  -H "userId: $LOGGED_IN_USER_ID" \
  -H "username: $LOGGED_IN_USERNAME" \
  -H "userType: 3" \
  -H "X-Requested-With: XMLHttpRequest" \
  -H "Accept: application/json")

echo "购物车响应:"
echo $CART_RESPONSE | jq
if [[ $(echo $CART_RESPONSE | jq -r '.code') == "200" ]]; then
  echo -e "${GREEN}获取购物车成功！${NC}"
else
  echo -e "${RED}获取购物车失败 (HTTP Status: $(echo $CART_RESPONSE | jq -r '.status // .code'), Message: $(echo $CART_RESPONSE | jq -r '.message // .msg'))${NC}"
fi

# 5. 测试添加商品到购物车
echo -e "\n${BLUE}5. 测试添加商品到购物车接口${NC}"
# 假设itemId=13是有效的，如果不是，请修改
ADD_TO_CART_PAYLOAD=$(cat <<EOF
{
  "itemId": 13,
  "quantity": 1,
  "userId": $LOGGED_IN_USER_ID, 
  "itemType": "DISH"
}
EOF
)

ADD_CART_RESPONSE=$(curl -s -X POST "http://localhost:8080/api/v1/cart/add" \
  -H "customerToken: $TOKEN" \
  -H "customerSecretKey: $CUSTOMER_SECRET_KEY" \
  -H "userId: $LOGGED_IN_USER_ID" \
  -H "username: $LOGGED_IN_USERNAME" \
  -H "userType: 3" \
  -H "X-Requested-With: XMLHttpRequest" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d "$ADD_TO_CART_PAYLOAD")

echo "添加购物车响应:"
echo $ADD_CART_RESPONSE | jq
if [[ $(echo $ADD_CART_RESPONSE | jq -r '.code') == "200" ]]; then
  echo -e "${GREEN}添加商品到购物车成功！${NC}"
else
  echo -e "${RED}添加商品到购物车失败 (HTTP Status: $(echo $ADD_CART_RESPONSE | jq -r '.status // .code'), Message: $(echo $ADD_CART_RESPONSE | jq -r '.message // .msg'))${NC}"
fi

# 6. 再次测试获取购物车接口验证添加成功
echo -e "\n${BLUE}6. 再次测试获取购物车接口验证添加成功${NC}"
CART_AFTER_ADD_RESPONSE=$(curl -s -X GET "http://localhost:8080/api/v1/cart" \
  -H "customerToken: $TOKEN" \
  -H "customerSecretKey: $CUSTOMER_SECRET_KEY" \
  -H "userId: $LOGGED_IN_USER_ID" \
  -H "username: $LOGGED_IN_USERNAME" \
  -H "userType: 3" \
  -H "X-Requested-With: XMLHttpRequest" \
  -H "Accept: application/json")

echo "购物车响应 (添加后):"
echo $CART_AFTER_ADD_RESPONSE | jq
if [[ $(echo $CART_AFTER_ADD_RESPONSE | jq -r '.code') == "200" ]]; then
  echo -e "${GREEN}获取购物车成功！${NC}"
else
  echo -e "${RED}获取购物车失败 (HTTP Status: $(echo $CART_AFTER_ADD_RESPONSE | jq -r '.status // .code'), Message: $(echo $CART_AFTER_ADD_RESPONSE | jq -r '.message // .msg'))${NC}"
fi

echo -e "\n${BLUE}=== API测试完成 ===${NC}" 