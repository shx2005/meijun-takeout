-- 使用数据库
USE mo_db;

-- 插入商家数据
INSERT INTO merchants (uuid, username, password, number, address, createTime, updateTime) VALUES
('merchant-001', '美食元素餐厅', 'e10adc3949ba59abbe56e057f20f883e', '13800138000', '上海市浦东新区张江高科技园区', NOW(), NOW()),
('merchant-002', '家乡菜馆', 'e10adc3949ba59abbe56e057f20f883e', '13900139000', '上海市徐汇区漕河泾开发区', NOW(), NOW()),
('merchant-003', '品味轩', 'e10adc3949ba59abbe56e057f20f883e', '13700137000', '上海市黄浦区人民广场', NOW(), NOW());

-- 插入管理员数据（root管理员已在init.sql中创建）
INSERT INTO admins (uuid, username, password, role, createTime, updateTime) VALUES
('adm-001', 'admin1', 'e10adc3949ba59abbe56e057f20f883e', 'admin', NOW(), NOW()),
('adm-002', 'admin2', 'e10adc3949ba59abbe56e057f20f883e', 'admin', NOW(), NOW());

-- 插入顾客数据
INSERT INTO customers (wechat_openid, uuid, username, password, avatar_url, balance, createTime, updateTime) VALUES
('openid-001', 'user-001', '张三', 'e10adc3949ba59abbe56e057f20f883e', '/static/images/avatar1.jpg', 100.00, NOW(), NOW()),
('openid-002', 'user-002', '李四', 'e10adc3949ba59abbe56e057f20f883e', '/static/images/avatar2.jpg', 200.00, NOW(), NOW()),
('openid-003', 'user-003', '王五', 'e10adc3949ba59abbe56e057f20f883e', '/static/images/avatar3.jpg', 150.00, NOW(), NOW()),
('openid-004', 'user-004', '赵六', 'e10adc3949ba59abbe56e057f20f883e', '/static/images/avatar4.jpg', 50.00, NOW(), NOW()),
('openid-005', 'user-005', '钱七', 'e10adc3949ba59abbe56e057f20f883e', '/static/images/avatar5.jpg', 300.00, NOW(), NOW());

-- 插入员工数据
INSERT INTO employees (uuid, username, password, status, createTime, updateTime, merchant_id) VALUES
('emp-001', '员工1', 'e10adc3949ba59abbe56e057f20f883e', 'active', NOW(), NOW(), 1),
('emp-002', '员工2', 'e10adc3949ba59abbe56e057f20f883e', 'active', NOW(), NOW(), 1),
('emp-003', '员工3', 'e10adc3949ba59abbe56e057f20f883e', 'inactive', NOW(), NOW(), 1),
('emp-004', '员工4', 'e10adc3949ba59abbe56e057f20f883e', 'active', NOW(), NOW(), 2),
('emp-005', '员工5', 'e10adc3949ba59abbe56e057f20f883e', 'active', NOW(), NOW(), 3);

-- 插入门店数据
INSERT INTO stores (name, merchant_id, address, info, createTime, updateTime) VALUES
('美食元素张江店', 1, '上海市浦东新区张江高科技园区', '提供各种家常菜和特色菜品', NOW(), NOW()),
('美食元素徐家汇店', 1, '上海市徐汇区徐家汇商圈', '环境优雅，菜品多样', NOW(), NOW()),
('家乡菜馆总店', 2, '上海市徐汇区漕河泾开发区', '正宗家乡风味', NOW(), NOW()),
('品味轩旗舰店', 3, '上海市黄浦区人民广场', '高档中式料理', NOW(), NOW());

-- 插入菜品分类数据
-- 在dishes表中使用了category_id，但没有对应的分类表，这里创建一个分类表
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    sort INT NOT NULL,
    status INT DEFAULT 1,
    createTime TIMESTAMP,
    updateTime TIMESTAMP
);

-- 插入菜品分类数据
INSERT INTO categories (id, type, name, sort, status, createTime, updateTime) VALUES
(1, 1, '家常菜', 1, 1, NOW(), NOW()),
(2, 1, '盖饭', 2, 1, NOW(), NOW()),
(3, 1, '米饭', 3, 1, NOW(), NOW()),
(4, 1, '特色菜', 4, 1, NOW(), NOW()),
(5, 1, '干锅', 5, 1, NOW(), NOW()),
(6, 1, '家常菜系列', 6, 1, NOW(), NOW()),
(7, 1, '汤菜', 7, 1, NOW(), NOW()),
(8, 1, '素菜系列', 8, 1, NOW(), NOW()),
(9, 2, '套餐', 9, 1, NOW(), NOW());

-- 插入菜品数据
INSERT INTO dishes (name, price, category_id, description, image_url, createTime, updateTime, merchant_id) VALUES
('鱼香肉丝', 28.00, 1, '主料：猪肉、胡萝卜、青椒、木耳', '/static/images/dish1.jpg', NOW(), NOW(), 1),
('宫保鸡丁', 26.00, 1, '主料：鸡胸肉、花生米、黄瓜、胡萝卜', '/static/images/dish2.jpg', NOW(), NOW(), 1),
('红烧排骨', 32.00, 1, '主料：猪排骨、土豆、胡萝卜', '/static/images/dish3.jpg', NOW(), NOW(), 1),
('麻婆豆腐', 22.00, 2, '主料：豆腐、肉末、豆瓣酱', '/static/images/dish4.jpg', NOW(), NOW(), 1),
('干锅土豆片', 28.00, 5, '主料：土豆、辣椒、木耳、肉片', '/static/images/dish5.jpg', NOW(), NOW(), 1),
('水煮肉片', 32.00, 4, '主料：猪肉、豆芽、白菜', '/static/images/dish6.jpg', NOW(), NOW(), 1),
('蒜蓉蒸茄子', 18.00, 8, '主料：茄子、蒜蓉', '/static/images/dish7.jpg', NOW(), NOW(), 1),
('白米饭', 2.00, 3, '精选东北大米', '/static/images/dish8.jpg', NOW(), NOW(), 1),
('番茄蛋花汤', 15.00, 7, '主料：番茄、鸡蛋', '/static/images/dish9.jpg', NOW(), NOW(), 1),
('回锅肉', 28.00, 1, '主料：五花肉、青椒', '/static/images/dish10.jpg', NOW(), NOW(), 1),
('青椒土豆丝', 16.00, 8, '主料：土豆、青椒', '/static/images/dish11.jpg', NOW(), NOW(), 1),
('干煸四季豆', 18.00, 8, '主料：四季豆、辣椒', '/static/images/dish12.jpg', NOW(), NOW(), 1),
-- 增加更多菜品
('香菇青菜', 16.00, 8, '主料：香菇、青菜', '/static/images/dish13.jpg', NOW(), NOW(), 1),
('辣子鸡', 32.00, 4, '主料：鸡肉、干辣椒', '/static/images/dish14.jpg', NOW(), NOW(), 1),
('鱼香茄子', 22.00, 8, '主料：茄子、胡萝卜、木耳', '/static/images/dish15.jpg', NOW(), NOW(), 1),
('糖醋排骨', 36.00, 1, '主料：猪排骨、菠萝', '/static/images/dish16.jpg', NOW(), NOW(), 2),
('蒜蓉西兰花', 18.00, 8, '主料：西兰花、蒜蓉', '/static/images/dish17.jpg', NOW(), NOW(), 2),
('红烧肉', 38.00, 1, '主料：五花肉、香料', '/static/images/dish18.jpg', NOW(), NOW(), 2),
('酸菜鱼', 42.00, 4, '主料：鱼肉、酸菜', '/static/images/dish19.jpg', NOW(), NOW(), 2),
('黄焖鸡米饭', 25.00, 2, '主料：鸡肉、土豆、香菇，配米饭一份', '/static/images/dish20.jpg', NOW(), NOW(), 3),
('红烧牛肉面', 28.00, 2, '主料：牛肉、面条', '/static/images/dish21.jpg', NOW(), NOW(), 3),
('鱼香肉丝套餐', 32.00, 9, '主料：猪肉丝、胡萝卜、青椒，配米饭一份', '/static/images/dish22.jpg', NOW(), NOW(), 3);

-- 插入购物车数据
INSERT INTO carts (user_id, createTime, updateTime) VALUES
(1, NOW(), NOW()),
(2, NOW(), NOW()),
(3, NOW(), NOW()),
(4, NOW(), NOW()),
(5, NOW(), NOW());

-- 插入购物车条目数据
INSERT INTO cart_items (cart_id, item_id, item_type, quantity, price) VALUES
(1, 1, 'dish', 2, 28.00),
(1, 8, 'dish', 1, 2.00),
(2, 3, 'dish', 1, 32.00),
(2, 9, 'dish', 2, 15.00),
(3, 5, 'dish', 1, 28.00),
(3, 7, 'dish', 1, 18.00),
(4, 2, 'dish', 2, 26.00),
(5, 4, 'dish', 1, 22.00);

-- 插入订单数据
INSERT INTO orders (customer_id, merchant_id, total, status, createTime, updateTime) VALUES
(1, 1, 58.00, 'completed', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2, 1, 62.00, 'completed', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
(3, 1, 46.00, 'completed', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
(1, 2, 36.00, 'completed', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(4, 1, 52.00, 'pending', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(5, 3, 32.00, 'pending', NOW(), NOW());

-- 插入订单详情数据
INSERT INTO order_details (order_id, dish_id, quantity, price, createTime, updateTime) VALUES
(1, 1, 2, 28.00, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
(1, 8, 1, 2.00, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2, 3, 1, 32.00, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2, 9, 2, 15.00, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
(3, 5, 1, 28.00, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
(3, 7, 1, 18.00, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
(4, 16, 1, 36.00, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(5, 2, 2, 26.00, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(6, 21, 1, 28.00, NOW(), NOW());

-- 插入订单评价数据
INSERT INTO order_comments (order_id, comment, createTime, updateTime) VALUES
(1, '菜品味道很好，服务也很周到', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2, '红烧排骨很入味，汤也很好喝', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
(3, '干锅土豆片非常好吃，下次还会再点', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
(4, '糖醋排骨的味道很不错', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY));

-- 插入售后数据
INSERT INTO order_afters (order_id, user_id, type, reason, content, status, createTime, updateTime) VALUES
(5, 4, 'refund', '送餐太慢', '等了一个小时还没送到', 'pending', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 插入优惠券数据
INSERT INTO coupons (user_id, name, description, type, value, min_amount, max_amount, start_time, end_time, createTime, updateTime) VALUES
(1, '满30减5', '订单满30元减5元', 'fixed', 5.00, 30.00, 5.00, DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), NOW(), NOW()),
(1, '满50减10', '订单满50元减10元', 'fixed', 10.00, 50.00, 10.00, DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), NOW(), NOW()),
(2, '满30减5', '订单满30元减5元', 'fixed', 5.00, 30.00, 5.00, DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), NOW(), NOW()),
(3, '满100减15', '订单满100元减15元', 'fixed', 15.00, 100.00, 15.00, DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), NOW(), NOW()),
(4, '85折优惠', '订单享85折优惠', 'percentage', 0.85, 0.00, 50.00, DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), NOW(), NOW()),
(5, '满30减5', '订单满30元减5元', 'fixed', 5.00, 30.00, 5.00, DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), NOW(), NOW());

-- 插入促销信息
INSERT INTO promotions (name, description, start_time, end_time, createTime, updateTime) VALUES
('新用户立减10元', '新注册用户下单立减10元', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_ADD(NOW(), INTERVAL 15 DAY), NOW(), NOW()),
('周末特惠', '周末期间全场菜品9折', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), NOW(), NOW()),
('午餐特惠', '工作日11:00-14:00点餐享8.5折优惠', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_ADD(NOW(), INTERVAL 20 DAY), NOW(), NOW()); 