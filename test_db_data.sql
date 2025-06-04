-- 使用mo_db数据库
USE mo_db;

-- 插入测试顾客数据
INSERT INTO customers (wechat_openid, uuid, username, password, avatar_url, balance, createTime, updateTime, phone_num, gender, address, name)
VALUES ('dummy_openid_1234', '79111e92-36ec-11f0-91e0-6121812639ae', '17344402975', '20050311', 'https://thispersondoesnotexist.com/image', 0.00, '2025-05-22 17:09:31', '2025-05-22 17:09:31', '17344402975', '男', '上海市浦东新区张江高科技园区创新大厦B座501室', 'shx');

-- 插入测试商家
INSERT INTO merchants (uuid, username, password, number, address, createTime, updateTime)
VALUES ('mer-test-1', 'merchant1', '123456', '18888888888', '上海市浦东新区张江高科技园区', NOW(), NOW());

-- 插入测试员工
INSERT INTO employees (uuid, username, password, status, createTime, updateTime, merchant_id)
VALUES ('emp-test-1', 'employee1', '123456', 'active', NOW(), NOW(), 1);

-- 插入测试菜品数据
INSERT INTO dishes (name, price, category_id, description, image_url, merchant_id, status, createTime, updateTime)
VALUES 
('鱼香肉丝', 28.00, 1, '主料：猪肉、胡萝卜、青椒、木耳', '/static/images/dish1.jpg', 1, 1, NOW(), NOW()),
('宫保鸡丁', 26.00, 1, '主料：鸡胸肉、花生米、黄瓜、胡萝卜', '/static/images/dish2.jpg', 1, 1, NOW(), NOW()),
('红烧排骨', 32.00, 1, '主料：猪排骨、土豆、胡萝卜', '/static/images/dish3.jpg', 1, 1, NOW(), NOW()),
('麻婆豆腐', 22.00, 2, '主料：豆腐、肉末、豆瓣酱', '/static/images/dish4.jpg', 1, 1, NOW(), NOW()),
('干锅土豆片', 28.00, 5, '主料：土豆、辣椒、木耳、肉片', '/static/images/dish5.jpg', 1, 1, NOW(), NOW()),
('水煮肉片', 32.00, 4, '主料：猪肉、豆芽、白菜', '/static/images/dish6.jpg', 1, 1, NOW(), NOW()),
('蒜蓉蒸茄子', 18.00, 8, '主料：茄子、蒜蓉', '/static/images/dish7.jpg', 1, 1, NOW(), NOW()),
('白米饭', 2.00, 3, '精选东北大米', '/static/images/dish8.jpg', 1, 1, NOW(), NOW()),
('番茄蛋花汤', 15.00, 7, '主料：番茄、鸡蛋', '/static/images/dish9.jpg', 1, 1, NOW(), NOW()),
('回锅肉', 28.00, 1, '主料：五花肉、青椒', '/static/images/dish10.jpg', 1, 1, NOW(), NOW()),
('青椒土豆丝', 16.00, 8, '主料：土豆、青椒', '/static/images/dish11.jpg', 1, 1, NOW(), NOW()),
('干煸四季豆', 18.00, 8, '主料：四季豆、辣椒', '/static/images/dish12.jpg', 1, 1, NOW(), NOW());

-- 插入测试门店数据
INSERT INTO stores (name, merchant_id, address, info, createTime, updateTime)
VALUES ('美君外卖旗舰店', 1, '上海市浦东新区张江高科技园区创新大厦A座一楼', '专注于提供优质美食的外卖服务，菜品多样，配送迅速。', NOW(), NOW());

-- 插入测试订单数据
INSERT INTO orders (customer_id, merchant_id, total, status, createTime, updateTime)
VALUES 
(1, 1, 114.00, 'completed', '2025-05-24 18:05:03', '2025-05-24 18:05:03'),
(1, 1, 76.00, 'pending', '2025-05-26 18:05:16', '2025-05-26 18:05:16'),
(1, 1, 88.00, 'completed', '2025-05-27 13:30:25', '2025-05-27 13:30:25');

-- 插入订单详情数据
INSERT INTO order_details (order_id, dish_id, quantity, price, createTime, updateTime)
VALUES 
(1, 1, 2, 28.00, '2025-05-24 18:05:12', '2025-05-24 18:05:12'),
(1, 2, 1, 26.00, '2025-05-24 18:05:12', '2025-05-24 18:05:12'),
(1, 3, 1, 32.00, '2025-05-24 18:05:12', '2025-05-24 18:05:12'),
(2, 2, 2, 26.00, '2025-05-26 18:05:24', '2025-05-26 18:05:24'),
(2, 4, 1, 22.00, '2025-05-26 18:05:24', '2025-05-26 18:05:24'),
(2, 5, 1, 28.00, '2025-05-26 18:05:24', '2025-05-26 18:05:24'),
(3, 1, 2, 28.00, '2025-05-27 13:30:47', '2025-05-27 13:30:47'),
(3, 3, 1, 32.00, '2025-05-27 13:30:50', '2025-05-27 13:30:50');

-- 插入订单评论数据
INSERT INTO order_comments (order_id, comment, createTime, updateTime)
VALUES (3, '菜品很好吃，配送速度快，服务态度好！', '2025-05-27 13:30:57', '2025-05-27 13:30:57');

-- 插入售后数据
INSERT INTO order_afters (order_id, user_id, type, reason, content, status, createTime, updateTime)
VALUES (3, 1, 'refund', '菜品有异味', '我点的红烧肉盖饭有点异味，希望能够退款', 'approved', '2025-05-27 13:31:23', '2025-05-27 13:31:23');

-- 插入购物车数据
INSERT INTO carts (user_id, createTime, updateTime)
VALUES (1, '2025-05-27 13:30:10', '2025-05-27 13:30:10');

-- 插入购物车商品数据
INSERT INTO cart_items (cart_id, item_id, item_type, quantity, price)
VALUES 
(1, 1, 'DISH', 2, 28.00),
(1, 2, 'DISH', 1, 32.00);

-- 插入优惠券数据
INSERT INTO coupons (user_id, name, description, type, value, min_amount, max_amount, start_time, end_time, createTime, updateTime)
VALUES 
(1, '新用户满减券', '满50减10元', 'fixed', 10.00, 50.00, 100.00, '2025-05-27 13:31:10', '2025-06-26 13:31:10', '2025-05-27 13:31:10', '2025-05-27 13:31:10'),
(1, '折扣券', '全场8折', 'percentage', 20.00, 0.00, 200.00, '2025-05-27 13:31:14', '2025-06-11 13:31:14', '2025-05-27 13:31:14', '2025-05-27 13:31:14');

-- 插入促销活动数据
INSERT INTO promotions (name, description, start_time, end_time, createTime, updateTime)
VALUES ('夏季特惠', '夏季清凉特惠，全场满100减20', '2025-05-27 13:31:19', '2025-07-26 13:31:19', '2025-05-27 13:31:19', '2025-05-27 13:31:19'); 