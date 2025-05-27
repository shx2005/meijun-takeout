-- 插入测试商家账户
INSERT INTO merchants (uuid, username, password, number, address, createTime, updateTime) 
VALUES ('mer-test-1', 'merchant1', '123456', '18888888888', '上海市浦东新区张江高科技园区', NOW(), NOW());

-- 插入测试员工账户
INSERT INTO employees (uuid, username, password, status, createTime, updateTime, merchant_id)
VALUES ('emp-test-1', 'employee1', '123456', 'active', NOW(), NOW(), 1); 