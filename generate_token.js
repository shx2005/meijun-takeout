// 解码和生成JWT token
const jwt = require('jsonwebtoken');

// 从命令行参数获取 id, username, identity
const id = process.argv[2];
const username = process.argv[3];
const identity = process.argv[4];

if (!id || !username || !identity) {
  console.error('错误: 请提供 id, username, 和 identity 作为命令行参数。');
  console.error('用法: node generate_token.js <id> <username> <identity>');
  process.exit(1);
}

try {
  // 创建与后端API验证兼容的payload
  const apiValidationPayload = {
    id: parseInt(id, 10), // 确保id是数字
    username: username,
    identity: identity,
    // 设置一个未来的过期时间
    exp: Math.floor(Date.now() / 1000) + (24 * 60 * 60) // 24小时后过期
  };
  
  // 使用假设的secret来签名token
  // 重要提示: 这个secretKey必须与后端验证时使用的密钥完全一致！
  const secretKey = 'meijun-takeout-secret'; // 这仍然是一个占位符/猜测值
  const newToken = jwt.sign(apiValidationPayload, secretKey, { algorithm: 'HS256' });
  
  // 仅输出token，以便shell脚本捕获
  console.log(newToken);
  
} catch (error) {
  console.error('生成token时出错:', error);
  process.exit(1);
} 