# WolfHouseBlog

一个基于 Spring Boot 3 的个人博客与内容管理后端项目。

集成用户认证与权限、文章/分区管理、常用标签、订阅关注等核心能力，并支持
MyBatis-Flex、Redis、RabbitMQ、ElasticSearch、Knife4j 等常用技术组件。

**默认管理员**：

用户名：`admin`

密码：`CoreWaGuanliDesuNe~1`

## 技术栈

- 语言/运行环境：Java 21
- 框架：Spring Boot 3.5.4
- Web/AOP/校验：spring-boot-starter-web、spring-boot-starter-aop、spring-boot-starter-validation
- 安全认证：spring-boot-starter-security、JWT（jjwt）
- 持久化：MyBatis-Flex、HikariCP
- 数据库：MySQL 8+
- 缓存：Spring Data Redis
- 消息队列：RabbitMQ
- 检索：ElasticSearch Java Client 9.0.1
- 工具库：Hutool
- 接口文档：Knife4j OpenAPI 3
- 构建工具：Maven

## 功能概览

- 账号与安全
    - 注册、登录、登出，获取当前账号资料（JWT）
    - 角色与权限：管理员（ADMIN）/ 用户（USER），基于 Spring Security 的接口级鉴权
- 内容域
    - 文章：发布/草稿、更新、删除、详情；分页与排序；复杂检索（数据库或 ElasticSearch，高亮返回、日期范围过滤）
    - 互动：评论（发布/删除/回复）、点赞（点赞/取消）、收藏（添加/移除/按收藏夹查询）
    - 分区：父子层级、排序、级联删除、可见性控制
    - 标签：常用标签（增删改查）与文章标签绑定展示
    - 收藏夹：新建/编辑/删除、设置默认、按用户获取、查看收藏夹内文章
- 社交域
    - 关注/订阅：关注、取消关注、获取关注列表
- 管理与权限
    - 管理员管理：新增、修改、删除
    - 权限管理：获取权限列表、分配/更新/删除管理员权限
    - 用户管控：管理员删除用户、禁用/启用用户
- 系统能力（非功能性）
    - Redis 缓存、RabbitMQ 集成
    - ElasticSearch 检索与统一高亮、排序字段白名单
    - 全局异常处理、统一响应模型、Swagger/Knife4j 文档

## 开发阶段

后端系统功能实现暂时分为三个阶段。

- [x] 第一阶段：实现基础功能
    - 用户管理：注册，登陆，修改信息，关注，获取信息
    - 文章管理：新增，修改，删除，基于数据库的搜索
- [x] 第二阶段：实现进阶功能
    - 分区管理：获取，新增，修改，删除，排序，级联删除
    - 常用标签管理：获取列表，获取，新增，修改，删除
    - 管理员管理：新增，修改，删除，获取权限，删除用户
    - 用户管理：禁用，注销
- [ ] 第三阶段：实现高级功能
    - [ ] 用户管理：根据用户名搜索√，忘记密码，头像修改
    - [x] 权限限制：使用 Spring Security 实现管理员、用户操作权限控制
    - [x] 权限管理：新增，修改，删除，分配权限
    - [ ] 文章管理：
        - [x] 复杂查询：模糊查询，排序
        - [x] 点赞：点赞、取消点赞
        - [x] 收藏：添加、移除
        - [x] 评论：发布、删除、回复
        - [ ] 分享
    - [x] 收藏夹管理：添加、移除、编辑、设置默认
    - [x] 系统优化：
        - 引入 Redis 缓存: 用户登录，文章详情
        - 引入 ElasticSearch: 文章复杂查询

## 目录结构

```
WolfHouseBlog/
├─ pom.xml
├─ readme.md
├─ src
│  ├─ main
│  │  ├─ java/com/wolfhouse/wolfhouseblog
│  │  │  ├─ auth                # 安全认证相关
│  │  │  │  ├─ config           # 安全配置
│  │  │  │  ├─ filter           # JWT 过滤器
│  │  │  │  └─ service          # 认证服务
│  │  │  ├─ common              # 通用组件
│  │  │  │  ├─ constant         # 常量定义
│  │  │  │  ├─ exceptions       # 自定义异常
│  │  │  │  ├─ http             # HTTP 相关
│  │  │  │  └─ utils            # 工具类
│  │  │  ├─ config              # 全局配置
│  │  │  ├─ controller          # 控制器
│  │  │  ├─ handler             # 异常处理器
│  │  │  ├─ mapper              # MyBatis-Flex 映射
│  │  │  ├─ pojo                # 领域对象
│  │  │  │  ├─ dto              # 数据传输对象
│  │  │  │  ├─ entity           # 实体类
│  │  │  │  └─ vo               # 视图对象
│  │  │  └─ service             # 业务服务
│  │  │     └─ impl             # 服务实现
│  │  └─ resources
│  │     ├─ application.yaml    # 基础配置（激活 dev）
│  │     ├─ mapper              # SQL 映射文件
│  │     └─ sql                 # 数据库初始化脚本
│  └─ test
│     └─ java/com               # 单元测试包根
```

## 环境要求

- JDK 21
- Maven 3.9+
- MySQL 8+
- Redis 6+
- RabbitMQ 3.11+
- ElasticSearch 8.18.7

## 快速开始

1) 克隆项目并进入目录

```
git clone git@github.com:RylinWolf/WolfHouseBlog.git
cd WolfHouseBlog
```

2) 初始化数据库（MySQL）

1. 在本地 MySQL 中执行 `src/main/resources/sql/schema.sql`，将创建数据库 `wolfBlog` 及相关表结构。
2. 在本地 MySQL 中执行 `src/main/resources/sql/init.sql`，初始化管理员角色

3) 配置本地开发环境

- 默认端口：`8999`
- 默认激活 profile：`dev`（见 `application.yaml`）
- 创建 `application-dev.yaml`
- 在 `src/main/resources/application-dev.yaml` 中添加：
    - MySQL：`spring.datasource.url/username/password`
    - Redis：`spring.data.redis.password`
    - RabbitMQ：`spring.rabbitmq.*`
    - ElasticSearch: `custom.elasticsearch.host`
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
    expiration: 86400000  # token 过期时间
  date:
    datetime: yyyy-MM-dd HH:mm:ss
    time: HH:mm:ss
    date: yyyy-MM-dd
   
  elasticsearch:
    host: [你的 ES 服务器地址]
    max-result-window: 500000 # 最大查询窗口


knife4j:
  enable: true
  production: false  # 若要在生产环境中关闭，则设置为 true
```

4) 运行项目

- 方式一：Maven 运行（推荐开发态）

```
mvn spring-boot:run
```

- 方式二：打包并运行

```
mvn clean package -DskipTests
java -jar target/WolfHouseBlog-1.4.3-SNAPSHOT.jar
```

5) 访问接口文档（Knife4j）

- 启动后访问：http://localhost:8999/doc.html
- 或者访问 Swagger UI：http://localhost:8999/swagger-ui/index.html

## 常见问题（FAQ）

- 端口被占用？
    - 修改 `application.yaml` 的 `server.port` 或释放 8999 端口。
- 数据库连接失败？
    - 确认已创建数据库并执行了 `schema.sql`，检查配置文件中 MySQL URL、账号与密码。
- Redis/RabbitMQ/ElasticSearch 必须安装吗？
    - 项目已集成并使用相关依赖；若不安装则无法正常运行项目。
    - 对于 ElasticSearch，目前仅在文章搜索功能中使用，可手动移除相关代码，换用数据库实现类
- 文档打不开？
    - 确认服务已启动且访问路径为 `/doc.html`，同时检查安全配置是否允许访问。

## 部署建议

- 使用独立的 `application-prod.yaml` 来覆盖生产配置（数据库、Redis、RabbitMQ、ES、JWT 密钥等）。
- 通过环境变量或外部化配置注入敏感信息（例如：`SPRING_DATASOURCE_PASSWORD`）。

## 致谢

- Spring Boot、MyBatis-Flex、Knife4j、Hutool 等优秀开源项目。

## 架构与模块

- 参考根目录的设计图：
    - 系统架构.drawio：整体技术与部署架构
    - 文章模块.drawio：文章域模型与核心交互
- 核心模块：
    - 认证与安全（auth、security、filter、config）
    - 内容域（article、partition、tag、favorites、comment、like）
    - 管理与权限（admin、authority）
    - 通用组件（common：constant、exceptions、http、utils）

## API 路由速览（更多见 /doc.html）

- 用户：/user/register、/user/login、/user/profile、/user/{username}
- 文章：/article/create、/article/update、/article/delete/{id}、/article/{id}、/article/page
- 点赞：/article/like/{id}、/article/unlike/{id}
- 评论：/comment/create、/comment/delete/{id}、/comment/reply
-

收藏夹：/favorites/create、/favorites/update、/favorites/delete/{id}、/favorites/default、/favorites/list、/favorites/{id}/articles

- 分区：/partition/create、/partition/update、/partition/delete/{id}、/partition/list、/partition/tree
- 标签：/tag/list、/tag/{id}、/tag/create、/tag/update、/tag/delete/{id}
- 关注：/follow/{userId}、/unfollow/{userId}、/follow/list
- 管理：/admin/create、/admin/update、/admin/delete/{id}
- 权限：/authority/list、/authority/assign、/authority/remove

以 Swagger/Knife4j 文档为准，具体参数、请求体与响应详见 /doc.html。

## 权限与角色说明

- 匿名可访问：登录注册、Swagger 文档、部分文章检索接口（取决于可见性）
- 需要认证：用户资料、互动行为（点赞/收藏/评论/关注）
- 管理员专属：管理员管理、权限分配、用户禁用/删除

## 检索与高亮（ElasticSearch）

- 项目已集成 ElasticSearch Java Client（8.18.7），在搜索场景下可选择走 ES。
- 高亮策略（见 common/utils/EsUtil）：
    - 统一 preTags: <em class='highlight'>，postTags: </em>
    - requireFieldMatch=false，fragmentSize=50
- 排序字段白名单：通过 ArticleConstant.SORT_FIELD_ALLOWED 控制，避免任意字段排序导致性能或安全问题。
- 日期范围查询：支持按发布时间范围过滤，格式遵循 application 配置。