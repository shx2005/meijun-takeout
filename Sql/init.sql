-- 创建数据库
CREATE DATABASE IF NOT EXISTS mo_db;
USE mo_db;

-- 创建顾客表
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS dishes;
DROP TABLE IF EXISTS admins;
DROP TABLE IF EXISTS merchants;
DROP TABLE IF EXISTS orders;

-- 创建顾客表
CREATE TABLE customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    wechat_openid VARCHAR(255) NOT NULL UNIQUE,
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
    image_url VARCHAR(255),
    createTime TIMESTAMP,
    updateTime TIMESTAMP,
    merchant_id INT,
    FOREIGN KEY (merchant_id) REFERENCES merchants(id) ON DELETE CASCADE
);

-- 创建订单表
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    merchant_id INT,
    total DECIMAL(10, 2) NOT NULL,
    orderTime TIMESTAMP,
    status ENUM('pending', 'uncomfirmed', 'confirmed', 'delivering', 'completed', 'cancelled'),
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
    item_type ENUM('dish', 'set_meal') NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    price DECIMAL(10, 2),
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
    min_amount DECIMAL(10, 2),
    max_amount DECIMAL(10, 2),
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

-- 插入root管理员
INSERT INTO admins (uuid, username, password, role) VALUES ('adm-root','root', 'root', 'root');