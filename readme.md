# WolfHouseBlog

一个基于 Spring Boot 3 的个人/团队博客与内容管理后端项目。集成用户认证与权限、文章/分区管理、常用标签、订阅关注等核心能力，并支持
MyBatis-Flex、Redis、RabbitMQ、ElasticSearch、Knife4j 等常用技术组件。

**默认管理员**：

用户名：`admin`

密码：`CoreWaGuanliDesuNe~1`

## 技术栈

- 语言/运行环境：Java 21
- 框架：Spring Boot 3.5.3
- Web/AOP/校验：spring-boot-starter-web、spring-boot-starter-aop、spring-boot-starter-validation
- 安全认证：spring-boot-starter-security、JWT（jjwt）
- 持久化：MyBatis-Flex、HikariCP
- 数据库：MySQL 8+
- 缓存：Spring Data Redis
- 消息队列：RabbitMQ
- 检索：ElasticSearch Java Client
- 工具库：Hutool
- 接口文档：Knife4j OpenAPI 3
- 构建工具：Maven

## 功能概览

- 用户与认证
    - 用户基础信息、用户认证表
    - JWT 登录/校验
    - Spring Security 权限控制
- 内容域
    - 文章发布、编辑、可见性控制、标签
    - 分区（支持父子分区、排序、可见性）
    - 常用标签管理
- 社交域
    - 关注/订阅
- 运维能力
    - Redis 缓存
    - RabbitMQ 异步消息
    - ElasticSearch（可选，按需接入）
    - Knife4j 在线文档

## 开发阶段

后端系统实现暂时分为三个阶段。

- [x] 第一阶段：实现基础功能
    - 用户管理：注册，登陆，修改信息，关注，获取信息
    - 文章管理：新增，修改，删除，基于数据库的搜索
- [x] 第二阶段：实现进阶功能
    - 分区管理：获取，新增，修改，删除，排序，级联删除
    - 常用标签管理：获取列表，获取，新增，修改，删除
    - 管理员管理：新增，修改，删除，获取权限，删除用户
    - 用户管理：禁用，注销
- [ ] 第三阶段：实现高级功能
    - [ ] 使用 AI 生成博客文章摘要
    - [ ] 引入 Redis 缓存优化性能
    - [ ] 权限管理：新增，修改，删除，分配权限
    - [ ] 文章管理：使用 ES 搜索
    - [x] 用户管理：根据用户名搜索
    - [x] 权限限制：使用 Spring Security 实现管理员、用户操作权限控制
    - [ ] 管理后台：使用动态入口
    - [ ] 访问控制：优化 JWT
    - [ ] 业务方法优化
        - 优化获取分区列表的逻辑

## 目录结构

```
WolfHouseBlog/
├─ pom.xml
├─ HELP.md
├─ readme.md                    
├─ src
└─ ├─ main
   │  ├─ java/com/wolfhouse/wolfhouseblog
   │  │  ├─ auth                # 安全认证相关
   │  │  │  ├─ config          # 安全配置
   │  │  │  ├─ filter         # JWT过滤器
   │  │  │  └─ service        # 认证服务
   │  │  ├─ common             # 通用组件
   │  │  │  ├─ constant       # 常量定义
   │  │  │  ├─ exceptions     # 自定义异常
   │  │  │  ├─ http          # HTTP相关
   │  │  │  └─ utils         # 工具类
   │  │  ├─ config            # 全局配置
   │  │  ├─ controller        # 控制器
   │  │  ├─ handler          # 异常处理器
   │  │  ├─ mapper           # MyBatis-Flex映射
   │  │  ├─ pojo             # 领域对象
   │  │  │  ├─ dto          # 数据传输对象
   │  │  │  ├─ entity       # 实体类
   │  │  │  └─ vo           # 视图对象
   │  │  └─ service          # 业务服务
   │  │     └─ impl         # 服务实现
   │  └─ resources
   │     ├─ application.yaml    # 基础配置（激活dev）
   │     ├─ application-dev.yaml# 本地开发配置（需自行创建）
   │     ├─ mapper             # SQL映射文件
   │     ├─ static            # 静态资源
   │     ├─ templates         # 模板文件
   │     └─ sql/schema.sql    # 数据库初始化脚本
   └─ test
      └─ java                 # 单元测试

```

## 环境要求

- JDK 21
- Maven 3.9+
- MySQL 8+
- Redis 6+
- RabbitMQ 3.11+
- ElasticSearch 8+

## 快速开始

1) 克隆项目并进入目录

```
git clone git@github.com:RylinWolf/WolfHouseBlog.git
cd WolfHouseBlog
```

2) 初始化数据库（MySQL）

- 在本地 MySQL 中执行 `src/main/resources/sql/schema.sql`，将创建数据库 `wolfBlog` 及相关表结构。

3) 配置本地开发环境

- 默认端口：`8999`
- 默认激活 profile：`dev`（见 `application.yaml`）
- 创建 `application-dev.yaml`
- 在 `src/main/resources/application-dev.yaml` 中添加：
    - MySQL：`spring.datasource.url/username/password`
    - Redis：`spring.data.redis.password`
    - RabbitMQ：`spring.rabbitmq.*`
    - JWT：`custom.jwt.secret`、`custom.jwt.expiration`
    - 日期格式：`custom.date.*`

示例（已内置的默认 dev 配置）：

```
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/wolfBlog?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: [你的用户]
    password: [你的密码]
  data:
    redis:
      password: [你的密码]
  rabbitmq:
    virtual-host: [你的虚拟地址]
    username: [你的用户]
    password: [你的密码]
    host: localhost
    port: 5672

custom:
  jwt:
    secret: [自定义密钥]
    expiration: 86400000
  date:
    datetime: yyyy-MM-dd HH:mm:ss
    time: HH:mm:ss
    date: yyyy-MM-dd
```

4) 运行项目

- 方式一：Maven 运行（推荐开发态）

```
mvn spring-boot:run
```

- 方式二：打包并运行

```
mvn clean package -DskipTests
java -jar target/WolfHouseBlog-0.0.1-SNAPSHOT.jar
```

5) 访问接口文档（Knife4j）

- 启动后访问：http://localhost:8999/swagger-ui/index.html

## 常见问题（FAQ）

- 端口被占用？
    - 修改 `application.yaml` 的 `server.port` 或释放 8999 端口。
- 数据库连接失败？
    - 确认已创建数据库并执行了 `schema.sql`，检查 `application-dev.yaml` 中 MySQL URL、账号与密码。
- Redis/RabbitMQ/ElasticSearch 必须安装吗？
    - 项目已集成相关依赖，功能上按需启用；开发阶段如未使用到相关模块，可在配置中先留空或关闭对应自动装配（视具体代码实现）。
- 文档打不开？
    - 确认服务已启动且访问路径为 `/doc.html`，同时检查安全配置是否允许访问。

## 部署建议

- 使用独立的 `application-prod.yaml` 来覆盖生产配置（数据库、Redis、RabbitMQ、ES、JWT 密钥等）。
- 通过环境变量或外部化配置注入敏感信息（例如：`SPRING_DATASOURCE_PASSWORD`）。

## 致谢

- Spring Boot、MyBatis-Flex、Knife4j、Hutool 等优秀开源项目。
