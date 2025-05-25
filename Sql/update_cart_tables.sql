-- 更新cart_items表中的item_type枚举类型
USE mo_db;

-- 如果存在旧数据，进行类型转换
UPDATE cart_items SET item_type = 'DISH' WHERE item_type = 'dish';
UPDATE cart_items SET item_type = 'SET_MEAL' WHERE item_type = 'set_meal';

-- 修改表结构
ALTER TABLE cart_items MODIFY COLUMN item_type ENUM('DISH', 'SET_MEAL') NOT NULL; 