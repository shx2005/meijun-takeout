// 使用方法示例
// 将以下代码添加到pages/index/index.vue的methods中

// 引入调试工具
// import { checkToken, setTestToken, testRequest } from '../../debug/token_checker.js';

methods: {
  // ... 已有代码 ...
  
  // 调试token并尝试请求
  testTokenAuth() {
    try {
      // 检查token
      const token = checkToken();
      
      // 如果token不存在，设置一个测试token
      if (!token) {
        setTestToken();
        checkToken(); // 再次检查
      }
      
      // 尝试请求菜品分页
      testRequest('http://localhost:8080/api/v1/dishes/page')
        .then(res => {
          if (res.statusCode === 200) {
            uni.$showMsg('请求成功', 'success');
          } else {
            uni.$showMsg('请求失败: ' + res.statusCode);
          }
        })
        .catch(err => {
          uni.$showMsg('请求异常');
          console.error('请求异常:', err);
        });
    } catch (error) {
      console.error('调试出错:', error);
    }
  }
}

// 添加一个测试按钮到模板:
// <button @click="testTokenAuth" type="default">测试认证</button> 