#!/bin/bash

# 定义颜色
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 从Node.js脚本生成的token
GENERATED_TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjdXN0b21lcklEIjozLCJleHAiOjE3NDgyNDkyNTIsImlhdCI6MTc0ODE2Mjg1Mn0.VylE0z8k4ZHXnxXKPcc6To818l5eX0vAdHJcPreR9wk"

echo -e "${BLUE}=== 使用生成的兼容token测试API ===${NC}"

# 1. 登录获取用户ID
echo -e "\n${BLUE}1. 登录获取用户ID${NC}"
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "userType: 3" \
  -d '{"username":"17344402975","password":"20050311","identity":"CUSTOMER"}')

USER_ID=$(echo $LOGIN_RESPONSE | jq -r '.data.id')
echo -e "获取到UserId: $USER_ID"

# 定义不同的token头格式
TOKEN_HEADERS=(
  "token: $GENERATED_TOKEN"
  "Authorization: Bearer $GENERATED_TOKEN"
  "Token: $GENERATED_TOKEN"
  "Authentication: Bearer $GENERATED_TOKEN"
)

# 2. 测试获取用户信息 - 尝试不同token头格式
echo -e "\n${BLUE}2. 测试获取用户信息 - 尝试不同token头格式${NC}"

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
  echo -e "${RED}可能原因:${NC}"
  echo -e "1. token使用的secret与后端不一致"
  echo -e "2. token的格式正确但请求头格式不对"
  echo -e "3. 权限验证逻辑可能使用了其他字段或有特殊要求"
fi

echo -e "\n${BLUE}=== API测试完成 ===${NC}" 