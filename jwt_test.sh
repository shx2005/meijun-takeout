#!/bin/bash

# 定义颜色
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=== 使用后端兼容的token格式测试API ===${NC}"

# 1. 登录测试 - 获取原始token
echo -e "\n${BLUE}1. 获取原始token${NC}"
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "userType: 3" \
  -d '{"username":"17344402975","password":"20050311","identity":"CUSTOMER"}')

echo $LOGIN_RESPONSE | jq

# 提取token和信息
ORIGINAL_TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.data.token')
USER_ID=$(echo $LOGIN_RESPONSE | jq -r '.data.id')

echo -e "获取到原始Token: $ORIGINAL_TOKEN"
echo -e "获取到UserId: $USER_ID"

if [ -z "$ORIGINAL_TOKEN" ] || [ "$ORIGINAL_TOKEN" == "null" ]; then
  echo -e "${RED}登录失败，无法获取token${NC}"
  exit 1
fi

echo -e "${GREEN}登录成功！${NC}"

# 2. 测试不同的token头格式
echo -e "\n${BLUE}2. 测试获取用户信息 - 尝试不同token头格式${NC}"

# 定义不同的token头格式
TOKEN_HEADERS=(
  "token: $ORIGINAL_TOKEN"
  "Authorization: Bearer $ORIGINAL_TOKEN"
  "Token: $ORIGINAL_TOKEN"
  "Authentication: Bearer $ORIGINAL_TOKEN"
  "Authentication: $ORIGINAL_TOKEN"
  "tokenName: $ORIGINAL_TOKEN"
)

# 测试每一种token头格式
for header in "${TOKEN_HEADERS[@]}"; do
  echo -e "\n${BLUE}尝试: $header${NC}"
  USER_INFO=$(curl -s -X GET "http://localhost:8080/api/v1/user/info" \
    -H "$header" \
    -H "Accept: application/json" \
    -H "userType: 3")
  
  echo $USER_INFO | jq
  
  # 检查是否成功
  if [[ $(echo $USER_INFO | jq -r '.code' 2>/dev/null) == "200" ]]; then
    echo -e "${GREEN}成功! 使用 '$header'${NC}"
    WORKING_HEADER="$header"
    break
  else
    echo -e "${RED}失败${NC}"
  fi
done

# 如果找到了有效的token头格式
if [ -n "$WORKING_HEADER" ]; then
  echo -e "\n${GREEN}找到有效的token头格式: $WORKING_HEADER${NC}"
  
  # 3. 测试获取购物车接口
  echo -e "\n${BLUE}3. 测试获取购物车接口${NC}"
  CART=$(curl -s -X GET "http://localhost:8080/api/v1/cart" \
    -H "$WORKING_HEADER" \
    -H "Accept: application/json" \
    -H "userType: 3")

  echo $CART | jq
  if [[ $(echo $CART | jq -r '.code' 2>/dev/null) == "200" ]]; then
    echo -e "${GREEN}获取购物车成功！${NC}"
  else
    echo -e "${RED}获取购物车失败${NC}"
  fi

  # 4. 测试添加商品到购物车
  echo -e "\n${BLUE}4. 测试添加商品到购物车接口${NC}"
  ADD_RESULT=$(curl -s -X POST "http://localhost:8080/api/v1/cart/add" \
    -H "$WORKING_HEADER" \
    -H "Content-Type: application/json" \
    -H "Accept: application/json" \
    -H "userType: 3" \
    -d '{"itemId":13,"quantity":1,"userId":'"$USER_ID"',"itemType":"DISH"}')

  echo $ADD_RESULT | jq
  if [[ $(echo $ADD_RESULT | jq -r '.code' 2>/dev/null) == "200" ]]; then
    echo -e "${GREEN}添加商品到购物车成功！${NC}"
  else
    echo -e "${RED}添加商品到购物车失败${NC}"
  fi
else
  echo -e "\n${RED}无法找到有效的token头格式${NC}"
fi

echo -e "\n${BLUE}=== API测试完成 ===${NC}" 