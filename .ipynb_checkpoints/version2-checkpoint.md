4.1.1.4 接口关系
Login类与以下组件有交互关系：
- Register类：用户可通过登录页面跳转至注册页面
- Index类：登录成功后跳转至首页
- 后端认证服务：进行用户验证

![登录界面接口关系图](./uml_diagrams/images/login_interface.png)

4.1.2 注册Register类 

4.1.2.4 接口关系
Register类与以下组件有交互关系：
- Login类：用户可通过注册页面返回登录页面
- 后端认证服务：处理用户注册请求

![注册界面接口关系图](./uml_diagrams/images/register_interface.png)

4.2 首页页面 

4.2.1.4 接口关系
Index类与以下组件有交互关系：
- DishDetail类：点击菜品跳转到菜品详情页面
- Cart类：点击购物车图标跳转到购物车页面
- My类：点击个人中心按钮跳转到个人中心页面
- 后端服务：获取菜品分类、菜品列表、购物车数据

![首页界面接口关系图](./uml_diagrams/images/index_interface.png)

4.2.2 菜品详情DishDetail类 

4.2.2.4 接口关系
DishDetail类与以下组件有交互关系：
- Index类：返回首页
- Cart类：点击购物车图标跳转到购物车页面
- 后端服务：获取菜品详情和评价数据，管理购物车

![菜品详情界面接口关系图](./uml_diagrams/images/dish_detail_interface.png)

4.3 个人中心页面 

4.3.1.4 接口关系
My类与以下组件有交互关系：
- UserInfo类：点击头像进入用户信息编辑页面
- OrderList类：点击订单管理进入订单列表页面
- Address类：点击地址管理进入地址列表页面
- Setting类：点击设置进入系统设置页面
- 后端服务：获取用户基本信息和订单数据

![个人中心界面接口关系图](./uml_diagrams/images/my_interface.png)

4.3.2 用户信息UserInfo类
4.3.2.4 接口关系
UserInfo类与以下组件有交互关系：
- My类：返回个人中心页面
- 后端服务：获取和更新用户信息

![用户信息界面接口关系图](./uml_diagrams/images/user_info_interface.png)

4.3.3 设置Setting类
4.3.3.1 界面预览
设置页面提供系统相关的配置选项，包括通知设置、隐私设置、清除缓存、版本信息和退出登录功能。界面采用列表形式展示各个设置项，点击可进入详细设置或执行相应操作。

4.3.3.2 数据成员
- notificationEnabled - 通知开关状态
- privacySettings - 隐私设置选项
- cacheSize - 缓存大小
- appVersion - 应用版本号
- isLoading - 加载状态标识

4.3.3.3 主要函数
函数名	功能	URL	请求方式	参数
loadSettings	加载设置	/api/v1/user/settings	GET	
toggleNotification	切换通知状态	/api/v1/user/settings/notification	PUT	enabled
updatePrivacy	更新隐私设置	/api/v1/user/settings/privacy	PUT	settings
clearCache	清除缓存			
logout	退出登录	/api/v1/auth/logout	POST	

4.3.3.4 接口关系
Setting类与以下组件有交互关系：
- My类：返回个人中心页面
- Login类：退出登录后跳转到登录页面
- 后端服务：更新用户设置和处理退出登录请求

![设置界面接口关系图](./uml_diagrams/images/setting_interface.png)

4.4 购物车页面
4.4.1 购物车Cart类
4.4.1.1 界面预览
购物车页面展示用户选择的所有商品，包括商品图片、名称、规格、价格和数量。用户可以在此页面调整商品数量或删除商品。页面底部显示商品总价和结算按钮。

4.4.1.2 数据成员
- cartItems - 购物车商品列表
- totalPrice - 商品总价
- isAllSelected - 全选状态
- isEditing - 编辑模式标识
- isLoading - 加载状态标识

4.4.1.3 主要函数
函数名	功能	URL	请求方式	参数
loadCartItems	加载购物车商品	/api/v1/cart/list	GET	
updateCartItem	更新商品数量	/api/v1/cart/update	PUT	id、quantity
deleteCartItem	删除购物车商品	/api/v1/cart/delete	DELETE	id
clearCart	清空购物车	/api/v1/cart/clear	DELETE	
toggleSelectAll	切换全选状态			
toggleEdit	切换编辑模式			
handleCheckout	前往结算			
saveToLocalStorage	保存到本地存储			

4.4.1.4 接口关系
Cart类与以下组件有交互关系：
- Index类：返回首页继续购物
- AddOrder类：点击结算按钮跳转到订单确认页面
- 后端服务：管理购物车数据

![购物车界面接口关系图](./uml_diagrams/images/cart_interface.png)

4.4.2 订单确认AddOrder类
4.4.2.4 接口关系
AddOrder类与以下组件有交互关系：
- Cart类：返回购物车页面
- Address类：选择地址跳转到地址列表页面
- PayConfirm类：提交订单后跳转到支付确认页面
- 后端服务：提交订单数据

![订单确认界面接口关系图](./uml_diagrams/images/add_order_interface.png)

4.5 订单管理页面
4.5.1.4 接口关系
OrderList类与以下组件有交互关系：
- My类：返回个人中心页面
- OrderDetail类：点击订单项跳转到订单详情页面
- 后端服务：获取订单列表数据

![订单列表界面接口关系图](./uml_diagrams/images/order_list_interface.png)

4.5.2 订单详情OrderDetail类
4.5.2.4 接口关系
OrderDetail类与以下组件有交互关系：
- OrderList类：返回订单列表页面
- RefundPage类：申请退款跳转到退款申请页面
- PayConfirm类：支付订单跳转到支付确认页面
- 后端服务：获取和更新订单数据

![订单详情界面接口关系图](./uml_diagrams/images/order_detail_interface.png)

4.5.3 订单评价类
4.5.3.4 接口关系
OrderEvaluate类与以下组件有交互关系：
- OrderDetail类：返回订单详情页面
- 后端服务：提交评价数据

![订单评价界面接口关系图](./uml_diagrams/images/order_evaluate_interface.png)

4.6 地址管理页面
4.6.1.4 接口关系
Address类与以下组件有交互关系：
- AddOrder类：返回订单确认页面并传递选择的地址
- AddressEdit类：点击编辑或添加新地址跳转到地址编辑页面
- 后端服务：获取和管理地址数据

![地址列表界面接口关系图](./uml_diagrams/images/address_interface.png)

4.6.2 地址编辑AddressEdit类
4.6.2.4 接口关系
AddressEdit类与以下组件有交互关系：
- Address类：保存后返回地址列表页面
- 后端服务：保存和更新地址数据

![地址编辑界面接口关系图](./uml_diagrams/images/address_edit_interface.png)

4.7 支付页面
4.7.1 支付确认PayConfirm类
4.7.1.1 界面预览
支付确认页面显示订单金额和可用的支付方式选项，如微信支付、支付宝等。用户可以选择支付方式并点击确认支付按钮。页面顶部有倒计时提示支付时间限制。

4.7.1.2 数据成员
- orderId - 订单ID
- orderInfo - 订单信息
- payAmount - 支付金额
- selectedPayMethod - 选中的支付方式
- payMethods - 支付方式列表
- countdown - 支付倒计时
- isLoading - 加载状态标识

4.7.1.3 主要函数
函数名	功能	URL	请求方式	参数
getOrderInfo	获取订单信息	/api/v1/order/detail	GET	orderId
loadPayMethods	加载支付方式	/api/v1/payment/methods	GET	
selectPayMethod	选择支付方式			method
confirmPay	确认支付	/api/v1/payment/pay	POST	orderId、method
startCountdown	启动倒计时			
cancelOrder	取消订单	/api/v1/order/cancel	POST	orderId

4.7.1.4 接口关系
PayConfirm类与以下组件有交互关系：
- OrderDetail类：返回订单详情页面
- PaySuccess类：支付成功后跳转到支付成功页面
- 后端服务：处理支付请求和更新订单状态

![支付确认界面接口关系图](./uml_diagrams/images/pay_confirm_interface.png)

4.7.2 支付成功PaySuccess类
4.7.2.1 界面预览
支付成功页面显示支付完成的状态图标和提示信息，以及订单金额。页面提供查看订单和返回首页两个操作按钮。

4.7.2.2 数据成员
- orderId - 订单ID
- orderAmount - 订单金额
- payTime - 支付时间

4.7.2.3 主要函数
函数名	功能	URL	请求方式	参数
loadOrderInfo	加载订单信息	/api/v1/order/detail	GET	orderId
viewOrder	查看订单			orderId
backToIndex	返回首页			
formatDate	格式化日期			date

4.7.2.4 接口关系
PaySuccess类与以下组件有交互关系：
- OrderDetail类：点击查看订单按钮跳转到订单详情页面
- Index类：点击返回首页按钮跳转到首页
- 后端服务：获取订单信息

![支付成功界面接口关系图](./uml_diagrams/images/pay_success_interface.png)

5 数据说明