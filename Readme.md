# 🍱 美军外卖系统

美军外卖系统是一款基于多模块架构的外卖平台应用，涵盖用户端、商家端、员工端以及后台管理系统的完整功能。该项目采用现代化后端技术栈与前端框架构建，具备高可维护性与良好的扩展性。

---

## 🧩 项目结构

```plaintext
root-project/
├── pom.xml  # 根项目的 Maven 配置文件
├── mo-api/               # 接口定义模块
│   ├── src/main/java/    # 包含服务接口和 DTO
│   └── pom.xml
├── mo-core/              # 核心模块（实体类、工具类、异常处理等）
│   ├── src/main/java/
│   └── pom.xml
├── mo-service/           # 业务逻辑层模块
│   ├── src/main/java/    # 包含服务实现类、数据访问层
│   └── pom.xml
├── mo-web/               # Web 层模块（Spring Boot 启动类、Controller）
│   ├── src/main/java/
│   └── pom.xml
└── README.md
```


---

## 📦 模块说明

### 1. **mo-api**
- 定义所有服务接口（如 `AuthService`, `DishService`, `OrderService` 等）。
- 数据传输对象（DTO）定义。
- 所有其他模块通过依赖此模块调用服务接口。

### 2. **mo-core**
- 实体类（Entity）定义，对应数据库表结构。
- 工具类（如 JWT 工具、Redis 工具等）。
- 异常类定义（统一异常处理）。
- 枚举类型定义（订单状态、用户身份等）。

### 3. **mo-service**
- 实现 `mo-api` 中定义的服务接口。
- 数据访问层（Mapper）使用 MyBatis 进行数据库操作。
- 使用 AOP 自动填充创建时间、更新人等字段。

### 4. **mo-web**
- Spring Boot 启动类和 Controller 控制器。
- 提供 RESTful API 接口，支持 Swagger 文档查看。
- 配置类（Redis、Swagger、MyBatis 映射等）。

---

## 🔗 模块间依赖关系

| 模块名       | 描述 | 依赖模块         |
|--------------|------|------------------|
| `mo-service` | 实现服务接口，使用核心模块中的实体类 | `mo-api`, `mo-core` |
| `mo-web`     | 调用服务并对外暴露 RESTful 接口 | `mo-api`, `mo-service` |

---

## ⚙️ 技术栈

- **后端**：Java 21, Spring Boot, MyBatis, Redis, JWT
- **前端**：Vue.js / UniApp（根据实际项目补充）
- **数据库**：MySQL 8.x
- **构建工具**：Maven 3.9.x
- **文档生成**：Swagger UI

---

## 📝 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/meijun-takeout/meijun-ordering.git
cd meijun-ordering
```


### 2. 初始化数据库

导入 SQL 文件：

```bash
mysql -u root -p < init.sql
```


或在 MySQL 客户端中执行 [init.sql](file://G:\Documents\GitHub\meijun-takeout\meijun-ordering\init.sql) 脚本。

### 3. 修改配置文件

修改 `mo-web/src/main/resources/application.yml` 或 [application-dev.yml](file://G:\Documents\GitHub\meijun-takeout\meijun-ordering\mo-web\target\classes\application-dev.yml) 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mo_db
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```


### 4. 构建与启动

```bash
mvn clean install
cd mo-web
mvn spring-boot:run
```


项目将在 `http://localhost:8080` 上运行。

---

## 📌 功能概览

| 功能 | 描述 |
|------|------|
| 用户注册/登录 | 支持微信、手机号注册，JWT 鉴权 |
| 商品展示 | 商家菜品、套餐浏览，分类筛选 |
| 购物车管理 | 添加、删除、数量调整 |
| 下单支付 | 选择地址、配送方式、付款 |
| 订单追踪 | 查看订单状态变化 |
| 售后服务 | 支持退款、换货等售后申请 |
| 商家后台 | 商品管理、订单处理、优惠券发放等 |

---

## 📁 目录结构示例

```
.
├── mo-api
│   ├── src/main/java/com/mo/api/dto        # 数据传输对象
│   └── src/main/java/com/mo/api/service     # 接口定义
├── mo-core
│   ├── src/main/java/com/mo/entity          # 数据库实体类
│   └── src/main/java/com/mo/common          # 工具类、异常类、枚举类
├── mo-service
│   ├── src/main/java/com/mo/service/impl    # 服务实现类
│   └── src/main/java/com/mo/service/mapper  # MyBatis Mapper
└── mo-web
    ├── src/main/java/com/mo/web/controller  # REST 控制器
    └── src/main/resources                   # 配置文件、MyBatis XML 映射
```


---

## 🧪 单元测试

```bash
cd mo-core
mvn test
```


部分模块已集成 Mockito 编写单元测试，确保代码质量。

---

## 📚 开发规范

- 所有接口遵循 RESTful 规范，返回统一格式 `Result<T>`。
- 日志使用 `Lombok + Slf4j` 输出。
- 字段自动填充通过 AOP 切面完成（如创建时间、更新人等）。
- 所有敏感信息加密存储（如密码）。

---

# 美颂外卖API Token问题排查与前端适配方案

## 问题描述

在尝试通过脚本调用美颂外卖后端API时，除了登录接口外，其他所有需要token验证的接口均返回401 Unauthorized错误。这表明token验证机制存在问题或前后端对token处理方式的理解不一致。

## 当前最新理解与核心问题点 (非常重要！基于用户最新反馈和JWT示例)

根据与用户的反复沟通、最新澄清以及用户提供的JWT示例 (`eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjdXN0b21lcklEIjozLCJleHAiOjE3NDgxNjYyNjl9.V3UwGX7zSxs-OXxvetJryy7vdmV4cwFF6dxlN5Sh2-s`)，情况梳理如下：

1.  **登录响应 (`/api/v1/auth/login`)**:
    *   返回的JSON中，`data.token` 字段的值是一个**标准的JWT字符串** (例如，类似于上面提供的 `eyJ...` 示例)。这个JWT的payload中已包含 `customerID` (对应登录返回的 `data.id`)。
    *   同时，响应的 `data.id` (即 `customerID`) 和 `data.username` 也是后续请求需要用到的信息。

2.  **后续API的认证要求 (核心解读用户关键描述)**:
    *   用户关键描述为："他的data.token就是我们需要的token，但是需要base64编码后提取解码下次发送时从前端加上id和username才行"。
    *   **当前最可能的解读如下**：
        *   **步骤1 (获取信息)**: 客户端从登录响应中获取 `data.token` (原始JWT字符串), `data.id` (用户ID), 和 `data.username` (用户名)。
        *   **步骤2 (转换Token)**: 客户端需要将从 `data.token` 获取到的**原始JWT字符串本身进行一次整体的Base64编码**。
            *   例如：如果原始JWT是 `jwt_string = "eyJ..."`，客户端会计算 `encoded_jwt = base64_encode(jwt_string)`。
        *   **步骤3 (构建请求)**: 当调用其他需要认证的API时（如 `/api/v1/user/info`），客户端需要：
            *   将**经过Base64编码后的 `encoded_jwt`** 放入一个特定的请求头中（例如 `token: <encoded_jwt>`）。
            *   **同时**，将原始的 `data.id` 和 `data.username` 作为**另外的、独立的请求头**发送（例如 `X-User-ID: <id_value>` 和 `X-Username: <username_value>`）。
        *   **步骤4 (服务器端处理 - "提取解码")**: 用户描述中的"提取解码"指的应该是服务器端的操作。服务器收到客户端发送的Base64编码的token后，会先进行Base64解码得到原始的JWT字符串，然后再用这个原始JWT进行后续的身份验证。

3.  **这个解读的关键点**:
    *   登录返回的 `data.token` **是**一个直接可用的JWT。
    *   但是，这个JWT在被客户端用于后续请求**之前**，其本身需要被客户端进行**整体Base64编码**。
    *   编码后的JWT与原始的 `id` 和 `username` (通过单独的请求头)共同构成了API的认证信息。

4.  **签名密钥 (Secret Key) 问题**:
    *   此方案下，客户端**不需要自行生成或重新签名JWT**，因为使用的是登录时后端返回的、已经签过名的JWT（只是对其进行了Base64编码的传输处理）。因此，之前关于客户端侧签名密钥未知的问题不再是障碍。

## 后端逻辑的潜在特性

如果这种"客户端对JWT进行Base64编码 + 附加id/username请求头"的认证方式是准确的，那么意味着：

*   后端期望接收一个Base64编码过的JWT字符串，并在内部解码后使用。
*   认证信息是分布式的：一部分在（解码后的）JWT的payload中，一部分在独立的请求头中。
*   这是一种高度自定义、非标准的认证策略，容易引起误解。

## 下一步行动：等待用户确认此核心解读

为了创建能够成功调用API的测试脚本，**迫切需要用户明确以下信息**：

1.  **确认上述核心解读的正确性**：
    *   登录返回的 `data.token` 是否确实是一个标准JWT？
    *   客户端是否确实需要将这个JWT字符串**整体进行Base64编码**，然后将编码后的结果用于后续请求的token头？
    *   "提取解码"是否确实指服务器端对接收到的Base64编码的token进行解码？

2.  **如果上述解读基本正确，请提供确切的请求头名称**：
    *   经过Base64编码后的JWT字符串，是通过哪个HTTP请求头名称发送的？（例如，是 `token` 还是 `Authorization` 还是其他？）
    *   原始的 `data.id` 是通过哪个HTTP请求头名称发送的？（例如，`X-User-ID`, `userId`, `id`？）
    *   原始的 `data.username` 是通过哪个HTTP请求头名称发送的？（例如，`X-Username`, `username`？）

**在得到这些细节的最终确认之前，脚本的修改仍基于当前最合理的推测。**

## 对后端的建议 (再次重申)

最清晰、最标准且易于维护的方式是：

1.  登录接口 (`/api/v1/auth/login`) 返回一个**单一的、标准的JWT**。
2.  这个JWT的**payload内部就直接包含所有后续API验证所需的字段**（例如 `id`, `username`, `identity`, `exp` 等）。
3.  该JWT由后端使用其唯一的、一致的签名密钥进行签名。
4.  客户端拿到此JWT后，无需任何编码、解码或附加额外信息的步骤，直接将其用于后续所有API请求的**标准`Authorization: Bearer <JWT>`请求头**中。

这种方式能极大简化客户端实现，减少误解，并提升安全性与标准化程度。

## 现有资产说明

*   `smart_token_test.sh`: **此脚本将是主要修改对象**，以反映最新的、关于"客户端对JWT进行Base64编码 + 附加id/username请求头"的理解。
*   `generate_token.js`: 如果此解读正确，此脚本将**不再需要**，因为不涉及客户端生成JWT。
*   `token_adapter.js` / `api_client.js`: 前端适配方案代码，其内部逻辑（特别是请求头的设置和token处理）也需要根据最终确认的认证细节进行重大调整。
*   `README.md`: 本文档，持续更新，力求准确反映问题理解的演进和当前状态。