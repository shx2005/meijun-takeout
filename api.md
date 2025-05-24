<<<<<<< HEAD
# 注意  

如果数据在body中，使用DTO进行封装, 使用@RequestBody  
如果是PathVariable,  使用@PathVariable  
controller层返回Result\<T>(Result已经封装好了)  

---

前端请求头记得带上```userType```的header(方便起见用0-3，分别是admin,merchant,employee,customer).  
还有```tokenName```的header，这是登录时返回的token，用于后续请求。(开发时如果嫌太麻烦的话，可以把注册的interceptor删掉)(tokenName根据用户类型获取，配置文件里有)  
java数据类型和json有区别，这里用的java数据类型  
很多情况下body并不需要携带userId，因为登陆后id会存在缓存里。(userId可以是admin,employee,customer,merchant，视情况而定)

### 余额支付接口  

1. **确认支付**  
接口  
**post** /api/v1/payment/balance  

request

```json
{
  "orderId" : "orderId",
  "amount" : "amount",
  "payType" : "payType"
}
```

| 名字      | 类型         | 描述             | 可选 |
|---------|------------|----------------|----|
| orderId | Long       | 订单id           | 否  |
| amount  | BigDecimal | 订单金额           | 否  |
| payType | String     | 支付方式（用enum也可以） | 否  |

response

```json
{
  "code" : "code",
  "message" : "message",
  "data" : {
    "balance" : "balance",
    "orderId" : "orderId",
    "payType" : "payType"
  }
}

```

| 名字      | 类型      | 描述             | 可选 |
|---------|---------|----------------|----|
| code    | Integer | 状态码            | 否  |
| message | String  | 状态信息           | 否  |
| orderId | Long    | 订单id           | 否  |
| balance | String  | 用户余额           | 否  |
| payType | String  | 支付方式（用enum也可以） | 否  |

code和message在Result中存在，这里就不再赘述，后续从略

2. **显示余额**  
接口  
**get** /api/v1/user/balance  
request

```json

```

注：默认已经登陆不需要额外信息,当然,可以带上userId
response  

```json
{
  "code" : "code",
  "message" : "message",
  "data" : {
    "balance" : "balance"
  }
}

```

| 名字      | 类型         | 描述             | 可选 |
|---------|------------|----------------|---|
| balance | BigDecimal | 用户余额           | 否 |

### 优惠券核销接口

1. **优惠券校验**
post /api/v1/coupons/validate  
request  

```json
{
  "couponId": "couponId",
  "orderId": "orderId",
  "amount": "amount",
  "payType": "payType"
}
```

|名字 | 类型         | 描述             | 可选 |
|----|------------|----------------|---|
|couponId | Long       | 优惠券id           | 否 |
|orderId | Long       | 订单id           | 否 |
|amount | BigDecimal | 订单金额           | 否 |
|payType | String     | 支付方式（用enum也可以） | 否 |

response

```json
{
  "code" : "code",
  "message" : "message",
  "data" : {
    "amount" : "amount",
    "orderId" : "orderId",
    "payType" : "payType"
  }
}

```

| 名字      | 类型         | 描述             | 可选 |
|---------|------------|----------------|---|
| amount | BigDecimal | 订单金额           | 否 |
| orderId | Long       | 订单id           | 否 |
| payType | String     | 支付方式（用enum也可以） | 否 |

2. **满减计算接口**  
post /api/v1/coupons/calculate  
request  

```json
{
  "couponId": "couponId",
  "orderId": "orderId",
  "amount": "amount",
  "payType": "payType"
}
```

|名字 | 类型         | 描述             | 可选 |
|----|------------|----------------|---|
|couponId | Long       | 优惠券id           | 否 |
|orderId | Long       | 订单id           | 否 |
|amount | BigDecimal | 订单金额           | 否 |
|payType | String     | 支付方式（用enum也可以） | 否 |

response

```json
{
  "code" : "code",
  "message" : "message",
  "data" : {
    "amount" : "amount"
  }
}
```

| 名字      | 类型         | 描述             | 可选 |
|---------|------------|----------------|---|
| amount | BigDecimal | 订单金额           | 否 |

### 支付异常处理接口

1. **支付异常处理**  
post api/v1/payment/error  
request

```json
{
  "orderId" : "orderId",
  "userId" : "userId",
  "payType" : "payType"
}
```

|名字 | 类型         | 描述             | 可选 |
|----|------------|----------------|---|
|orderId | Long       | 订单id           | 否 |
|userId | Long       | 用户id           | 否 |
|payType | String     | 支付方式（用enum也可以） | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "retryToken": "token",
    "redirectUrl": "url",
    "orderId": "orderId",
    "payType": "payType"
  }
}
```

| 名字      | 类型      | 描述             | 可选 |
|---------|---------|----------------|---|
| retryToken | String   | 重试token           | 否 |
| redirectUrl | String   | 重试url           | 否 |
| orderId | Long    | 订单id           | 否 |
| payType | String  | 支付方式（用enum也可以） | 否 |

### 客服沟通接口(用户端)

1. **发送信息**  
post /api/v1/support/customer/messages  
request  

```json
{
  "userId": "userId",
  "employeeId": "employeeId",
  "content": "content",
  "orderId": "orderId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |
|employeeId | Long       | 员工id  | 否 |
|content | String     | 发送地信息 | 否 |
|orderId | Long       | 订单id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "messageId": "messageId",
    "status": "status"
  }
}
```

2. **获取客服信息**  
get /api/v1/support/customer/info  
request

```json
{
  "userId": "userId",
  "employeeId": "employeeId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |
|employeeId | Long       | 员工id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "employeeId": "employeeId",
    "employeeName": "employeeName",
    "employeeAvatar": "employeeAvatar",
    "employeePhone": "employeePhone"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|employeeId | Long       | 员工id  | 否 |
|employeeName | String     | 员工名称 | 否 |
|employeeAvatar | String     | 员工头像 | 否 |
|employeePhone | String     | 员工电话 | 否 |

3. **撤回信息**  
delete /api/v1/support/customer/messages/{messageId}  
request

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|messageId | Long       | 信息id  | 否 |
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "messageId": "messageId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|messageId | Long       | 信息id  | 否 |
|status | String     | 操作状态  | 否 |  

### 操作售后请求  

post /api/v1/after-sale/{requestId}/actions  
request

```json
{
  "userId": "userId",
  "action": "action"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|requestId | Long       | 请求id  | 否 |
|userId | Long       | 用户id  | 否 |
|action | String     | 操作  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "requestId": "requestId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|requestId | Long       | 请求id  | 否 |
|status | String     | 操作状态  | 否 |

### 凭证上传接口  

post /api/v1/after-sale/{requestId}/upload
request

```json
{
  "userId": "userId",
  "content": "content",
  "file": "file"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|requestId | Long       | 请求id  | 否 |
|userId | Long       | 用户id  | 否 |
|content | String     | 凭证描述  | 否 |
|file | MultipartFile     | 文件  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "requestId": "requestId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|requestId| Long       | 请求id  | 否 |
|status | String     | 操作状态  | 否 |

### 获取售后进度

get /api/v1/after-sale/{requestId}/status  
request

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|requestId | Long       | 请求id  | 否 |
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "requestId": "requestId",
    "status": "status"
  }
}
```

### 获取售后反馈  

get /api/v1/after-sale/{requestId}/feedback
request

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|requestId | Long       | 售后请求id  | 否 |
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "requestId": "requestId",
    "feedback": "feedback",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|requestId | Long       | 售后请求id  | 否 |
|feedback | String     | 反馈  | 否 |
|status | String     | 操作状态  | 否 |

### 客服沟通接口(客服端)

1. **发送信息**  
post /api/v1/support/employee/messages  
request  

```json
{
  "userId": "userId",
  "employeeId": "employeeId",
  "content": "content",
  "orderId": "orderId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |
|employeeId | Long       | 员工id  | 否 |
|content | String     | 发送地信息 | 否 |
|orderId | Long       | 订单id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "messageId": "messageId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|messageId | Long       | 信息id  | 否 |
|status | String     | 操作状态  | 否 |

2. **获取顾客信息**  
get /api/v1/support/employee/info  
request

```json
{
  "userId": "userId",
  "customerId": "customerId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |
|customerId | Long       | 客户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "customerId": "customerId",
    "customerName": "customerName",
    "customerAvatar": "customerAvatar"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|customerId | Long       | 客户id  | 否 |
|customerName | String     | 客户名称 | 否 |
|customerAvatar | String     | 客户头像 | 否 |

3. **撤回信息**  
delete /api/v1/support/employee/messages/{messageId}  
request

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|messageId | Long       | 信息id  | 否 |
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "messageId": "messageId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|messageId | Long       | 信息id  | 否 |
|status | String     | 操作状态  | 否 |  

### 查看售后请求

1. **获取售后请求列表**  
get api/v1/merchants/after-sale  
request

```json
{
  "userId": "userId"
}
```

| 名字     | 类型   | 描述   | 可选 |
|--------|------|------|----|
| userId | Long | 用户id | 否  |

response

```json
{
  "code": "code",
  "message": "message",
  "data": [
    {
      "afterSaleId": "afterSaleId",
      "orderId": "orderId",
      "customerId": "customerId",
      "type": "type",
      "status": "status",
      "content": "content",
      "createTime": "createTime",
      "updateTime": "updateTime"
    }
  ]
}
```

|  名字     | 类型   | 描述   | 可选 |
|---------|------|------|----|
| afterSaleId | Long | 售后id | 否  |
| orderId | Long | 订单id | 否  |
| customerId | Long | 客户id | 否  |
| type | String | 类型   | 否  |
| status | String | 状态   | 否  |
| content | String | 内容   | 否  |
| createTime | String | 创建时间 | 否  |
| updateTime | String | 更新时间 | 否  |

2. **同意请求**  
post api/v1/merchants/after-sale/{requestId}/approve  
request

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|requestId | Long       | 售后请求id  | 否 |
|userId | Long       | 商家id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "requestId": "requestId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|requestId | Long       | 售后请求id  | 否 |
|status | String     | 操作状态  | 否 |

3. **拒绝请求**  
post api/v1/merchants/after-sale/{requestId}/reject  
request

```json
{
  "userId": "userId",
  "reason": "reason"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|requestId | Long       | 售后请求id  | 否 |
|userId | Long       | 商家id  | 否 |
|reason | String     | 拒绝理由  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "requestId": "requestId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|requestId | Long       | 售后请求id  | 否 |
|status | String     | 操作状态  | 否 |

4. **提供处理反馈信息**  
post api/v1/merchants/after-sale/{requestId}/feedback  
request

```json
{
  "userId": "userId",
  "feedback": "feedback"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|requestId | Long       | 售后请求id  | 否 |
|userId | Long       | 商家id  | 否 |
|feedback | String     | 反馈信息  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "requestId": "requestId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|requestId | Long       | 售后请求id  | 否 |
|status | String     | 操作状态  | 否 |

### 优惠券接口(商家端)

1. **获取优惠券列表**  
get api/v1/merchants/coupons  
request  

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": [
    { 
      "couponId": "couponId",
      "name": "name",
      "type": "type",
      "description": "description",
      "amount": "amount",
      "startTime": "startTime",
      "endTime": "endTime",
      "status": "status",
      "createTime": "createTime",
      "updateTime": "updateTime"
    }
  ]
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|couponId | Long       | 优惠券id  | 否 |
|name | String     | 优惠券名称  | 否 |
|type | String     | 优惠券类型  | 否 |
|description | String     | 优惠券描述  | 否 |
|amount | String     | 优惠券面值  | 否 |
|startTime | String     | 优惠券开始时间  | 否 |
|endTime | String     | 优惠券结束时间  | 否 |
|status | String     | 优惠券状态  | 否 |

2. **增加优惠券**  
post api/v1/merchants/coupons  
request

```json
{
  "userId": "userId",
  "couponName": "couponName",
  "couponType": "couponType",
  "description": "description",
  "amount": "amount",
  "startTime": "startTime",
  "endTime": "endTime"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |
|couponName | String     | 优惠券名称  | 否 |
|couponType | String     | 优惠券类型  | 否 |
|description | String     | 优惠券描述  | 否 |
|amount | String     | 优惠券面值  | 否 |
|startTime | String     | 优惠券开始时间  | 否 |
|endTime | String     | 优惠券结束时间  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "couponId": "couponId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|couponId | Long       | 优惠券id  | 否 |
|status | String     | 操作状态  | 否 |

3. **删除优惠券**  
delete api/v1/merchants/coupons/{couponId}
request

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|couponId | Long       | 优惠券id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "couponId": "couponId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|couponId | Long       | 优惠券id  | 否 |
|status | String     | 操作状态  | 否 |

4. **修改优惠券**  
put api/v1/merchants/coupons/{couponId}  
request

```json
{
  "userId": "userId",
  "couponName": "couponName",
  "couponType": "couponType",
  "description": "description",
  "amount": "amount",
  "startTime": "startTime",
  "endTime": "endTime",
  "status": "status"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|----|
|userId | Long       | 用户id  | 否  |
|couponId | Long       | 优惠券id  | 否  |
|couponName | String     | 优惠券名称  | 是  |
|couponType | String     | 优惠券类型  | 是  |
|description | String     | 优惠券描述  | 是  |
|amount | String     | 优惠券金额 | 是  |
|startTime | String     | 优惠券开始时间  | 是  |
|endTime | String     | 优惠券结束时间  | 是  |
|status | String     | 优惠券状态  | 是  |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "couponId": "couponId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|couponId | Long       | 优惠券id | 否 |
|status | String     | 操作状态  | 否 |

5. **发放优惠券**  
post  api/v1/merchants/coupons/distribute  
request

```json
{
  "userId": "userId",
  "couponId": "couponId",
  "customerIds": [
    "customerId"
  ]
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |
|couponId | Long       | 优惠券id  | 否 |
|customerIds | List<Long> | 客户id列表  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "status": "status"
  }
}
```

|名字 | 类型         | 描述   | 可选 |
|---|------------|------|---|
|status | String     | 操作状态 | 否 |

### 促销商品管理

1. **获取促销商品列表**  
get api/v1/merchants/promotions  
request  

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": [
    {
      "promotionId": "promotionId",
      "name": "name",
      "type": "type",
      "description": "description",
      "startTime": "startTime",
      "endTime": "endTime",
      "status": "status",
      "createTime": "createTime",
      "updateTime": "updateTime"
    }
  ]
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|promotionId | Long       | 促销商品id  | 否 |
|name | String     | 促销商品名称  | 否 |
|type | String     | 促销商品类型  | 否 |
|description | String     | 促销商品描述  | 否 |
|startTime | String     | 促销商品开始时间  | 否 |
|endTime | String     | 促销商品结束时间  | 否 |
|status | String     | 促销商品状态  | 否 |

2. **增加促销商品**  
post api/v1/merchants/promotions  
request

```json
{
  "userId": "userId",
  "name": "name",
  "type": "type",
  "description": "description",
  "startTime": "startTime",
  "endTime": "endTime"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |
|name | String     | 促销商品名称  | 否 |
|type | String     | 促销商品类型  | 否 |
|description | String     | 促销商品描述  | 否 |
|startTime | String     | 促销商品开始时间  | 否 |
|endTime | String     | 促销商品结束时间  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "promotionId": "promotionId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述     | 可选 |
|---|------------|--------|---|
|promotionId | Long       | 促销商品id | 否 |
|status | String     | 操作状态   | 否 |

3. **删除促销商品**  
delete api/v1/merchants/promotions/{promotionId}  
request  

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述     | 可选 |
|---|------------|--------|---|
|promotionId | Long       | 促销商品id | 否 |
|userId | Long       | 用户id   | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "promotionId": "promotionId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述     | 可选 |
|---|------------|--------|---|
|promotionId | Long       | 促销商品id | 否 |
|status | String     | 操作状态   | 否 |

4. **修改促销商品**  
put api/v1/merchants/promotions/{promotionId}  
request

```json
{
  "userId": "userId",
  "name": "name",
  "type": "type",
  "description": "description",
  "startTime": "startTime",
  "endTime": "endTime",
  "status": "status"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否  |
|promotionId | Long       | 优惠券id  | 否  |
|name | String     | 优惠券名称  | 是  |
|type | String     | 优惠券类型  | 是  |
|description | String     | 优惠券描述  | 是  |
|startTime | String     | 优惠券开始时间  | 是  |
|endTime | String     | 优惠券结束时间  | 是  |
|status | String     | 优惠券状态  | 是  |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "promotionId": "promotionId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|promotionId | Long       | 优惠券id | 否 |
|status | String     | 操作状态 | 否 |

### 客服接口(商家端)

1. **获取售后申请列表**  
get api/v1/merchant/support/after-sales  
request

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": [
    { 
      "afterSaleId": "afterSaleId",
      "orderId": "orderId",
      "customerId": "customerId",
      "type": "type",
      "reason": "reason",
      "content": "content",
      "status": "status",
      "createTime": "createTime",
      "updateTime": "updateTime"
    }
  ]
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|afterSaleId | Long       | 售后id  | 否 |
|orderId | Long       | 订单id  | 否 |
|customerId | Long       | 客户id  | 否 |
|type | String     | 售后类型  | 否 |
|reason | String     | 售后原因  | 否 |
|content | String     | 售后内容  | 否 |
|status | String     | 售后状态  | 否 |

2. **获取私信客服消息列表**  
get api/v1/merchant/support/messages  
request

```json
{
  "userId": "userId",
  "customerId": "customerId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |
|customerId | Long       | 客户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": [
    {
      "messageId": "messageId",
      "employeeId": "employeeId",
      "customerId": "customerId",
      "content": "content",
      "createTime": "createTime",
      "updateTime": "updateTime"
    }
  ]
}
```

| 名字         | 类型     | 描述   | 可选 |
|------------|--------|------|----|
| messageId  | Long   | 私信id |    |
| employeeId | Long   | 员工id | 否  |
| customerId | Long   | 客户id | 否  |
| content    | String | 私信内容 | 否  |

3. **发送给指定用户信息**  
post api/v1/merchant/support/messages  
request

```json
{
  "userId": "userId",
  "customerId": "customerId",
  "content": "content"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |
|customerId | Long       | 客户id  | 否 |
|content | String     | 私信内容  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "messageId": "messageId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|messageId | Long       | 私信id  | 否 |
|status | String     | 操作状态  | 否 |

4. **撤回消息**  
delete api/v1/merchant/support/messages/{messageId}
request

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|messageId | Long       | 私信id  | 否 |
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "messageId": "messageId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|messageId | Long       | 私信id  | 否 |
|status | String     | 操作状态  | 否 |

5. **获取与客户会话信息**  
get api/v1/support/info  
request

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": [
    {
      "messageId": "messageId",
      "customerId": "customerId",
      "employeeId": "employeeId",
      "content": "content",
      "createTime": "createTime",
      "updateTime": "updateTime"
    }
  ]
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|messageId | Long       | 私信id  | 否 |
|customerId | Long       | 客户id  | 否 |
|employeeId | Long       | 员工id  | 否 |
|content | String     | 私信内容  | 否 |

### 用户信息接口(商家端)

1. **获取用户信息列表**  
get  api/v1/merchant/users  
request

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": [
    {
      "customerId": "customerId",
      "name": "name",
      "phone": "phone",
      "avatar_url": "avatar_url"
    }
  ]
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|customerId | Long       | 客户id  | 否 |
|name | String     | 客户姓名  | 否 |
|phone | String     | 客户手机号  | 否 |
|avatar_url | String     | 客户头像  | 否 |

2. **查找指定用户**  
get  api/v1/merchant/users/search  
request

```json
{
  "userId": "userId",
  "name": "name",
  "customerId": "customerId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|----|
|userId | Long       | 用户id  | 否  |
|name | String     | 客户姓名  | 是  |
|customerId | Long       | 客户id  | 否  |

response

```json
{
  "code": "code",
  "message": "message",
  "data": [
    {
      "customerId": "customerId",
      "name": "name",
      "phone": "phone",
      "avatar_url": "avatar_url"
    }
  ]
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|customerId | Long       | 客户id  | 否 |
|name | String     | 客户姓名  | 否 |
|phone | String     | 客户手机号  | 否 |
|avatar_url | String     | 客户头像  | 否 |

3. **获取用户支付流水**  
get  api/v1/payments/history  
request

```json
{
  "userId": "userId",
  "customerId": "customerId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |
|customerId | Long       | 客户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": [
    { 
      "paymentId": "paymentId",
      "customerId": "customerId",
      "amount": "amount",
      "createTime": "createTime",
      "status": "status"
    }
  ]
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|paymentId | Long       | 支付id  | 否 |
|customerId | Long       | 客户id  | 否 |
|amount | String     | 金额  | 否 |
|createTime | String     | 创建时间  | 否 |
|status | String     | 状态  | 否 |

4. **获取店员列表**  
get api/v1/admin/staff  
request  

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": [
    {
      "employeeId": "employeeId",
      "name": "name",
      "phone": "phone",
      "avatar_url": "avatar_url"
    }
  ]
}
```

5. **增加店员**  
post api/v1/admin/staff  
request

```json
{
  "userId": "userId",
  "name": "name",
  "phone": "phone",
  "avatar_url": "avatar_url"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |
|name | String     | 店员姓名  | 否 |
|phone | String     | 店员手机号  | 否 |
|avatar_url | String     | 店员头像  | 否 |

6. **删除店员**  
delete api/v1/admin/staff/{employeeId}  
request

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|employeeId | Long       | 店员id  | 否 |
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "employeeId": "employeeId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|employeeId | Long       | 店员id  | 否 |
|status | String     | 操作状态  | 否 |

7. **修改店员**  
put api/v1/admin/staff/{employeeId}  
request

```json
{
  "userId": "userId",
  "name": "name",
  "phone": "phone",
  "avatar_url": "avatar_url"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|employeeId | Long       | 店员id  | 否 |
|userId | Long       | 用户id  | 否 |
|name | String     | 店员姓名  | 否 |
|phone | String     | 店员手机号  | 否 |
|avatar_url | String     | 店员头像  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "employeeId": "employeeId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|employeeId | Long       | 店员id  | 否 |
|status | String     | 操作状态  | 否 |

8. **查找店员信息**  
get api/v1/admin/staff/search  
request

```json
{
  "userId": "userId",
  "name": "name",
  "employeeId": "employeeId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |
|name | String     | 店员姓名  | 否 |
|employeeId | Long       | 店员id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": [
    {
      "employeeId": "employeeId",
      "name": "name",
      "phone": "phone",
      "avatar_url": "avatar_url"
    }
  ]
}
```

### 店面信息管理(管理员端)

1. **获取店面信息列表**  
get api/v1/admin/stores  
request  

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": [
    {
      "storeId": "storeId",
      "name": "name",
      "merchantId": "merchantId",
      "description": "description"
    }
  ]
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|storeId | Long       | 店面id  | 否 |
|name | String     | 店面名称  | 否 |
|merchantId | Long       | 商家id  | 否 |
|description | String     | 店面描述  | 否 |

2. **增加店面信息**
3. **删除店面信息**
4. **修改店面信息**
5. **查找店面信息**  
参考前面的怎删改查  
这个其实没有必要，因为只有一家店

### 店员信息管理(管理员端)

1. **获取店员信息列表**
2. **增加店员信息**
3. **删除店员信息**
4. **修改店员信息**
5. **查找店员信息**  
参考商家端，只需要把路径改下就是了  

### 数据汇总接口  

1. **获取总商品销售信息**  
get api/v1/admin/sales  
request

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": [
    {
      "productId": "productId",
      "name": "name",
      "sales": "sales"
    }
  ]
}
```

2. **获取总客流量**  
get api/v1/admin/traffic  
request  

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "traffic": "traffic"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|traffic | Long       | 客流量  | 否 |

3. **获取总销售额**  
get api/v1/admin/sales/total  
request  

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "total": "total"
  }
}
```

4. **查找指定店面数据报表**  
不要了，因为只有一个店  

### 管理员信息接口  

1. **获取管理员信息**
2. **增加管理员信息**
3. **删除管理员信息**
4. **修改管理员信息**
5. **查找管理员信息**  
参照上面的增删改查，这个写不写问题不大
=======
# 注意  

如果数据在body中，使用DTO进行封装, 使用@RequestBody  
如果是PathVariable,  使用@PathVariable  
controller层返回Result\<T>(Result已经封装好了)  

---

前端请求头记得带上```userType```的header(方便起见用0-3，分别是admin,merchant,employee,customer).  
还有```tokenName```的header，这是登录时返回的token，用于后续请求。(开发时如果嫌太麻烦的话，可以把注册的interceptor删掉)(tokenName根据用户类型获取，配置文件里有)  
java数据类型和json有区别，这里用的java数据类型  
很多情况下body并不需要携带userId，因为登陆后id会存在缓存里。(userId可以是admin,employee,customer,merchant，视情况而定)

### 余额支付接口  

1. **确认支付**  
接口  
**post** /api/v1/payment/balance  

request

```json
{
  "orderId" : "orderId",
  "amount" : "amount",
  "payType" : "payType"
}
```

| 名字      | 类型         | 描述             | 可选 |
|---------|------------|----------------|----|
| orderId | Long       | 订单id           | 否  |
| amount  | BigDecimal | 订单金额           | 否  |
| payType | String     | 支付方式（用enum也可以） | 否  |

response

```json
{
  "code" : "code",
  "message" : "message",
  "data" : {
    "balance" : "balance",
    "orderId" : "orderId",
    "payType" : "payType"
  }
}

```

| 名字      | 类型      | 描述             | 可选 |
|---------|---------|----------------|----|
| code    | Integer | 状态码            | 否  |
| message | String  | 状态信息           | 否  |
| orderId | Long    | 订单id           | 否  |
| balance | String  | 用户余额           | 否  |
| payType | String  | 支付方式（用enum也可以） | 否  |

code和message在Result中存在，这里就不再赘述，后续从略

2. **显示余额**  
接口  
**get** /api/v1/user/balance  
request

```json

```

注：默认已经登陆不需要额外信息,当然,可以带上userId
response  

```json
{
  "code" : "code",
  "message" : "message",
  "data" : {
    "balance" : "balance"
  }
}

```

| 名字      | 类型         | 描述             | 可选 |
|---------|------------|----------------|---|
| balance | BigDecimal | 用户余额           | 否 |

### 优惠券核销接口

1. **优惠券校验**
post /api/v1/coupons/validate  
request  

```json
{
  "couponId": "couponId",
  "orderId": "orderId",
  "amount": "amount",
  "payType": "payType"
}
```

|名字 | 类型         | 描述             | 可选 |
|----|------------|----------------|---|
|couponId | Long       | 优惠券id           | 否 |
|orderId | Long       | 订单id           | 否 |
|amount | BigDecimal | 订单金额           | 否 |
|payType | String     | 支付方式（用enum也可以） | 否 |

response

```json
{
  "code" : "code",
  "message" : "message",
  "data" : {
    "amount" : "amount",
    "orderId" : "orderId",
    "payType" : "payType"
  }
}

```

| 名字      | 类型         | 描述             | 可选 |
|---------|------------|----------------|---|
| amount | BigDecimal | 订单金额           | 否 |
| orderId | Long       | 订单id           | 否 |
| payType | String     | 支付方式（用enum也可以） | 否 |

2. **满减计算接口**  
post /api/v1/coupons/calculate  
request  

```json
{
  "couponId": "couponId",
  "orderId": "orderId",
  "amount": "amount",
  "payType": "payType"
}
```

|名字 | 类型         | 描述             | 可选 |
|----|------------|----------------|---|
|couponId | Long       | 优惠券id           | 否 |
|orderId | Long       | 订单id           | 否 |
|amount | BigDecimal | 订单金额           | 否 |
|payType | String     | 支付方式（用enum也可以） | 否 |

response

```json
{
  "code" : "code",
  "message" : "message",
  "data" : {
    "amount" : "amount"
  }
}
```

| 名字      | 类型         | 描述             | 可选 |
|---------|------------|----------------|---|
| amount | BigDecimal | 订单金额           | 否 |

### 支付异常处理接口

1. **支付异常处理**  
post api/v1/payment/error  
request

```json
{
  "orderId" : "orderId",
  "userId" : "userId",
  "payType" : "payType"
}
```

|名字 | 类型         | 描述             | 可选 |
|----|------------|----------------|---|
|orderId | Long       | 订单id           | 否 |
|userId | Long       | 用户id           | 否 |
|payType | String     | 支付方式（用enum也可以） | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "retryToken": "token",
    "redirectUrl": "url",
    "orderId": "orderId",
    "payType": "payType"
  }
}
```

| 名字      | 类型      | 描述             | 可选 |
|---------|---------|----------------|---|
| retryToken | String   | 重试token           | 否 |
| redirectUrl | String   | 重试url           | 否 |
| orderId | Long    | 订单id           | 否 |
| payType | String  | 支付方式（用enum也可以） | 否 |

### 客服沟通接口(用户端)

1. **发送信息**  
post /api/v1/support/customer/messages  
request  

```json
{
  "userId": "userId",
  "employeeId": "employeeId",
  "content": "content",
  "orderId": "orderId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |
|employeeId | Long       | 员工id  | 否 |
|content | String     | 发送地信息 | 否 |
|orderId | Long       | 订单id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "messageId": "messageId",
    "status": "status"
  }
}
```

2. **获取客服信息**  
get /api/v1/support/customer/info  
request

```json
{
  "userId": "userId",
  "employeeId": "employeeId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |
|employeeId | Long       | 员工id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "employeeId": "employeeId",
    "employeeName": "employeeName",
    "employeeAvatar": "employeeAvatar",
    "employeePhone": "employeePhone"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|employeeId | Long       | 员工id  | 否 |
|employeeName | String     | 员工名称 | 否 |
|employeeAvatar | String     | 员工头像 | 否 |
|employeePhone | String     | 员工电话 | 否 |

3. **撤回信息**  
delete /api/v1/support/customer/messages/{messageId}  
request

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|messageId | Long       | 信息id  | 否 |
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "messageId": "messageId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|messageId | Long       | 信息id  | 否 |
|status | String     | 操作状态  | 否 |  

### 操作售后请求  

post /api/v1/after-sale/{requestId}/actions  
request

```json
{
  "userId": "userId",
  "action": "action"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|requestId | Long       | 请求id  | 否 |
|userId | Long       | 用户id  | 否 |
|action | String     | 操作  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "requestId": "requestId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|requestId | Long       | 请求id  | 否 |
|status | String     | 操作状态  | 否 |

### 凭证上传接口  

post /api/v1/after-sale/{requestId}/upload
request

```json
{
  "userId": "userId",
  "content": "content",
  "file": "file"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|requestId | Long       | 请求id  | 否 |
|userId | Long       | 用户id  | 否 |
|content | String     | 凭证描述  | 否 |
|file | MultipartFile     | 文件  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "requestId": "requestId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|requestId| Long       | 请求id  | 否 |
|status | String     | 操作状态  | 否 |

### 获取售后进度

get /api/v1/after-sale/{requestId}/status  
request

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|requestId | Long       | 请求id  | 否 |
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "requestId": "requestId",
    "status": "status"
  }
}
```

### 获取售后反馈  

get /api/v1/after-sale/{requestId}/feedback
request

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|requestId | Long       | 售后请求id  | 否 |
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "requestId": "requestId",
    "feedback": "feedback",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|requestId | Long       | 售后请求id  | 否 |
|feedback | String     | 反馈  | 否 |
|status | String     | 操作状态  | 否 |

### 客服沟通接口(客服端)

1. **发送信息**  
post /api/v1/support/employee/messages  
request  

```json
{
  "userId": "userId",
  "employeeId": "employeeId",
  "content": "content",
  "orderId": "orderId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |
|employeeId | Long       | 员工id  | 否 |
|content | String     | 发送地信息 | 否 |
|orderId | Long       | 订单id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "messageId": "messageId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|messageId | Long       | 信息id  | 否 |
|status | String     | 操作状态  | 否 |

2. **获取顾客信息**  
get /api/v1/support/employee/info  
request

```json
{
  "userId": "userId",
  "customerId": "customerId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |
|customerId | Long       | 客户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "customerId": "customerId",
    "customerName": "customerName",
    "customerAvatar": "customerAvatar"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|customerId | Long       | 客户id  | 否 |
|customerName | String     | 客户名称 | 否 |
|customerAvatar | String     | 客户头像 | 否 |

3. **撤回信息**  
delete /api/v1/support/employee/messages/{messageId}  
request

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|messageId | Long       | 信息id  | 否 |
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "messageId": "messageId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|messageId | Long       | 信息id  | 否 |
|status | String     | 操作状态  | 否 |  

### 查看售后请求

1. **获取售后请求列表**  
get api/v1/merchants/after-sale  
request

```json
{
  "userId": "userId"
}
```

| 名字     | 类型   | 描述   | 可选 |
|--------|------|------|----|
| userId | Long | 用户id | 否  |

response

```json
{
  "code": "code",
  "message": "message",
  "data": [
    {
      "afterSaleId": "afterSaleId",
      "orderId": "orderId",
      "customerId": "customerId",
      "type": "type",
      "status": "status",
      "content": "content",
      "createTime": "createTime",
      "updateTime": "updateTime"
    }
  ]
}
```

|  名字     | 类型   | 描述   | 可选 |
|---------|------|------|----|
| afterSaleId | Long | 售后id | 否  |
| orderId | Long | 订单id | 否  |
| customerId | Long | 客户id | 否  |
| type | String | 类型   | 否  |
| status | String | 状态   | 否  |
| content | String | 内容   | 否  |
| createTime | String | 创建时间 | 否  |
| updateTime | String | 更新时间 | 否  |

2. **同意请求**  
post api/v1/merchants/after-sale/{requestId}/approve  
request

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|requestId | Long       | 售后请求id  | 否 |
|userId | Long       | 商家id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "requestId": "requestId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|requestId | Long       | 售后请求id  | 否 |
|status | String     | 操作状态  | 否 |

3. **拒绝请求**  
post api/v1/merchants/after-sale/{requestId}/reject  
request

```json
{
  "userId": "userId",
  "reason": "reason"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|requestId | Long       | 售后请求id  | 否 |
|userId | Long       | 商家id  | 否 |
|reason | String     | 拒绝理由  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "requestId": "requestId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|requestId | Long       | 售后请求id  | 否 |
|status | String     | 操作状态  | 否 |

4. **提供处理反馈信息**  
post api/v1/merchants/after-sale/{requestId}/feedback  
request

```json
{
  "userId": "userId",
  "feedback": "feedback"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|requestId | Long       | 售后请求id  | 否 |
|userId | Long       | 商家id  | 否 |
|feedback | String     | 反馈信息  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "requestId": "requestId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|requestId | Long       | 售后请求id  | 否 |
|status | String     | 操作状态  | 否 |

### 优惠券接口(商家端)

1. **获取优惠券列表**  
get api/v1/merchants/coupons  
request  

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": [
    { 
      "couponId": "couponId",
      "name": "name",
      "type": "type",
      "description": "description",
      "amount": "amount",
      "startTime": "startTime",
      "endTime": "endTime",
      "status": "status",
      "createTime": "createTime",
      "updateTime": "updateTime"
    }
  ]
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|couponId | Long       | 优惠券id  | 否 |
|name | String     | 优惠券名称  | 否 |
|type | String     | 优惠券类型  | 否 |
|description | String     | 优惠券描述  | 否 |
|amount | String     | 优惠券面值  | 否 |
|startTime | String     | 优惠券开始时间  | 否 |
|endTime | String     | 优惠券结束时间  | 否 |
|status | String     | 优惠券状态  | 否 |

2. **增加优惠券**  
post api/v1/merchants/coupons  
request

```json
{
  "userId": "userId",
  "couponName": "couponName",
  "couponType": "couponType",
  "description": "description",
  "amount": "amount",
  "startTime": "startTime",
  "endTime": "endTime"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |
|couponName | String     | 优惠券名称  | 否 |
|couponType | String     | 优惠券类型  | 否 |
|description | String     | 优惠券描述  | 否 |
|amount | String     | 优惠券面值  | 否 |
|startTime | String     | 优惠券开始时间  | 否 |
|endTime | String     | 优惠券结束时间  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "couponId": "couponId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|couponId | Long       | 优惠券id  | 否 |
|status | String     | 操作状态  | 否 |

3. **删除优惠券**  
delete api/v1/merchants/coupons/{couponId}
request

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|couponId | Long       | 优惠券id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "couponId": "couponId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|couponId | Long       | 优惠券id  | 否 |
|status | String     | 操作状态  | 否 |

4. **修改优惠券**  
put api/v1/merchants/coupons/{couponId}  
request

```json
{
  "userId": "userId",
  "couponName": "couponName",
  "couponType": "couponType",
  "description": "description",
  "amount": "amount",
  "startTime": "startTime",
  "endTime": "endTime",
  "status": "status"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|----|
|userId | Long       | 用户id  | 否  |
|couponId | Long       | 优惠券id  | 否  |
|couponName | String     | 优惠券名称  | 是  |
|couponType | String     | 优惠券类型  | 是  |
|description | String     | 优惠券描述  | 是  |
|amount | String     | 优惠券金额 | 是  |
|startTime | String     | 优惠券开始时间  | 是  |
|endTime | String     | 优惠券结束时间  | 是  |
|status | String     | 优惠券状态  | 是  |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "couponId": "couponId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|couponId | Long       | 优惠券id | 否 |
|status | String     | 操作状态  | 否 |

5. **发放优惠券**  
post  api/v1/merchants/coupons/distribute  
request

```json
{
  "userId": "userId",
  "couponId": "couponId",
  "customerIds": [
    "customerId"
  ]
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |
|couponId | Long       | 优惠券id  | 否 |
|customerIds | List<Long> | 客户id列表  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "status": "status"
  }
}
```

|名字 | 类型         | 描述   | 可选 |
|---|------------|------|---|
|status | String     | 操作状态 | 否 |

### 促销商品管理

1. **获取促销商品列表**  
get api/v1/merchants/promotions  
request  

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": [
    {
      "promotionId": "promotionId",
      "name": "name",
      "type": "type",
      "description": "description",
      "startTime": "startTime",
      "endTime": "endTime",
      "status": "status",
      "createTime": "createTime",
      "updateTime": "updateTime"
    }
  ]
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|promotionId | Long       | 促销商品id  | 否 |
|name | String     | 促销商品名称  | 否 |
|type | String     | 促销商品类型  | 否 |
|description | String     | 促销商品描述  | 否 |
|startTime | String     | 促销商品开始时间  | 否 |
|endTime | String     | 促销商品结束时间  | 否 |
|status | String     | 促销商品状态  | 否 |

2. **增加促销商品**  
post api/v1/merchants/promotions  
request

```json
{
  "userId": "userId",
  "name": "name",
  "type": "type",
  "description": "description",
  "startTime": "startTime",
  "endTime": "endTime"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |
|name | String     | 促销商品名称  | 否 |
|type | String     | 促销商品类型  | 否 |
|description | String     | 促销商品描述  | 否 |
|startTime | String     | 促销商品开始时间  | 否 |
|endTime | String     | 促销商品结束时间  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "promotionId": "promotionId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述     | 可选 |
|---|------------|--------|---|
|promotionId | Long       | 促销商品id | 否 |
|status | String     | 操作状态   | 否 |

3. **删除促销商品**  
delete api/v1/merchants/promotions/{promotionId}  
request  

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述     | 可选 |
|---|------------|--------|---|
|promotionId | Long       | 促销商品id | 否 |
|userId | Long       | 用户id   | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "promotionId": "promotionId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述     | 可选 |
|---|------------|--------|---|
|promotionId | Long       | 促销商品id | 否 |
|status | String     | 操作状态   | 否 |

4. **修改促销商品**  
put api/v1/merchants/promotions/{promotionId}  
request

```json
{
  "userId": "userId",
  "name": "name",
  "type": "type",
  "description": "description",
  "startTime": "startTime",
  "endTime": "endTime",
  "status": "status"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否  |
|promotionId | Long       | 优惠券id  | 否  |
|name | String     | 优惠券名称  | 是  |
|type | String     | 优惠券类型  | 是  |
|description | String     | 优惠券描述  | 是  |
|startTime | String     | 优惠券开始时间  | 是  |
|endTime | String     | 优惠券结束时间  | 是  |
|status | String     | 优惠券状态  | 是  |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "promotionId": "promotionId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|promotionId | Long       | 优惠券id | 否 |
|status | String     | 操作状态 | 否 |

### 客服接口(商家端)

1. **获取售后申请列表**  
get api/v1/merchant/support/after-sales  
request

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": [
    { 
      "afterSaleId": "afterSaleId",
      "orderId": "orderId",
      "customerId": "customerId",
      "type": "type",
      "reason": "reason",
      "content": "content",
      "status": "status",
      "createTime": "createTime",
      "updateTime": "updateTime"
    }
  ]
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|afterSaleId | Long       | 售后id  | 否 |
|orderId | Long       | 订单id  | 否 |
|customerId | Long       | 客户id  | 否 |
|type | String     | 售后类型  | 否 |
|reason | String     | 售后原因  | 否 |
|content | String     | 售后内容  | 否 |
|status | String     | 售后状态  | 否 |

2. **获取私信客服消息列表**  
get api/v1/merchant/support/messages  
request

```json
{
  "userId": "userId",
  "customerId": "customerId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |
|customerId | Long       | 客户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": [
    {
      "messageId": "messageId",
      "employeeId": "employeeId",
      "customerId": "customerId",
      "content": "content",
      "createTime": "createTime",
      "updateTime": "updateTime"
    }
  ]
}
```

| 名字         | 类型     | 描述   | 可选 |
|------------|--------|------|----|
| messageId  | Long   | 私信id |    |
| employeeId | Long   | 员工id | 否  |
| customerId | Long   | 客户id | 否  |
| content    | String | 私信内容 | 否  |

3. **发送给指定用户信息**  
post api/v1/merchant/support/messages  
request

```json
{
  "userId": "userId",
  "customerId": "customerId",
  "content": "content"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |
|customerId | Long       | 客户id  | 否 |
|content | String     | 私信内容  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "messageId": "messageId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|messageId | Long       | 私信id  | 否 |
|status | String     | 操作状态  | 否 |

4. **撤回消息**  
delete api/v1/merchant/support/messages/{messageId}
request

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|messageId | Long       | 私信id  | 否 |
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "messageId": "messageId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|messageId | Long       | 私信id  | 否 |
|status | String     | 操作状态  | 否 |

5. **获取与客户会话信息**  
get api/v1/support/info  
request

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": [
    {
      "messageId": "messageId",
      "customerId": "customerId",
      "employeeId": "employeeId",
      "content": "content",
      "createTime": "createTime",
      "updateTime": "updateTime"
    }
  ]
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|messageId | Long       | 私信id  | 否 |
|customerId | Long       | 客户id  | 否 |
|employeeId | Long       | 员工id  | 否 |
|content | String     | 私信内容  | 否 |

### 用户信息接口(商家端)

1. **获取用户信息列表**  
get  api/v1/merchant/users  
request

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": [
    {
      "customerId": "customerId",
      "name": "name",
      "phone": "phone",
      "avatar_url": "avatar_url"
    }
  ]
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|customerId | Long       | 客户id  | 否 |
|name | String     | 客户姓名  | 否 |
|phone | String     | 客户手机号  | 否 |
|avatar_url | String     | 客户头像  | 否 |

2. **查找指定用户**  
get  api/v1/merchant/users/search  
request

```json
{
  "userId": "userId",
  "name": "name",
  "customerId": "customerId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|----|
|userId | Long       | 用户id  | 否  |
|name | String     | 客户姓名  | 是  |
|customerId | Long       | 客户id  | 否  |

response

```json
{
  "code": "code",
  "message": "message",
  "data": [
    {
      "customerId": "customerId",
      "name": "name",
      "phone": "phone",
      "avatar_url": "avatar_url"
    }
  ]
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|customerId | Long       | 客户id  | 否 |
|name | String     | 客户姓名  | 否 |
|phone | String     | 客户手机号  | 否 |
|avatar_url | String     | 客户头像  | 否 |

3. **获取用户支付流水**  
get  api/v1/payments/history  
request

```json
{
  "userId": "userId",
  "customerId": "customerId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |
|customerId | Long       | 客户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": [
    { 
      "paymentId": "paymentId",
      "customerId": "customerId",
      "amount": "amount",
      "createTime": "createTime",
      "status": "status"
    }
  ]
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|paymentId | Long       | 支付id  | 否 |
|customerId | Long       | 客户id  | 否 |
|amount | String     | 金额  | 否 |
|createTime | String     | 创建时间  | 否 |
|status | String     | 状态  | 否 |

4. **获取店员列表**  
get api/v1/admin/staff  
request  

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": [
    {
      "employeeId": "employeeId",
      "name": "name",
      "phone": "phone",
      "avatar_url": "avatar_url"
    }
  ]
}
```

5. **增加店员**  
post api/v1/admin/staff  
request

```json
{
  "userId": "userId",
  "name": "name",
  "phone": "phone",
  "avatar_url": "avatar_url"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |
|name | String     | 店员姓名  | 否 |
|phone | String     | 店员手机号  | 否 |
|avatar_url | String     | 店员头像  | 否 |

6. **删除店员**  
delete api/v1/admin/staff/{employeeId}  
request

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|employeeId | Long       | 店员id  | 否 |
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "employeeId": "employeeId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|employeeId | Long       | 店员id  | 否 |
|status | String     | 操作状态  | 否 |

7. **修改店员**  
put api/v1/admin/staff/{employeeId}  
request

```json
{
  "userId": "userId",
  "name": "name",
  "phone": "phone",
  "avatar_url": "avatar_url"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|employeeId | Long       | 店员id  | 否 |
|userId | Long       | 用户id  | 否 |
|name | String     | 店员姓名  | 否 |
|phone | String     | 店员手机号  | 否 |
|avatar_url | String     | 店员头像  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "employeeId": "employeeId",
    "status": "status"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|employeeId | Long       | 店员id  | 否 |
|status | String     | 操作状态  | 否 |

8. **查找店员信息**  
get api/v1/admin/staff/search  
request

```json
{
  "userId": "userId",
  "name": "name",
  "employeeId": "employeeId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |
|name | String     | 店员姓名  | 否 |
|employeeId | Long       | 店员id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": [
    {
      "employeeId": "employeeId",
      "name": "name",
      "phone": "phone",
      "avatar_url": "avatar_url"
    }
  ]
}
```

### 店面信息管理(管理员端)

1. **获取店面信息列表**  
get api/v1/admin/stores  
request  

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": [
    {
      "storeId": "storeId",
      "name": "name",
      "merchantId": "merchantId",
      "description": "description"
    }
  ]
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|storeId | Long       | 店面id  | 否 |
|name | String     | 店面名称  | 否 |
|merchantId | Long       | 商家id  | 否 |
|description | String     | 店面描述  | 否 |

2. **增加店面信息**
3. **删除店面信息**
4. **修改店面信息**
5. **查找店面信息**  
参考前面的怎删改查  
这个其实没有必要，因为只有一家店

### 店员信息管理(管理员端)

1. **获取店员信息列表**
2. **增加店员信息**
3. **删除店员信息**
4. **修改店员信息**
5. **查找店员信息**  
参考商家端，只需要把路径改下就是了  

### 数据汇总接口  

1. **获取总商品销售信息**  
get api/v1/admin/sales  
request

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": [
    {
      "productId": "productId",
      "name": "name",
      "sales": "sales"
    }
  ]
}
```

2. **获取总客流量**  
get api/v1/admin/traffic  
request  

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "traffic": "traffic"
  }
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|traffic | Long       | 客流量  | 否 |

3. **获取总销售额**  
get api/v1/admin/sales/total  
request  

```json
{
  "userId": "userId"
}
```

|名字 | 类型         | 描述    | 可选 |
|---|------------|-------|---|
|userId | Long       | 用户id  | 否 |

response

```json
{
  "code": "code",
  "message": "message",
  "data": {
    "total": "total"
  }
}
```

4. **查找指定店面数据报表**  
不要了，因为只有一个店  

### 管理员信息接口  

1. **获取管理员信息**
2. **增加管理员信息**
3. **删除管理员信息**
4. **修改管理员信息**
5. **查找管理员信息**  
参照上面的增删改查，这个写不写问题不大
>>>>>>> upstream/master
