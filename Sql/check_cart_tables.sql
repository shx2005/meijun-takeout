-- 检查并创建购物车相关表
USE mo_db;

-- 检查carts表是否存在
CREATE TABLE IF NOT EXISTS carts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES customers(id) ON DELETE CASCADE
);

-- 检查cart_items表是否存在
CREATE TABLE IF NOT EXISTS cart_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cart_id INT,
    item_id INT,
    item_type ENUM('DISH', 'SET_MEAL') NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    price DECIMAL(10, 2),
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE
);

-- 输出表结构信息
SELECT 'carts表已存在或已创建' AS message;
SHOW COLUMNS FROM carts;

SELECT 'cart_items表已存在或已创建' AS message;
SHOW COLUMNS FROM cart_items; 