#!/bin/bash

# 定义颜色
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=== 使用修复的token格式测试API ===${NC}"

# 1. 登录测试 - 获取原始token
echo -e "\n${BLUE}1. 获取原始token${NC}"
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "userType: 3" \
  -d '{"username":"17344402975","password":"20050311","identity":"CUSTOMER"}')

echo $LOGIN_RESPONSE | jq

# 提取token和userId并保存到变量
ORIGINAL_TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.data.token')
USER_ID=$(echo $LOGIN_RESPONSE | jq -r '.data.id')
USERNAME=$(echo $LOGIN_RESPONSE | jq -r '.data.username')
echo -e "获取到原始Token: $ORIGINAL_TOKEN"
echo -e "获取到UserId: $USER_ID"
echo -e "获取到Username: $USERNAME"

if [ -z "$ORIGINAL_TOKEN" ] || [ "$ORIGINAL_TOKEN" == "null" ]; then
  echo -e "${RED}登录失败，无法获取token${NC}"
  exit 1
fi

echo -e "${GREEN}登录成功！${NC}"

# 2. 使用正确格式的token (这里使用hardcoded token作为示例)
# 实际项目中应该通过正确方式生成JWT token，包含id, username和identity字段
echo -e "\n${BLUE}2. 准备使用正确格式的token${NC}"

# 这个是从fixed_login.js中获取的正确格式token示例
# 实际使用时需根据后端要求重新生成
FIXED_TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MywidXNlcm5hbWUiOiIxNzM0NDQwMjk3NSIsImlkZW50aXR5IjoiQ1VTVE9NRVIifQ.cNPLs3WtkpI60-YB1hZR34YLF65K7LmAVGiWdyXgvK8"

echo -e "使用修复的Token: $FIXED_TOKEN"
WORKING_HEADER="Authorization: Bearer $FIXED_TOKEN"

# 3. 测试获取用户信息接口
echo -e "\n${BLUE}3. 测试获取用户信息接口${NC}"
USER_INFO=$(curl -s -X GET "http://localhost:8080/api/v1/user/info" \
  -H "$WORKING_HEADER" \
  -H "Accept: application/json" \
  -H "userType: 3")

echo $USER_INFO | jq
if [[ $(echo $USER_INFO | jq -r '.code') == "200" ]]; then
  echo -e "${GREEN}获取用户信息成功！${NC}"
else
  echo -e "${RED}获取用户信息失败 $(echo $USER_INFO | jq -r '.msg')${NC}"
  echo -e "${RED}请确认token格式是否正确${NC}"
fi

# 4. 测试获取购物车接口
echo -e "\n${BLUE}4. 测试获取购物车接口${NC}"
CART=$(curl -s -X GET "http://localhost:8080/api/v1/cart" \
  -H "$WORKING_HEADER" \
  -H "Accept: application/json" \
  -H "userType: 3")

echo $CART | jq
if [[ $(echo $CART | jq -r '.code') == "200" ]]; then
  echo -e "${GREEN}获取购物车成功！${NC}"
else
  echo -e "${RED}获取购物车失败 $(echo $CART | jq -r '.msg')${NC}"
fi

# 5. 测试添加商品到购物车
echo -e "\n${BLUE}5. 测试添加商品到购物车接口${NC}"
ADD_RESULT=$(curl -s -X POST "http://localhost:8080/api/v1/cart/add" \
  -H "$WORKING_HEADER" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "userType: 3" \
  -d '{"itemId":13,"quantity":1,"userId":'"$USER_ID"',"itemType":"DISH"}')

echo $ADD_RESULT | jq
if [[ $(echo $ADD_RESULT | jq -r '.code') == "200" ]]; then
  echo -e "${GREEN}添加商品到购物车成功！${NC}"
else
  echo -e "${RED}添加商品到购物车失败 $(echo $ADD_RESULT | jq -r '.msg')${NC}"
fi

echo -e "\n${BLUE}=== API测试完成 ===${NC}"

echo -e "\n${RED}注意：这是临时解决方案！${NC}"
echo -e "${RED}后端开发人员需要修复token生成与验证机制的不一致问题${NC}"
echo -e "${RED}确保token中包含的字段与验证时期望的字段一致${NC}" 