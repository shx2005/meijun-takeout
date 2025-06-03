-- 用于 OrderControllerTest 的测试用户数据
INSERT INTO customers (id, wechat_openid, uuid, username, password, balance, createTime, updateTime)
VALUES (1, 'test_openid_for_order_controller_test', 'test_uuid_for_order_controller_test', 'test_order_user', 'test_password', 0.00, NOW(), NOW())
ON DUPLICATE KEY UPDATE uuid = VALUES(uuid); -- 如果ID=1已存在，尝试更新以避免硬错误，但这取决于表是否有其他唯一键
-- 注意: ON DUPLICATE KEY UPDATE 可能需要表中除了主键外有其他唯一索引（如uuid或wechat_openid）才能有效触发。
-- 如果 customers 表中没有其他唯一键，并且 id=1 已存在，这个 INSERT 仍会失败。
-- 更安全的做法是在测试前清理相关表，或者确保 ID=1 不会被其他地方占用。
-- 对于 AUTO_INCREMENT 主键，直接插入ID需要小心。
-- 考虑到init.sql会执行，且里面有DROP TABLE, 应该没问题。 