-- 创建数据库
CREATE DATABASE IF NOT EXISTS `test-db`;
USE `test-db`;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS order_details;
DROP TABLE IF EXISTS order_comments;
DROP TABLE IF EXISTS after_sales;
DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS carts;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS dishes;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS stores;
DROP TABLE IF EXISTS coupons;
DROP TABLE IF EXISTS messages; 
DROP TABLE IF EXISTS promotions;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS merchants;
DROP TABLE IF EXISTS admins;
SET FOREIGN_KEY_CHECKS = 1;

-- 创建分类表
CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建顾客表
CREATE TABLE customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    wechat_openid VARCHAR(255) UNIQUE,
    uuid VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(30),
    password VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(255),
    balance DECIMAL(10, 2) DEFAULT 0.00,
    createTime TIMESTAMP,
    updateTime TIMESTAMP
);

-- 创建商家表
CREATE TABLE merchants (
    id INT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(50),
    password VARCHAR(255) NOT NULL,
    number VARCHAR(20),
    address VARCHAR(255),
    createTime TIMESTAMP,
    updateTime TIMESTAMP
);

-- 创建管理员表
CREATE TABLE admins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('root', 'admin') DEFAULT 'admin',
    createTime TIMESTAMP,
    updateTime TIMESTAMP
);

-- 创建店员表
CREATE TABLE employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(50),
    password VARCHAR(255) NOT NULL,
    status ENUM('active', 'inactive') DEFAULT 'active',
    createTime TIMESTAMP,
    updateTime TIMESTAMP,
    merchant_id INT,
    FOREIGN KEY (merchant_id) REFERENCES merchants(id) ON DELETE CASCADE
);

-- 创建商品表
CREATE TABLE dishes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    category_id INT NOT NULL,
    description TEXT,
    createTime TIMESTAMP,
    updateTime TIMESTAMP,
    merchant_id INT,
    FOREIGN KEY (merchant_id) REFERENCES merchants(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- 创建订单表
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    merchant_id INT,
    total DECIMAL(10, 2) NOT NULL,
    orderTime TIMESTAMP,
    status ENUM('pending', 'unconfirmed', 'confirmed', 'delivering', 'completed', 'cancelled'),
    pay_status ENUM('unpaid', 'paid' , 'refund') DEFAULT 'unpaid',
    createTime TIMESTAMP,
    updateTime TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE SET NULL,
    FOREIGN KEY (merchant_id) REFERENCES merchants(id)
);

-- 创建订单详情表
DROP TABLE IF EXISTS order_details;
CREATE TABLE order_details (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    order_id INT,
    item_id INT,
    item_type ENUM('dish', 'set_meal') NOT NULL,
    dish_flavor VARCHAR(255),
    quantity INT,
    unit DECIMAL(10, 2),
    total DECIMAL(10, 2),
    image VARCHAR(255),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES dishes(id) ON DELETE CASCADE
);

-- 创建订单评价表
DROP TABLE IF EXISTS order_comments;
CREATE TABLE order_comments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    comment TEXT,
    createTime TIMESTAMP,
    updateTime TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- 创建订单售后表
DROP TABLE IF EXISTS after_sales;
CREATE TABLE after_sales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    user_id INT,
    type ENUM('refund', 'replace', 'other'),
    reason TEXT,
    content TEXT,
    status ENUM('pending', 'approved', 'rejected'),
    createTime TIMESTAMP,
    updateTime TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES customers(id) ON DELETE CASCADE
);

-- 创建购物车表
DROP TABLE IF EXISTS carts;
CREATE TABLE carts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    createTime TIMESTAMP,
    updateTime TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES customers(id) ON DELETE CASCADE
);

-- 创建购物车条目表
DROP TABLE IF EXISTS cart_items;
CREATE TABLE cart_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cart_id INT,
    item_id INT,
    name VARCHAR(255) NOT NULL,
    item_type ENUM('dish', 'set_meal') NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE
);

-- 创建商店表
CREATE TABLE stores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    merchant_id INT,
    address VARCHAR(255),
    info  TEXT,
    createTime TIMESTAMP,
    updateTime TIMESTAMP,
    FOREIGN KEY (merchant_id) REFERENCES merchants(id) ON DELETE CASCADE
);

-- 创建优惠券表
CREATE TABLE coupons (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    type ENUM('fixed', 'percentage'),
    value DECIMAL(10, 2),
    min_amount DECIMAL(10, 2) default 0,
    max_amount DECIMAL(10, 2) default 1000,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    createTime TIMESTAMP,
    updateTime TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES customers(id) ON DELETE SET NULL
);

-- 创建促销信息表
CREATE TABLE promotions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    createTime TIMESTAMP,
    updateTime TIMESTAMP
);

-- 创建消息表
CREATE TABLE messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    employee_id INT,
    order_id INT,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    status ENUM('unread', 'read'),
    senderType INT,
    createTime TIMESTAMP,
    updateTime TIMESTAMP
);

-- 示例数据插入开始 (确保在所有 CREATE TABLE 之后执行)

-- 1. 插入商家
INSERT INTO merchants (uuid, username, password, address, number, createTime, updateTime)
VALUES ('test-merchant-uuid-1', 'test_merchant_1', 'password123', '123 Merchant Road', 'M001', NOW(), NOW());

-- 1.5 插入分类
INSERT INTO categories (name, description)
VALUES ('招牌推荐', '本店特色招牌菜品');

-- 2. 插入顾客
INSERT INTO customers (wechat_openid, uuid, username, password, avatar_url, balance, createTime, updateTime)
VALUES ('test-customer-openid-1', 'test-customer-uuid-1', 'test_customer_1', 'password123', 'http://example.com/avatar_customer.jpg', 100.00, NOW(), NOW());

-- 3. 插入店员 (假设 merchant_id=1 是上面插入的商家)
INSERT INTO employees (uuid, username, password, status, merchant_id, createTime, updateTime)
VALUES ('test-employee-uuid-1', 'test_employee_1', 'password123', 'active', 1, NOW(), NOW());

-- 4. 插入菜品 (假设 merchant_id=1 是上面插入的商家, category_id=1 是上面插入的分类)
INSERT INTO dishes (name, price, category_id, description, merchant_id, createTime, updateTime)
VALUES
('美味测试菜品A', 25.50, 1, '由测试商家1提供的招牌菜A', 1, NOW(), NOW()),
('香辣测试菜品B', 30.00, 1, '由测试商家1提供的特色香辣菜品B', 1, NOW(), NOW());

-- 7. 创建订单 (假设 customer_id=1, merchant_id=1)
INSERT INTO orders (customer_id, merchant_id, total, orderTime, status, pay_status, createTime, updateTime)
VALUES (1, 1, 51.00, NOW(), 'pending', 'unpaid', NOW(), NOW());

-- 8. 创建订单详情 (假设 order_id=1, dish_id=1)
INSERT INTO order_details (name, order_id, item_id, item_type, dish_flavor, quantity, unit, total, image)
VALUES ('美味测试菜品A', 1, 1, 'dish', '标准', 2, 25.50, 51.00, 'http://example.com/dish_a.jpg');

-- 9. 创建订单评价 (假设 order_id=1)
INSERT INTO order_comments (order_id, comment, createTime, updateTime)
VALUES (1, '菜品味道很棒，测试通过！', NOW(), NOW());

-- 10. 创建售后记录 (假设 order_id=1, user_id=1)
INSERT INTO after_sales (order_id, user_id, type, reason, content, status, createTime, updateTime)
VALUES (1, 1, 'other', '测试售后原因', '对测试订单的售后内容描述', 'pending', NOW(), NOW());

-- 11. 创建商店 (假设 merchant_id=1)
INSERT INTO stores (name, merchant_id, address, info, createTime, updateTime)
VALUES ('测试商家1的旗舰店', 1, '123 Merchant Road, Main Plaza', '欢迎光临测试旗舰店', NOW(), NOW());

-- 12. 创建优惠券 (假设 user_id=1)
INSERT INTO coupons (user_id, name, description, type, value, min_amount, start_time, end_time, createTime, updateTime)
VALUES (1, '新人专享5元优惠券', '首次下单用户可用的5元代金券', 'fixed', 5.00, 20.00, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), NOW(), NOW());

-- 13. 创建促销信息
INSERT INTO promotions (name, description, start_time, end_time, createTime, updateTime)
VALUES ('夏季新品大促销', '所有夏季新品享受8折优惠', NOW(), DATE_ADD(NOW(), INTERVAL 15 DAY), NOW(), NOW());

-- 14. 创建消息 (假设 user_id=1, employee_id=1, order_id=1, senderType=0表示顾客发送)
INSERT INTO messages (user_id, employee_id, order_id, title, content, status, senderType, createTime, updateTime)
VALUES (1, 1, 1, '关于订单#1的咨询', '你好，我想问下我的测试订单大概什么时候能送到？', 'unread', 0, NOW(), NOW());

-- 示例数据插入结束

-- 插入root管理员
INSERT INTO admins (uuid, username, password, role) VALUES ('adm-root','root', 'root', 'root');