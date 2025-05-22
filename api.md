## 余额支付接口 
1. **确认支付**  
post /api/v1/payment/balance  
request
```json
{
  "orderId" : "orderId",
  "amount" : "amount",
  "payType" : "payType"
}
```
|名字 | 类型 | 描述 | 可选 | 
|---|---|---|---|
|orderId | String | 订单id | 否 |
|amount | String | 金额 | 否 |
|payType | String | 支付方式 | 否 |
2. **显示余额**  
get /api/v1/user/balance