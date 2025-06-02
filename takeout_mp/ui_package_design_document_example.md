文档编号：   CC-JS-003-2                   		 	


项目名称

Chess&Chatting
象棋聊天室系统

界面包CSCI详细设计说明书






第八小组（胡林伟小组）
（本文档由李天夫、肖敬和张郭旺共同完成）






2022年12月

 
修改记录
版本	变更原因	变更内容简述	编制/修订者	批准者	发布日期
V1.0	创建以及制定目录		李天夫		2022.12.14
V1.1	完成前三部分内容		张郭旺		2022.12.15
V1.2	完成登录注册设计		张郭旺		2022.12.15
V1.3	完成首页及基本信息设计		李天夫		2022.12.15
V1.4	完成好友私聊设计		李天夫		2022.12.16
V1.5	完成单机演练设计		肖敬		2022.12.16
V1.6	完成象棋房间设计		肖敬		2022.12.17
V1.7	完成初稿		张郭旺		2022.12.17
V1.8	修改初稿		肖敬		2022.12.18
					
					
 
界面包CSCI详细设计说明书	1
2022年12月	1
1 范围	8
1.1 标识	8
1.2 CSCI 概述	8
1.3 文档概述	9
2 引用的文档	10
3 前端包详细设计	10
3.1 结构	10
3.1.1 详细结构	10
3.1.1.1类结构	10
3.1.2 接口关系	11
3.2运行组织	12
3.3 性能要求	12
3.4 设计限制和约束	13
4 前端包类详细设计	14
4.1 注册登录页面	14
4.1.1登录Login类	14
4.1.2 注册Register类	15
4.2 首页页面	17
4.2.1 首页Index类	17
4.3 个人中心页面	19
4.3.1 登出Exit类	19
4.3.2 修改密码ChangePwd类	20
4.3.3 修改昵称ChangeName类	21
4.3.4 查看基本信息Info类	23
4.3.5 查看历史记录History类	24
4.4 好友功能页面	26
4.4.1 好友列表FriendList类	26
4.4.2 好友申请列表FriendRequestList类	27
4.4.3 添加好友addFriend类	29
4.4.4 删除好友deleteFriend类	30
4.4.5 好友聊天friendChat类	31
4.5 单机演练页面	33
4.5.1 单机象棋SinglePlayerChess类	33
4.5.2 游戏重置GameReset类	35
4.6 象棋功能页面	37
4.6.1 房间列表RoomList类	37
4.6.2 创建房间NewRoom类	39
4.6.3 房间信息roomInfo类	41
4.6.4 房间聊天roomChat类	43
4.6.5 成员列表memberList类	45
4.6.6 挑战列表challengeList类	47
4.6.7 房间下棋roomChess类	48
4.6.8 房间观战roomWatch类	50
5 数据说明	52
5.1 内部数据元素	52
5.2 外部接口的数据元素	52
6 需求可追踪性	52
 
1 范围
1.1 标识
【系统背景】
系统标识符：Chess&Chatting
系统名称  ：象棋聊天室系统（C&C）
【适用的CSCI】
标识符：CSCI-2
名称  ：界面UI包
1.2 CSCI 概述
    【系统功能概述】
“Chess&Chatting”应用是一个可以让用户体验与好友或陌生人对战象棋的软件。在这个应用中，你可以添加好友并邀请好友切磋象棋，或者直接开房间等待玩家进入房间开启对战，房间中也可以观战当前对局。除了象棋对战之外，也可以用于与好友聊天。
总的来说，本系统旨在完成并且很好的实现以下的功能：
用户注册
用户登录
修改密码
修改昵称
搜索用户
添加好友
删除好友
查询好友信息
查询历史记录
创建房间
查询房间密码
搜索房间
进入房间
退出房间
好友私聊
房间群聊
转让房主
走子
悔棋
认输
求和
象棋大厅展示房间
音效和背景音乐

【CSCI层次】
用包图表示本CSCI在系统中的位置（顶层系统包图）。
 
图1.2-1 C&C系统顶层包图
1.3 文档概述
【用途】
本CSCI详细说明书用于描述C&C系统的CSCI-界面UI包的详细设计方案，本说明书以《系统需求规格说明书》和《概要设计说明书》作为依据，本说明书是代码实现的依据，也作为单元测试的重要依据。本概要设计说明书的阅读对象为：开发工程师，测试工程师，CM工程师，QA工程师，MA工程师。
【内容】
本文档的主题内容如下：
描述CSCI-1的功能和作用；
2 引用的文档
按下列格式列出本文档引用到的所有文档。（《系统需求规格说明书》和《系统设计说明书》）
【序号】 【文档编号】 【文档名称】
  01	   CC-JS-001  《Chess & Chatting象棋聊天室项目系统需求规格说明书》
02      CC-JS-002  《Chess & Chatting象棋聊天室项目系统概要设计说明书》
3 前端包详细设计
3.1 结构
3.1.1 详细结构
3.1.1.1类结构
定义CSCI的类结构（用类图表示本CSCI的各个类之间的关系）；
 
3.1.2 接口关系
描述本CSCI的结构类和其他包之间的关系；
 

3.2运行组织
运行时的和其他包之间的简要流程和在设计上的考虑等（可用顺序图或/和活动图）。 
 

3.3 性能要求
    提供对于CSCI在运行时必须满足的性能要求。例如：内存和进程时间（给出计算单位）要求、资源占用率或响应时间等。
无
3.4 设计限制和约束
   【限制条件】
	硬件条件：
	[CPU]	intel core T4400	 
	[内存]	至少4GB	
	[显卡]	支持directx11的兼容显卡	
	[硬盘]	至少1024M可用空间
	软件条件：
	相关的编译软件：VSCode
   【编程语言】
    CSS + JavaScript + jQuery
   【编译/开发环境】
    编译器： VSCode
    编译环境：VSCode
    运行库：Navicat
   【运行平台】
硬件平台：计算机（PC）
软件平台：Windows10
4 前端包类详细设计
4.1 注册登录页面
4.1.1登录Login类
4.1.1.1 界面预览
 
4.1.1.2 数据成员
form——表单
h1——标题
input——输入框
button——登录按钮
a——跳转到注册页面的链接
p——描述信息
4.1.1.3 主要函数
函数名	功能	URL	请求方式	参数
Login	登录	/login	Post	usrId、pwd
4.1.1.4 接口关系
 
4.1.2 注册Register类
4.1.2.1 界面预览
 
4.1.2.2 数据成员
form——表单
h1——标题
input——输入框
button——注册按钮和清空按钮
a——跳转到注册页面的链接
p——描述信息
4.1.2.3 主要函数
函数名	功能	URL	请求方式	参数
Register	注册	/register	Post	uName、pwd
4.1.2.4 接口关系
 
4.2 首页页面
4.2.1 首页Index类
4.2.1.1 界面预览
 
4.2.1.2 数据成员
div——首页分块
span——点击区域
p——描述文字
img——图片
4.2.1.3 主要函数
函数名	功能	URL	请求方式	参数
SelfInfo	跳转到个人中心			
FriendInfo	跳转到好友聊天			
RoomInfo	跳转到象棋大厅			
Chess	跳转到单机演练			
4.2.1.4 接口关系
 
4.3 个人中心页面
4.3.1 登出Exit类
4.3.1.1 界面预览
 
4.3.1.2 数据成员
div——点击区域块
4.3.1.3 主要函数
函数名	功能	URL	请求方式	参数
Exit	退出登录	/exit	Put	uId
4.3.1.4 接口关系
 
4.3.2 修改密码ChangePwd类
4.3.2.1 界面预览
 
4.3.2.2 数据成员
h3——标题
input——输入框
button——提交按钮
4.3.2.3 主要函数
函数名	功能	URL	请求方式	参数
ChangePwd	修改密码	/revisePwd	Put	uId、newPwd、
4.3.2.4 接口关系
 
4.3.3 修改昵称ChangeName类
4.3.3.1 界面预览
 
4.3.3.2 数据成员
h3——标题
input——输入框
button——提交按钮
4.3.3.3 主要函数
函数名	功能	URL	请求方式	参数
ChangeName	修改昵称	/reviseName	Put	uId、newName、
4.3.3.4 接口关系
 
4.3.4 查看基本信息Info类
4.3.4.1 界面预览
 
4.3.4.2 数据成员
h3——标题
p——内容
4.3.4.3 主要函数
无
4.3.4.4 接口关系
 
4.3.5 查看历史记录History类
4.3.5.1 界面预览
 
4.3.5.2 数据成员
h3——标题
div——放置表格的块
table——放置内容的表格
4.3.5.3 主要函数
无
4.3.5.4 接口关系
 
4.4 好友功能页面
4.4.1 好友列表FriendList类
4.4.1.1 界面预览
 
4.4.1.2 数据成员
div——块元素
li——绑定点击事件的无序列表
4.4.1.3 主要函数
函数名	功能	URL	请求方式	参数
ManageFriend	进入好友管理界面			
FriendChat	进入好友聊天界面			
4.4.1.4 接口关系
 
4.4.2 好友申请列表FriendRequestList类
4.4.2.1 界面预览
 
4.4.2.2 数据成员
h3——标题
span——展示信息
button——同意或拒绝按钮
4.4.2.3 主要函数
函数名	功能	URL	请求方式	参数
AcceptFriend	同意申请	/ AcceptFriendApply	Post	aplId
RejectFriend	拒绝申请	/ RejectFriendApply	Post	aplId
4.4.2.4 接口关系
 
4.4.3 添加好友addFriend类
4.4.3.1 界面预览
 
4.4.3.2 数据成员
h3——标题
input——输入框
button——提交按钮
4.4.3.3 主要函数
函数名	功能	URL	请求方式	参数
AddFriend	申请添加好友	/ postFriendApply	Post	uId、fId
4.4.3.4 接口关系
 
4.4.4 删除好友deleteFriend类
4.4.4.1 界面预览
 
4.4.4.2 数据成员
h3——标题
input——输入框
button——提交按钮
4.4.4.3 主要函数
函数名	功能	URL	请求方式	参数
DeleteFriend	删除好友	/ deleteFriendShip	Delete	uId、fId
4.4.4.4 接口关系
 
4.4.5 好友聊天friendChat类
4.4.5.1 界面预览
 
4.4.5.2 数据成员
span——展示信息
button——按钮
p——消息
textarea——输入框
4.4.5.3 主要函数
函数名	功能	URL	请求方式	参数
DeleteFriend	删除好友	/ deleteFriendShip	Delete	uId、fId
Clear	清空消息			
Challenge	申请挑战	/fChat/Id/fId	Post	
ExitChat	退出聊天			
Send	发送消息	/fChat/Id/fId	Post	
Receive	接受消息	/fChat/Id/fId	Get	
4.4.5.4 接口关系
 
4.5 单机演练页面
4.5.1 单机象棋SinglePlayerChess类
4.5.1.1 界面预览
 
4.5.1.2 数据成员
table——棋盘，表格
div——棋子

4.5.1.3 主要函数
无
4.5.1.4 接口关系

 

4.5.2 游戏重置GameReset类
4.5.2.1 界面预览
 
4.5.2.2 数据成员
h1——游戏开始或重置功能及标识符（标注当前下棋方）

4.5.2.3 主要函数
无
4.5.2.4 接口关系
 

4.6 象棋功能页面
4.6.1 房间列表RoomList类
4.6.1.1 界面预览
 
4.6.1.2 数据成员
div——点击区域
input——输入需要搜索房间信息和提交信息
span——房间名

4.6.1.3 主要函数
无
4.6.1.4 接口关系

 



4.6.2 创建房间NewRoom类
4.6.2.1 界面预览
 
4.6.2.2 数据成员
p——提示
div——表单
input——输入房间名和密码
button——提交房间信息

4.6.2.3 主要函数
函数名	功能	URL	请求方式	参数
addRoom	创建房间	/createRoom	POST	owner
rName
rPwd

enterRoom	进入房间	/getRoomInfo	GET	rId

4.6.2.4 接口关系
 

4.6.3 房间信息roomInfo类
4.6.3.1 界面预览
 
4.6.3.2 数据成员
p——房间名、房间ID
input——离开房间
span——房主和挑战者
4.6.3.3 主要函数
函数名	功能	URL	请求方式	参数
leaveRoom	退出房间	/{rId}/leaveRoom	Delete	ID
rId
paintItemL	渲染界面	/getRoomInfo	GET	ID
rId

4.6.3.4 接口关系
 


4.6.4 房间聊天roomChat类
4.6.4.1 界面预览
 
4.6.4.2 数据成员
div——聊天表单
p——聊天内容
4.6.4.3 主要函数
函数名	功能	URL	请求方式	参数
rcSendMsg	发送消息	/sendMsg	POST	rId
ID
msg
	paintItemR	渲染界面	/getMsg	GET	rId
ID

4.6.4.4 接口关系

 
4.6.5 成员列表memberList类
4.6.5.1 界面预览
   
4.6.5.2 数据成员
div——成员名
input——添加好友、转让房主
4.6.5.3 主要函数
函数名	功能	URL	请求方式	参数
addFriendHere
	添加好友	/postFriendApply	POST	ID
memIndex
transferOwner	转让房主	/transferOwner	PUT	ID
rId
memIndex

4.6.5.4 接口关系
 
4.6.6 挑战列表challengeList类
4.6.6.1 界面预览
   
4.6.6.2 数据成员
input——发起挑战按钮、接受挑战按钮
div——挑战者ID
4.6.6.3 主要函数
函数名	功能	URL	请求方式	参数
paintItemR
	发起挑战	/postMatchApply	POST	ID

pickUpChallenge	接受挑战	/acceptMatchApply	PUT	rId
aplIndex

4.6.6.4 接口关系
 

4.6.7 房间下棋roomChess类
4.6.7.1 界面预览
 
4.6.7.2 数据成员
div——棋盘、棋子
table——表格
p——认输、求和、悔棋

4.6.7.3 主要函数
函数名	功能	URL	请求方式	参数
postRepentance	悔棋请求	/postRepentance	POST	rId
postSurrender	认输	/postSurrender	POST	rId
postDraw	求和	/postDraw	POST	rId
acceptDraw	同意平局	/acceptDraw	PUT	rId
rejectDraw	拒绝平局	/rejectDraw	PUT	rId

acceptRepentance	接受悔棋	/acceptRepentance	PUT	rId
rejectRepentance	拒绝悔棋	/rejectRepentance	PUT	rId

4.6.7.4 接口关系
 


4.6.8 房间观战roomWatch类
4.6.8.1 界面预览
 
4.6.8.2 数据成员
div——观战标识、棋盘棋子
table——表格

4.6.8.3 主要函数
函数名	功能	URL	请求方式	参数
init1	更新棋盘	无	无	roomObj.chessboard

4.6.8.4 接口关系
 


5 数据说明
5.1 内部数据元素
无。
5.2 外部接口的数据元素
无。
6 需求可追踪性
    给出CSCI中的软件需求规格和接口需求规格在本包各类中被分配的映像关系（该类有可能部分参与这些需求规格，可以某需求规格由多个类参与实现）。
    使用如下表示的格式：
表7－1 需求、接口规格映像表
规格标识符	分配的类
登录	Login
注册	Register
首页	Index
登出	Exit
修改密码	ChangePwd
修改昵称	ChangeName
查看基本信息	Info
查看历史记录	History
好友列表	FriendList
好友申请列表	FriendRequestList
添加好友	addFriend
删除好友	deleteFriend
好友聊天	FriendChat
单机象棋	SinglePlayerChess
游戏重置	GameReset
房间列表	RoomList
创建房间	NewRoom
房间信息	RoomInfo
房间聊天	roomChat
成员列表	memberList
挑战列表	challengeList
房间下棋	roomChess
房间观战	roomWatch

