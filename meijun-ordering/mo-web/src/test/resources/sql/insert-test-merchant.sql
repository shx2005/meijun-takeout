-- 用于测试的商家数据
INSERT INTO merchants (id, uuid, username, password, number, address, createTime, updateTime)
VALUES (1, 'test_merchant_uuid_for_order_test', 'test_merchant_user_for_order_test', 'password', 'M12345', 'Test Merchant Address', NOW(), NOW())
ON DUPLICATE KEY UPDATE username = VALUES(username); 