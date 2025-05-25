#!/bin/bash

# 定义颜色
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

LOGIN_USERNAME="17344402975"
LOGIN_PASSWORD="20050311"
LOGIN_IDENTITY="CUSTOMER"

echo -e "${BLUE}=== API测试: 使用动态生成的、结构符合API验证要求的Token ===${NC}"

# 1. 登录以获取基础信息 (id, username from response)
echo -e "\n${BLUE}1. 登录中...${NC}"
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "userType: 3" \
  -d "{\"username\":\"$LOGIN_USERNAME\",\"password\":\"$LOGIN_PASSWORD\",\"identity\":\"$LOGIN_IDENTITY\"}")

echo "登录响应:"
echo $LOGIN_RESPONSE | jq

USER_ID_FROM_LOGIN=$(echo $LOGIN_RESPONSE | jq -r '.data.id')
USERNAME_FROM_LOGIN=$(echo $LOGIN_RESPONSE | jq -r '.data.username')
# LOGIN_IDENTITY is already defined above

if [ -z "$USER_ID_FROM_LOGIN" ] || [ "$USER_ID_FROM_LOGIN" == "null" ] || [ -z "$USERNAME_FROM_LOGIN" ] || [ "$USERNAME_FROM_LOGIN" == "null" ]; then
  echo -e "${RED}登录失败或未能从响应中提取id/username。无法继续。${NC}"
  exit 1
fi

echo -e "从登录响应中提取: UserID='$USER_ID_FROM_LOGIN', Username='$USERNAME_FROM_LOGIN', Identity='$LOGIN_IDENTITY'${NC}"

# 2. 使用提取的信息生成新的JWT token (包含id, username, identity)
echo -e "\n${BLUE}2. 生成符合API验证结构的新Token...${NC}"
GENERATED_API_TOKEN=$(node generate_token.js "$USER_ID_FROM_LOGIN" "$USERNAME_FROM_LOGIN" "$LOGIN_IDENTITY")

if [ -z "$GENERATED_API_TOKEN" ]; then
  echo -e "${RED}通过node generate_token.js生成Token失败。${NC}"
  exit 1
fi
echo -e "生成的新Token: $GENERATED_API_TOKEN"
echo -e "${RED}重要提示: 此Token使用假设的密钥 ('meijun-takeout-secret') 签名。如果与后端密钥不匹配，API调用仍会失败。${NC}"

# 3. 使用新生成的Token测试API (以获取用户信息为例)
# 尝试使用 'token' 和 'Authorization: Bearer' 请求头

TOKEN_HEADERS_TO_TRY=(
  "token: $GENERATED_API_TOKEN"
  "Authorization: Bearer $GENERATED_API_TOKEN"
)

WORKING_HEADER=""

echo -e "\n${BLUE}3. 使用新生成的Token测试获取用户信息接口...${NC}"
for header_format in "${TOKEN_HEADERS_TO_TRY[@]}"; do
  echo -e "\n${BLUE}尝试请求头: $header_format${NC}"
  USER_INFO_RESPONSE=$(curl -s -X GET http://localhost:8080/api/v1/user/info \
    -H "$header_format" \
    -H "Accept: application/json" \
    -H "userType: 3")
  
  echo "获取用户信息响应:"
  echo $USER_INFO_RESPONSE | jq

  # 简单检查响应是否成功 (jq -r '.code' 存在且为200)
  # 注意: 某些失败响应可能没有 .code 字段，或为其他HTTP状态码
  response_code=$(echo $USER_INFO_RESPONSE | jq -r '.code // empty')
  http_status=$(echo $USER_INFO_RESPONSE | jq -r '.status // empty') # 有些错误用status

  if [ "$response_code" == "200" ]; then
    echo -e "${GREEN}成功! 使用请求头 '$header_format' 获取用户信息成功。${NC}"
    WORKING_HEADER="$header_format"
    break
  elif [ "$http_status" != "" ] && [ "$http_status" != "401" ] && [ "$http_status" != "403" ]; then 
    echo -e "${GREEN}收到非401/403的HTTP状态 $http_status。可能成功或有其他问题。进一步检查。${NC}"
    WORKING_HEADER="$header_format" # 假设为工作状态，供后续检查
    break
  else
    echo -e "${RED}使用请求头 '$header_format' 获取用户信息失败。错误码: ${response_code:-N/A}, HTTP状态: ${http_status:-N/A}${NC}"
  fi
done

if [ -z "$WORKING_HEADER" ]; then
  echo -e "\n${RED}所有尝试的请求头格式均未能成功获取用户信息。${NC}"
  echo -e "${RED}请再次确认:
    1. generate_token.js 中的 secretKey ('meijun-takeout-secret') 是否与后端完全一致。
    2. 后端API是否确实期望token的payload包含 id, username, identity。
    3. 请求头名称 ('token' 或 'Authorization: Bearer') 是否正确。${NC}"
  exit 1
fi

echo -e "\n${GREEN}找到可能有效的请求头: $WORKING_HEADER${NC}"

# 4. 使用有效的请求头测试其他接口 (例如，获取购物车)
echo -e "\n${BLUE}4. 使用有效的请求头 ('$WORKING_HEADER') 测试获取购物车接口...${NC}"
CART_RESPONSE=$(curl -s -X GET http://localhost:8080/api/v1/cart \
  -H "$WORKING_HEADER" \
  -H "Accept: application/json" \
  -H "userType: 3")

echo "获取购物车响应:"
echo $CART_RESPONSE | jq

# 5. 测试添加商品到购物车
echo -e "\n${BLUE}5. 测试添加商品到购物车接口${NC}"
ADD_TO_CART_PAYLOAD="{\"itemId\":13,\"quantity\":1,\"userId\":$USER_ID_FROM_LOGIN,\"itemType\":\"DISH\"}"
echo "发送的添加购物车Payload: $ADD_TO_CART_PAYLOAD"

ADD_RESULT=$(curl -s -X POST "http://localhost:8080/api/v1/cart/add" \
  -H "$WORKING_HEADER" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "userType: 3" \
  -d "$ADD_TO_CART_PAYLOAD")

echo "添加购物车响应:"
echo $ADD_RESULT | jq

echo -e "\n${BLUE}=== API测试完成 ===${NC}" 