# mo_db 测试数据库结构与数据报告

---

> 本文档包含测试数据库 mo_db 的所有表的结构和测试数据。

---

## 1. admins（管理员表）

**结构**

| Field      | Type                 | Null | Key | Default | Extra          |
|------------|----------------------|------|-----|---------|----------------|
| id         | int                  | NO   | PRI | NULL    | auto_increment |
| uuid       | varchar(255)         | NO   | UNI | NULL    |                |
| username   | varchar(50)          | NO   | UNI | NULL    |                |
| password   | varchar(255)         | NO   |     | NULL    |                |
| role       | enum('root','admin') | YES  |     | admin   |                |
| createTime | timestamp            | YES  |     | NULL    |                |
| updateTime | timestamp            | YES  |     | NULL    |                |

**数据**

| id | uuid     | username | password | role | createTime          | updateTime          |
|----|----------|----------|----------|------|---------------------|---------------------|
|  1 | adm-root | root     | password | root | 当前时间戳           | 当前时间戳           |

---

## 2. customers（顾客表）

**结构**

| Field         | Type          | Null | Key | Default | Extra          |
|---------------|---------------|------|-----|---------|----------------|
| id            | int           | NO   | PRI | NULL    | auto_increment |
| wechat_openid | varchar(255)  | NO   | UNI | NULL    |                |
| uuid          | varchar(255)  | NO   | UNI | NULL    |                |
| username      | varchar(30)   | YES  |     | NULL    |                |
| password      | varchar(255)  | NO   |     | NULL    |                |
| avatar_url    | varchar(255)  | YES  |     | NULL    |                |
| balance       | decimal(10,2) | YES  |     | 0.00    |                |
| createTime    | timestamp     | YES  |     | NULL    |                |
| updateTime    | timestamp     | YES  |     | NULL    |                |
| phone_num     | varchar(20)   | YES  |     | NULL    |                |
| gender        | varchar(10)   | YES  |     | NULL    |                |
| address       | varchar(255)  | YES  |     | NULL    |                |
| name          | varchar(50)   | YES  |     | NULL    |                |

**数据**

| id | wechat_openid     | uuid                                 | username     | password | avatar_url                             | balance | createTime          | updateTime          | phone_num   | gender | address                                      | name |
|----|-------------------|--------------------------------------|--------------|----------|----------------------------------------|---------|---------------------|---------------------|-------------|--------|----------------------------------------------|------|
|  1 | dummy_openid_1234 | 79111e92-36ec-11f0-91e0-6121812639ae | 17344402975  | 20050311 | https://thispersondoesnotexist.com/image | 0.00    | 2025-05-22 17:09:31 | 2025-05-22 17:09:31 | 17344402975 | 男     | 上海市浦东新区张江高科技园区创新大厦B座501室 | shx  |

---

## 3. merchants（商家表）

**结构**

| Field      | Type         | Null | Key | Default | Extra          |
|------------|--------------|------|-----|---------|----------------|
| id         | int          | NO   | PRI | NULL    | auto_increment |
| uuid       | varchar(255) | NO   | UNI | NULL    |                |
| username   | varchar(50)  | YES  |     | NULL    |                |
| password   | varchar(255) | NO   |     | NULL    |                |
| number     | varchar(20)  | YES  |     | NULL    |                |
| address    | varchar(255) | YES  |     | NULL    |                |
| createTime | timestamp    | YES  |     | NULL    |                |
| updateTime | timestamp    | YES  |     | NULL    |                |

**数据**

| id | uuid         | username  | password | number      | address                        | createTime          | updateTime          |
|----|--------------|-----------|----------|-------------|--------------------------------|---------------------|---------------------|
|  1 | mer-test-1   | merchant1 | 123456   | 18888888888 | 上海市浦东新区张江高科技园区   | 当前时间戳          | 当前时间戳          |

---

## 4. employees（员工表）

**结构**

| Field       | Type                      | Null | Key | Default | Extra          |
|-------------|---------------------------|------|-----|---------|----------------|
| id          | int                       | NO   | PRI | NULL    | auto_increment |
| uuid        | varchar(255)              | NO   | UNI | NULL    |                |
| username    | varchar(50)               | YES  |     | NULL    |                |
| password    | varchar(255)              | NO   |     | NULL    |                |
| status      | enum('active','inactive') | YES  |     | active  |                |
| createTime  | timestamp                 | YES  |     | NULL    |                |
| updateTime  | timestamp                 | YES  |     | NULL    |                |
| merchant_id | int                       | YES  | MUL | NULL    |                |

**数据**

| id | uuid       | username  | password | status | createTime | updateTime | merchant_id |
|----|------------|-----------|----------|--------|------------|------------|-------------|
|  1 | emp-test-1 | employee1 | 123456   | active | 当前时间戳 | 当前时间戳 | 1           |

---

## 5. dishes（菜品表）

**结构**

| Field       | Type          | Null | Key | Default | Extra          |
|-------------|---------------|------|-----|---------|----------------|
| id          | int           | NO   | PRI | NULL    | auto_increment |
| name        | varchar(50)   | NO   |     | NULL    |                |
| price       | decimal(10,2) | NO   |     | NULL    |                |
| category_id | int           | NO   |     | NULL    |                |
| description | text          | YES  |     | NULL    |                |
| image_url   | varchar(255)  | YES  |     | NULL    |                |
| createTime  | timestamp     | YES  |     | NULL    |                |
| updateTime  | timestamp     | YES  |     | NULL    |                |
| merchant_id | int           | YES  | MUL | NULL    |                |
| status      | tinyint       | YES  |     | 1       |                |

**数据**

| id | name        | price | category_id | description                          | image_url               | merchant_id | status |
|----|-------------|-------|-------------|--------------------------------------|-------------------------|-------------|--------|
| 1  | 鱼香肉丝    | 28.00 | 1           | 主料：猪肉、胡萝卜、青椒、木耳       | /static/images/dish1.jpg | 1           | 1      |
| 2  | 宫保鸡丁    | 26.00 | 1           | 主料：鸡胸肉、花生米、黄瓜、胡萝卜   | /static/images/dish2.jpg | 1           | 1      |
| 3  | 红烧排骨    | 32.00 | 1           | 主料：猪排骨、土豆、胡萝卜           | /static/images/dish3.jpg | 1           | 1      |
| 4  | 麻婆豆腐    | 22.00 | 2           | 主料：豆腐、肉末、豆瓣酱             | /static/images/dish4.jpg | 1           | 1      |
| 5  | 干锅土豆片  | 28.00 | 5           | 主料：土豆、辣椒、木耳、肉片         | /static/images/dish5.jpg | 1           | 1      |
| 6  | 水煮肉片    | 32.00 | 4           | 主料：猪肉、豆芽、白菜               | /static/images/dish6.jpg | 1           | 1      |
| 7  | 蒜蓉蒸茄子  | 18.00 | 8           | 主料：茄子、蒜蓉                     | /static/images/dish7.jpg | 1           | 1      |
| 8  | 白米饭      | 2.00  | 3           | 精选东北大米                         | /static/images/dish8.jpg | 1           | 1      |
| 9  | 番茄蛋花汤  | 15.00 | 7           | 主料：番茄、鸡蛋                     | /static/images/dish9.jpg | 1           | 1      |
| 10 | 回锅肉      | 28.00 | 1           | 主料：五花肉、青椒                   | /static/images/dish10.jpg | 1          | 1      |
| 11 | 青椒土豆丝  | 16.00 | 8           | 主料：土豆、青椒                     | /static/images/dish11.jpg | 1          | 1      |
| 12 | 干煸四季豆  | 18.00 | 8           | 主料：四季豆、辣椒                   | /static/images/dish12.jpg | 1          | 1      |

---

## 6. orders（订单表）

**结构**

| Field       | Type                                    | Null | Key | Default | Extra          |
|-------------|-----------------------------------------|------|-----|---------|----------------|
| id          | int                                     | NO   | PRI | NULL    | auto_increment |
| customer_id | int                                     | YES  | MUL | NULL    |                |
| merchant_id | int                                     | YES  | MUL | NULL    |                |
| total       | decimal(10,2)                           | NO   |     | NULL    |                |
| status      | enum('pending','completed','cancelled') | YES  |     | NULL    |                |
| createTime  | timestamp                               | YES  |     | NULL    |                |
| updateTime  | timestamp                               | YES  |     | NULL    |                |

**数据**

| id | customer_id | merchant_id | total  | status    | createTime          | updateTime          |
|----|-------------|-------------|--------|-----------|---------------------|---------------------|
|  1 |           1 |           1 | 114.00 | completed | 2025-05-24 18:05:03 | 2025-05-24 18:05:03 |
|  2 |           1 |           1 |  76.00 | pending   | 2025-05-26 18:05:16 | 2025-05-26 18:05:16 |
|  3 |           1 |           1 |  88.00 | completed | 2025-05-27 13:30:25 | 2025-05-27 13:30:25 |

---

## 7. order_details（订单详情表）

**结构**

| Field      | Type          | Null | Key | Default | Extra          |
|------------|---------------|------|-----|---------|----------------|
| id         | int           | NO   | PRI | NULL    | auto_increment |
| order_id   | int           | YES  | MUL | NULL    |                |
| dish_id    | int           | YES  | MUL | NULL    |                |
| quantity   | int           | YES  |     | NULL    |                |
| price      | decimal(10,2) | YES  |     | NULL    |                |
| createTime | timestamp     | YES  |     | NULL    |                |
| updateTime | timestamp     | YES  |     | NULL    |                |

**数据**

| id | order_id | dish_id | quantity | price | createTime          | updateTime          |
|----|----------|---------|----------|-------|---------------------|---------------------|
|  1 |        1 |       1 |        2 | 28.00 | 2025-05-24 18:05:12 | 2025-05-24 18:05:12 |
|  2 |        1 |       2 |        1 | 26.00 | 2025-05-24 18:05:12 | 2025-05-24 18:05:12 |
|  3 |        1 |       3 |        1 | 32.00 | 2025-05-24 18:05:12 | 2025-05-24 18:05:12 |
|  4 |        2 |       2 |        2 | 26.00 | 2025-05-26 18:05:24 | 2025-05-26 18:05:24 |
|  5 |        2 |       4 |        1 | 22.00 | 2025-05-26 18:05:24 | 2025-05-26 18:05:24 |
|  6 |        2 |       5 |        1 | 28.00 | 2025-05-26 18:05:24 | 2025-05-26 18:05:24 |
|  7 |        3 |       1 |        2 | 28.00 | 2025-05-27 13:30:47 | 2025-05-27 13:30:47 |
|  8 |        3 |       3 |        1 | 32.00 | 2025-05-27 13:30:50 | 2025-05-27 13:30:50 |

---

## 8. order_comments（订单评论表）

**结构**

| Field      | Type      | Null | Key | Default | Extra          |
|------------|-----------|------|-----|---------|----------------|
| id         | int       | NO   | PRI | NULL    | auto_increment |
| order_id   | int       | YES  | MUL | NULL    |                |
| comment    | text      | YES  |     | NULL    |                |
| createTime | timestamp | YES  |     | NULL    |                |
| updateTime | timestamp | YES  |     | NULL    |                |

**数据**

| id | order_id | comment                                                | createTime          | updateTime          |
|----|----------|--------------------------------------------------------|---------------------|---------------------|
|  1 |        3 | 菜品很好吃，配送速度快，服务态度好！                   | 2025-05-27 13:30:57 | 2025-05-27 13:30:57 |

---

## 9. order_afters（订单售后表）

**结构**

| Field      | Type                                  | Null | Key | Default | Extra          |
|------------|---------------------------------------|------|-----|---------|----------------|
| id         | int                                   | NO   | PRI | NULL    | auto_increment |
| order_id   | int                                   | YES  | MUL | NULL    |                |
| user_id    | int                                   | YES  | MUL | NULL    |                |
| type       | enum('refund','replace','other')      | YES  |     | NULL    |                |
| reason     | text                                  | YES  |     | NULL    |                |
| content    | text                                  | YES  |     | NULL    |                |
| status     | enum('pending','approved','rejected') | YES  |     | NULL    |                |
| createTime | timestamp                             | YES  |     | NULL    |                |
| updateTime | timestamp                             | YES  |     | NULL    |                |

**数据**

| id | order_id | user_id | type   | reason     | content                                     | status   | createTime          | updateTime          |
|----|----------|---------|--------|------------|---------------------------------------------|----------|---------------------|---------------------|
|  1 |        3 |       1 | refund | 菜品有异味 | 我点的红烧肉盖饭有点异味，希望能够退款      | approved | 2025-05-27 13:31:23 | 2025-05-27 13:31:23 |

---

## 10. carts（购物车表）

**结构**

| Field      | Type      | Null | Key | Default | Extra          |
|------------|-----------|------|-----|---------|----------------|
| id         | int       | NO   | PRI | NULL    | auto_increment |
| user_id    | int       | YES  | MUL | NULL    |                |
| createTime | timestamp | YES  |     | NULL    |                |
| updateTime | timestamp | YES  |     | NULL    |                |

**数据**

| id  | user_id | createTime          | updateTime          |
|-----|---------|---------------------|---------------------|
|  1 |       1 | 2025-05-27 13:30:10 | 2025-05-27 13:30:10 |

---

## 11. cart_items（购物车项表）

**结构**

| Field     | Type                    | Null | Key | Default | Extra          |
|-----------|-------------------------|------|-----|---------|----------------|
| id        | int                     | NO   | PRI | NULL    | auto_increment |
| cart_id   | int                     | YES  | MUL | NULL    |                |
| item_id   | int                     | YES  |     | NULL    |                |
| item_type | enum('DISH','SET_MEAL') | NO   |     | NULL    |                |
| quantity  | int                     | NO   |     | 1       |                |
| price     | decimal(10,2)           | YES  |     | NULL    |                |

**数据**

| id | cart_id | item_id | item_type | quantity | price |
|----|---------|---------|-----------|----------|-------|
|  1 |       1 |       1 | DISH      |        2 | 28.00 |
|  2 |       1 |       2 | DISH      |        1 | 32.00 |

---

## 12. coupons（优惠券表）

**结构**

| Field       | Type                       | Null | Key | Default | Extra          |
|-------------|----------------------------|------|-----|---------|----------------|
| id          | int                        | NO   | PRI | NULL    | auto_increment |
| user_id     | int                        | YES  | MUL | NULL    |                |
| name        | varchar(50)                | NO   |     | NULL    |                |
| description | text                       | YES  |     | NULL    |                |
| type        | enum('fixed','percentage') | YES  |     | NULL    |                |
| value       | decimal(10,2)              | YES  |     | NULL    |                |
| min_amount  | decimal(10,2)              | YES  |     | 0.00    |                |
| max_amount  | decimal(10,2)              | YES  |     | 1000.00 |                |
| start_time  | timestamp                  | YES  |     | NULL    |                |
| end_time    | timestamp                  | YES  |     | NULL    |                |
| createTime  | timestamp                  | YES  |     | NULL    |                |
| updateTime  | timestamp                  | YES  |     | NULL    |                |

**数据**

| id | user_id | name         | description | type       | value | min_amount | max_amount | start_time          | end_time            | createTime          | updateTime          |
|----|---------|--------------|-------------|------------|-------|------------|------------|---------------------|---------------------|---------------------|---------------------|
|  1 |       1 | 新用户满减券 | 满50减10元  | fixed      | 10.00 | 50.00      | 100.00     | 2025-05-27 13:31:10 | 2025-06-26 13:31:10 | 2025-05-27 13:31:10 | 2025-05-27 13:31:10 |
|  2 |       1 | 折扣券       | 全场8折     | percentage | 20.00 | 0.00       | 200.00     | 2025-05-27 13:31:14 | 2025-06-11 13:31:14 | 2025-05-27 13:31:14 | 2025-05-27 13:31:14 |

---

## 13. promotions（促销活动表）

**结构**

| Field       | Type         | Null | Key | Default | Extra          |
|-------------|--------------|------|-----|---------|----------------|
| id          | int          | NO   | PRI | NULL    | auto_increment |
| name        | varchar(50)  | NO   |     | NULL    |                |
| description | text         | YES  |     | NULL    |                |
| start_time  | timestamp    | YES  |     | NULL    |                |
| end_time    | timestamp    | YES  |     | NULL    |                |
| createTime  | timestamp    | YES  |     | NULL    |                |
| updateTime  | timestamp    | YES  |     | NULL    |                |

**数据**

| id | name     | description                 | start_time          | end_time            | createTime          | updateTime          |
|----|----------|----------------------------|---------------------|---------------------|---------------------|---------------------|
|  1 | 夏季特惠 | 夏季清凉特惠，全场满100减20 | 2025-05-27 13:31:19 | 2025-07-26 13:31:19 | 2025-05-27 13:31:19 | 2025-05-27 13:31:19 |

---

## 14. stores（门店表）

**结构**

| Field       | Type         | Null | Key | Default | Extra          |
|-------------|--------------|------|-----|---------|----------------|
| id          | int          | NO   | PRI | NULL    | auto_increment |
| name        | varchar(50)  | NO   |     | NULL    |                |
| merchant_id | int          | YES  | MUL | NULL    |                |
| address     | varchar(255) | YES  |     | NULL    |                |
| info        | text         | YES  |     | NULL    |                |
| createTime  | timestamp    | YES  |     | NULL    |                |
| updateTime  | timestamp    | YES  |     | NULL    |                |

**数据**

| id | name              | merchant_id | address                                | info                                         | createTime          | updateTime          |
|----|-------------------|-------------|----------------------------------------|----------------------------------------------|---------------------|---------------------|
|  1 | 美君外卖旗舰店    |           1 | 上海市浦东新区张江高科技园区创新大厦A座一楼 | 专注于提供优质美食的外卖服务，菜品多样，配送迅速。 | 当前时间戳          | 当前时间戳          |

---

## 数据库变更和测试用户说明

1. **数据库结构变更**：
   - 顾客表增加了phone_num, gender, address, name字段
   - 菜品表增加了status字段
   - 所有表的外键关系和约束保持一致

2. **测试用户账户**：
   - 顾客账户：17344402975（密码：20050311）- 保持不变
   - 商家账户：merchant1（密码：123456）- 已按要求更新
   - 员工账户：employee1（密码：123456）- 已按要求更新
   
3. **数据关联**：
   - 所有与顾客相关的数据都绑定到17344402975账户（ID=1）
   - 所有与商家和员工相关的数据都绑定到测试用户账户

4. **订单和交易数据**：
   - 包含3个订单数据，覆盖不同状态
   - 包含售后处理记录
   - 包含评价数据 