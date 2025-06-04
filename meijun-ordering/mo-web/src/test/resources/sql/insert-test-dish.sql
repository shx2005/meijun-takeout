-- 用于 OrderControllerTest 的测试菜品数据
-- 确保 merchant_id=1 存在 (通过 insert-test-merchant.sql 插入)
INSERT INTO dishes (id, name, price, category_id, description, createTime, updateTime, merchant_id)
VALUES (1, '测试宫保鸡丁', 25.00, 1, '这是一道美味的测试宫保鸡丁', NOW(), NOW(), 1)
ON DUPLICATE KEY UPDATE name = VALUES(name); 