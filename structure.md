# 项目结构

本文档由系统架构视角梳理 WolfHouseBlog 的整体架构、技术栈、配置与工具类、核心业务调用链及关键实现细节，便于研发与运维人员快速理解与维护。

---

## 1. 技术栈总览

- 基础框架
  - Spring Boot 3.5.4
  - Java 21，Lombok（编译期注解）
- Web 与文档
  - spring-boot-starter-web（JSON via Jackson）
  - Knife4j OpenAPI 3（接口文档）：`com.github.xiaoymin:knife4j-openapi3-jakarta-spring-boot-starter`
  - Swagger/OpenAPI：`io.swagger.v3.oas`（见 `SwaggerConfig`）
- 安全
  - spring-boot-starter-security
  - JWT: `io.jsonwebtoken:jjwt-impl`, `jjwt-jackson`
- 数据访问
  - MyBatis-Flex（`mybatis-flex-spring-boot3-starter`）
  - Spring Data JDBC（与 MyBatis-Flex 共同使用）
  - MySQL 驱动（runtime）
  - 连接池：HikariCP
- 缓存/中间件
  - Spring Data Redis（`spring-boot-starter-data-redis`）
  - RabbitMQ（`spring-boot-starter-amqp`）
  - Elasticsearch：
    - spring-boot-starter-data-elasticsearch（Spring Data 生态）
    - 官方 Java 高级客户端：`co.elastic.clients:elasticsearch-java`
- 其他
  - AOP（`spring-boot-starter-aop`）
  - 校验（`spring-boot-starter-validation`）
  - Hutool、Guava 工具库
  - Jackson 扩展：`jackson-datatype-jsr310`、`jackson-databind-nullable`
- 测试
  - spring-boot-starter-test、spring-security-test、spring-rabbit-test、Mockito

系统采用“ES + DB + Redis + MQ”组合，围绕文章与用户构建高读低写的博客平台：
- 查询走 ES（支持高亮/分页），写入/更新通过 MQ 异步同步 ES 与缓存；
- 热点数据（文章、点赞、浏览量）经 Redis 缓存与聚合；
- DB 作为最终一致性的数据源；
- JWT 保护 API 与权限控制。

---

## 2. 代码包结构总览

- `com.wolfhouse.wolfhouseblog.config`：配置类（Web/MVC、Security、MQ、Redis、Elasticsearch、ObjectMapper、Swagger、EntryPoint 等）
- `com.wolfhouse.wolfhouseblog.controller`：REST 控制器（如 `ArticleController`、`AdminController` 等）
- `com.wolfhouse.wolfhouseblog.application`：应用服务接口与实现（跨域编排，如 `ArticleApplicationServiceImpl`）
- `com.wolfhouse.wolfhouseblog.service`：领域服务（文章、用户、权限等），含 `mediator` 协调 ES 和 DB
- `com.wolfhouse.wolfhouseblog.es`：基于 ES 的文章查询/索引实现（`ArticleElasticServiceImpl`）
- `com.wolfhouse.wolfhouseblog.redis`：Redis 服务（`ArticleRedisService`、`RoleRedisService` 等）
- `com.wolfhouse.wolfhouseblog.mq`：MQ 常量、生产者（`MqRedesService`）、监听器（`ArticleRedesListener`）
- `com.wolfhouse.wolfhouseblog.common`：通用常量、异常、HTTP 包装、工具类、校验框架等
- `com.wolfhouse.wolfhouseblog.pojo`：DTO/VO/Domain（实体、数据传输与视图对象）

---

## 3. 配置类详解

1) `ObjectMapperConfig`
- 作用：集中管理 Jackson `ObjectMapper` 的三套配置，适配默认序列化、JsonNullable、ES 字段命名风格。
- Bean：
  - `defaultObjectMapper`（@Primary）：`JacksonObjectMapper` + `WRITE_DATES_AS_TIMESTAMPS=false` + `SimpleDateFormat(DateProperties.datetime())`
  - `jsonNullableObjectMapper`：`NON_NULL` + `JsonNullableModule`
  - `esObjectMapper`：`NON_NULL` + `JsonNullableModule` + `SNAKE_CASE`（ES 索引字段风格）

2) `MvcConfig`（实现 `WebMvcConfigurer`）
- CORS：允许任意源，方法 `GET/POST/PUT/DELETE/OPTIONS/PATCH`。
- 消息转换器：
  - 默认 JSON：`MappingJackson2HttpMessageConverter(defaultObjectMapper)` → `application/json`
  - JsonNullable JSON：`MappingJackson2HttpMessageConverter(jsonNullableObjectMapper)` → 自定义媒体类型 `application/json-nullable`
- 静态资源：`/doc.html`、`/webjars/**` 指向 META-INF 资源（Knife4j）。

3) `RedisConfig`
- 作用：统一 RedisTemplate 的 Key/Value 序列化策略为 `GenericJackson2JsonRedisSerializer(defaultObjectMapper)`。
- Bean：`RedisTemplate<String,Object>`

4) `MqConfig`
- 作用：
  - `RabbitTemplate` 初始化；
  - `MessagePostProcessor` 自动注入 `messageId`（若缺失则以 UUID 生成）。

5) `MqListenerConfig`
- 作用：配置 `RabbitListenerContainerFactory`，将消息 JSON 转换器设置为 `Jackson2JsonMessageConverter(jsonNullableObjectMapper)`。

6) `ElasticSearchConfig`
- 作用：基于 `esObjectMapper` 配置 Elastic 官方高阶客户端。
- Bean：`ElasticsearchClient`
- 认证：从环境变量读取 `ELASTIC_SEARCH_USERNAME/PASSWORD`，通过 `RestClient` + Basic Auth 连接到 `custom.elasticsearch.host`。
- 其他：映射 `@ConfigurationProperties(prefix = "custom.elasticsearch")`，含 `host` 与 `maxResultWindow`。

7) `SwaggerConfig`
- 作用：定义 `OpenAPI` Bean，设置标题 "WolfHouseBlog API"。

8) `SecurityConfig`
- 作用：配置 Spring Security 过滤链与密码编码器。
- Bean：
  - `JwtFilter`（依赖 `JwtUtil`、`ServiceAuthMediator`、`AdminService`、`RoleRedisService`）
  - `SecurityFilterChain`：
    - 关闭 formLogin/httpBasic/logout/CSRF；
    - 放行静态与公共路径（`SecurityConstant.STATIC_PATH_WHITELIST`、`PUBLIC_URLS`、`/webjars/**`、`OPTIONS`）；
    - 其余请求均需认证；
    - 异常处理：`AuthenticationEntryPoint`、`AccessDeniedHandler`；
    - 过滤器链：在 `UsernamePasswordAuthenticationFilter` 之前加入 `JwtFilter`。
  - `BCryptPasswordEncoder`

9) `EntryPointConfig`
- 作用：定义认证异常入口与权限拒绝处理：
  - `authenticationEntryPoint`：401，`HttpResult` 返回 UN_LOGIN 或 Token 错误信息；
  - `accessDeniedHandler`：403，`HttpResult` 返回 ACCESS_DENIED。

10) `AuthenticationManagerConfig`
- 作用：以应用自定义 `AuthenticationProvider` 构建 `AuthenticationManager`，供登录认证使用。

---

## 4. 工具类详解（作用、关键参数与返回值）

- `BeanUtil`
  - `static <T,V> V copyProperties(T source, Class<V> target[, boolean ignoreBlank])`：反射+Hutool 属性复制；`ignoreBlank=true` 会将空字段置 null 后复制；返回目标实例。
  - `static <T,V> List<V> copyList(List<T> source, Class<V> target)`：列表映射复制。
  - `static <T> Boolean isBlank(T obj)`：统一空判断（Collection/Map/String/Boolean/其他 → null 判定）。
  - `static Boolean isAnyBlank/isAnyNotBlank(Object...)`、`checkBlank(Object...)`、`blankFieldsFrom(Object)`：常用判空与空字段收集。

- `ServiceUtil`
  - `static Long loginUser()`：从 `SecurityContext` 获取当前登录用户 id（匿名返回 null）。
  - `static Boolean isLogin()`、`static Long loginUserOrE()`：登录态判断与未登录抛业务异常。
  - `static void setLoginUser(Long)`、`setAuthorities(Collection<GrantedAuthority>)`、`removeLogin()`：测试/内部场景注入与清理安全上下文。

- `JwtUtil`（基于 `JwtProperties`）
  - `String getToken(Authentication)` / `String getToken(String id)`：生成 HS256 签名的 JWT，主题为用户 id，过期时间来自配置。
  - `Claims parseToken(String token)`、`String getUsername(String token)`：解析并返回 claims/subject。

- `UrlMatchUtil`（基于 `SecurityConstant` 白名单与公共 URL）
  - 主要用于 `JwtFilter` 中判定是否放行（不强制 JWT 验证）的 URL。

- Page 工具
  - `PageDto`：分页入参基类（常含 `pageNum/pageSize` 等）。
  - `PageResult<T>`：分页结果，含 `records/currentPage/pageSize/totalRow`，并提供 `of(Page<T>[, Class<R>])` 转换。

- 校验框架（`common.utils.verify`）
  - 基础接口与节点：`VerifyChain`、`VerifyNode`、`VerifyStrategy`、`VerifyTool` 等。
  - 领域化校验节点：`user/*`、`article/*`、`admin/*`、`partition/*`、`tag/*`、`commons/*`。
  - 用法示例（见 `JwtFilter`）：`VerifyTool.of(UserVerifyNode.id(mediator).target(userId)).doVerify()`。

- `JacksonObjectMapper`
  - 自定义 `ObjectMapper` 扩展，结合 `DateProperties` 配置时间序列化格式，被 `ObjectMapperConfig` 复用。

- `EsUtil`、`JsonNullableUtil` 等
  - 面向 ES 字段/JsonNullable 适配的工具函数（与 `ArticleElasticServiceImpl`、消息转换器配合）。

---

## 5. 安全与认证授权流程

- 过滤链（`SecurityConfig`）：所有非白名单与非公共路径的请求均需认证。
- `JwtFilter`
  - 读取 `Authorization`（`HttpConstant.AUTH_HEADER`）中的 JWT；
  - 若存在 Redis token 缓存：读取并刷新；否则解析 JWT，获取 `userId`；
  - 校验用户可达（`VerifyTool` + `UserVerifyNode` + `ServiceAuthMediator`）；
  - 加载权限：先读 Redis（`RoleRedisService.getAuthorities`），若无则 `AdminService.getAuthorities` 并写缓存；
  - 构造 `UsernamePasswordAuthenticationToken(userId, null, authorities)` 写入 `SecurityContext`；
  - 异常：未提供/错误 Token 且 URL 不在公共列表 → 调用 `AuthenticationEntryPoint` 返回 401；用户不存在抛 `UserAuthException` 时放行但不注入认证。
- 方法级权限：各 Controller 使用 `@PreAuthorize("@ss.hasRole('...')")` 或 `@ss.hasPerm(...)`，其中 `@ss` 为自定义权限服务（参考 `auth.permission.PermissionService`）。

---

## 6. 消息队列（RabbitMQ）拓扑与监听

- 常量类：`MqArticleEsConstant`
  - 基于 `MqConstant.SERVICE/es/article` 前缀构造：
    - 事件：`post/delete/update/like/unlike`
    - 对应 Exchange/Queue/RoutingKey：`{BASE}{EVENT}.exchange|queue|key`
- 生产者：`MqRedesService`
  - `postArticle(Article)` → `POST_EXCHANGE/POST_KEY`
  - `updateArticle(ArticleUpdateDto)` → `UPDATE_EXCHANGE/UPDATE_KEY`
  - `deleteArticle(Long id)` → `DELETE_EXCHANGE/DELETE_KEY`
  - `like(Long id)` → `LIKE_EXCHANGE/LIKE_KEY`
  - `unlike(Long id)` → `LIKE_EXCHANGE/UNLIKE_KEY`
- 监听器：`ArticleRedesListener`
  - `post(ArticleVo)`：写 ES（保存 `ArticleEsDto`）、写 Redis 缓存文章
  - `update(ArticleUpdateDto)`：更新 ES，移除 Redis 缓存，调用 `ArticleApplicationService.getArtVoSync` 重新回填缓存
  - `delete(Long id)`：删除 ES 文档并移除 Redis 缓存
  - `like(Long id)`：ES 点赞数 `+1`，Redis 点赞缓存增量
  - `unlike(Long id)`：ES 点赞数 `-1`，Redis 点赞缓存增量（负向）
- 监听容器：`MqListenerConfig` 使用 JSON 消息转换器；消息 ID 自动补全由 `MqConfig.messageIdPostProcessor` 保证。

---

## 7. Redis 缓存与聚合策略（以文章为例）

- 服务：`ArticleRedisService`
  - 文章缓存：`cacheOrUpdateArticle(ArticleVo)`、`getCachedArticle(Long)`、`removeArticleCache(Long|Collection)`
  - 浏览量聚合：
    - 写入：`increaseView(Long articleId)`（hash/incr）
    - 拉取与清理：`getViewsAndDelete()` / `getViewsAndDelete(Long)`
    - 失败回滚：`decreaseViews(Map|articleId, views)`
  - 列表缓存：`cacheBriefs(ArticleQueryPageDto, PageResult<ArticleBriefVo>)`、`getCachedBrief(ArticleQueryPageDto)`
  - 点赞聚合：`like(Long id)`/`unlike(Long id)`、`getLikesAndRemove()`/`getLikesAndRemove(Long)`
  - 其他：分布式锁 `getLock(String lock)`；缓存随机过期 `randTimeout()`
- Key/序列化：统一 JSON 序列化（见 `RedisConfig`）；Key 命名在服务内部封装（含文章/视图/列表前缀）。

---

## 8. Elasticsearch 索引与查询（Article）

- 实现类：`ArticleElasticServiceImpl`（实现文章服务接口 `ArticleService` 能力）
- 能力概览：
  - 索引写入：`saveOne`、`saveBatch`、`saveBatchByDefault`、`update(ArticleUpdateDto)`、`deleteById`
  - 点赞/浏览量增量：`addLikes(Long,Long|Map)`、`addViews(Long,Long|Map)`
  - 单体/集合查询：`getById(Long)`、`getVoById(Long)`、`getVoByIds(Collection<Long>)`
  - 分页/条件/高亮：`queryVoBy(ArticleQueryPageDto, Boolean highlight, QueryColumn...)`、`getQueryVo(...)`、`pageResultBy(...)`
  - 分页 DSL 构建：`buildMustQueryListFromDto(...)`、`buildPagination(...)`
- 高亮与过滤：由 `ArticleQueryPageDto` 的条件构造 `must`/`filter` 查询，支持高亮字段；支持按列裁剪（`QueryColumn...`）。

---

## 9. 应用服务与中台编排（Mediator）

- `ArticleApplicationServiceImpl`
  - 文章 `BFF 层(Backend for Frontend)` 用于数据聚合
  - `getArtVoSync(Long id)`：获取文章详情优先 Redis；未命中则走 `ArticleEsDbMediator.getArticleVoById(id)`（先 ES，后 DB 并回写 ES），随后：
    - 注入作者信息：`UserEsDbMediator.getUserVoById(authorId)` 并拷贝为 `UserBriefVo`
    - 点赞并集成：`likeCount += redisService.getLikesAndRemove(id)`
    - 缓存落盘：`redisService.cacheOrUpdateArticle(vo)`
  - `queryArticleVo(ArticleQueryPageDto, [QueryColumn...])`：调用 `queryVoBy(...)`，内部走 `ArticleEsDbMediator.queryBy`（先 ES，ES 为空时回源 DB、回填 ES），并为每条记录注入作者信息（缓存优先）
  - `queryArticleBriefVo(ArticleQueryPageDto)`：与 `queryArticleVo` 类似，但输出 `ArticleBriefVo`
  
- `ArticleEsDbMediatorImpl`
  - 文章 ElasticSearch 与 数据库中介者
  - 读流程：
    - `getArticleVoById(id)`：优先 ES，未命中则 DB，成功后将文章同步到 ES
    - `queryBy(dto, columns)`：优先从 ES 分页查询；若记录为空则从 DB 查询、回写 ES；统一返回 `Page<ArticleVo>`
  - 写/同步：
    - `syncArticleFromDb(id|ArticleEsDto)`：将 DB/VO 映射为 ES DTO 并写 ES
    - `syncArticleToDb(id|ArticleEsDto)`：将 ES 文档同步回 DB（用于特定场景）
  - 增量：`addViews*`/`addLikes*` 提供对 ES 与 DB 的双写或选择性写入能力


---

## 10. 控制器与业务方法调用链

以下重点列举文章与管理员模块的典型接口调用链，标注参数/返回值与核心逻辑节点。

- Article 模块（`ArticleController` 片段，方法较多，仅列关键流）
  - `GET /api/article/{id} -> get(Long id): HttpResult<ArticleVo>`
    1) 参数：路径 `id`
    2) 应用层：`ArticleApplicationService.getArtVoSync(id)`
       - Redis 命中返回
       - 未命中：`ArticleEsDbMediator.getArticleVoById`（先 ES，后 DB → 回写 ES）
       - 注入作者信息、合并点赞增量、写回 Redis
    3) 返回：`ArticleVo` 包装为 `HttpResult`
  - `POST /api/article -> post(ArticleDto): HttpResult<ArticleVo>`（发布）
    1) Service 层（未展开文件）：创建文章至 DB/ES，并通过 `MqRedesService.postArticle(Article)` 投递；
    2) Listener `ArticleRedesListener.post`：写 ES 与 Redis 缓存；
    3) 返回：创建后的 `ArticleVo`
  - `PATCH /api/article -> update(ArticleUpdateDto): HttpResult<ArticleVo>`（更新）
    1) 生产消息：`MqRedesService.updateArticle(dto)`；
    2) Listener `update`：更新 ES、清理缓存、调用应用服务回填缓存；
    3) 返回：更新后的 `ArticleVo`
  - `DELETE /api/article/{id} -> delete(Long id): HttpResult<?>`
    1) 生产消息：`MqRedesService.deleteArticle(id)`；
    2) Listener `delete`：删除 ES 文档，移除缓存；
  - 点赞/取消点赞
    - `POST /api/article/{id}/like -> like(Long id)`：`MqRedesService.like(id)`；Listener `like`：ES `+1`，Redis 聚合
    - `POST /api/article/{id}/unlike -> unLike(Long id)`：`MqRedesService.unlike(id)`；Listener `unlike`：ES `-1`，Redis 聚合
  - 分页查询
    - `GET /api/article -> query(ArticleQueryPageDto): HttpResult<PageResult<?>>`
      - 应用层 `queryArticleVo(dto[, columns])`：走 `ArticleEsDbMediator.queryBy`（ES 优先、回源 DB 与回填 ES），并注入作者信息；
      - 返回 `PageResult<ArticleVo>` 或裁剪后的列集合。

- Admin 模块（`AdminController`）
  - `GET /api/a/au` 获取权限列表（需要 `ADMIN` 角色）
    - `ServiceUtil.loginUserOrE()` 获取登录用户 id
    - `AdminService.isUserAdmin(login)` 保护逻辑
    - `AdminService.getAuthorities(login)` → `BeanUtil.copyList(..., AuthorityVo.class)` → `HttpResult.success`
  - `POST /api/a` 添加管理员（需要 `SUPER_ADMIN`）
    - `AdminService.createAdmin(AdminPostDto)` → 失败返回 `HttpResult.failedIfBlank`
  - `PATCH /api/a` 更新管理员（`SUPER_ADMIN`）
    - `AdminService.updateAdmin(AdminUpdateDto)`
  - `DELETE /api/a/{adminId}` 删除管理员（`SUPER_ADMIN`）
    - `AdminService.delete(Long adminId)`
  - 用户管理（需要具体权限 `USER_DELETE`/`USER_DISABLE`）
    - `deleteUser(AdminUserControlDto)`、`disableUser(...)`、`enableUser(...)`

授权检查基于 `@PreAuthorize` + `PermissionService`（`@ss`），权限和角色从 Redis 优先读取，不命中再查 DB → 回写 Redis。

---

## 11. 持久化层与事务

- MyBatis-Flex 用于实体与 Mapper 映射，代码生成器/注解处理器由 `mybatis-flex-processor` 支持。
- `ArticleService.getMapper().insertWithPk(...)` 等代码显示场景化使用 Mapper 直接操作数据（见 `ArticleEsDbMediatorImpl.syncArticleToDb`）。
- Spring Data JDBC 参与事务管理与数据源配置（结合 HikariCP）。
- 事务边界：服务层（未展开的实现类）应承担写事务；应用层主要编排与集成。

---

## 12. 一致性与数据流摘要

- 写入：Controller → Service（DB）→ 生产 MQ → Listener（ES 写入/更新/删除 + Redis 缓存更新/清理）
- 读取：
  - 详情：Redis 命中 → 直接返回；未命中 → Mediator 先 ES、再 DB 回源并回写 ES → 应用服务注入作者、合并点赞增量并写回 Redis
  - 列表/搜索：ES 优先，ES 为空时回源 DB 并批量导入 ES
- 增量聚合：点赞与浏览量优先在 Redis 聚合，异步/准实时同步至 ES；DB 可按策略落盘
- 失败补偿：Redis 提供 `decreaseViews` 回滚接口；消息投递有 `messageId`，监听容器可扩展重试/死信（如有 `MqMessageRecoverConfig`）。

---

## 13. 关键方法参数与返回值摘要

- `ArticleApplicationService`
  - `ArticleVo getArtVoSync(Long id)`：入参文章 id；返回文章详情 VO；异常传播
  - `PageResult<ArticleVo> queryArticleVo(ArticleQueryPageDto dto[, QueryColumn... columns])`
  - `PageResult<ArticleBriefVo> queryArticleBriefVo(ArticleQueryPageDto dto)`

- `ArticleApplicationServiceImpl`
  - 见第 9 节详细流程；内部私有 `Page<ArticleVo> queryVoBy(...)` 用于统一分页查询与作者注入

- `ArticleEsDbMediator`
  - `ArticleVo getArticleVoById(Long id)`、`Page<ArticleVo> queryBy(ArticleQueryPageDto dto, QueryColumn[] columns)`
  - `void syncArticleFromDb(Long id|ArticleEsDto dto)`、`void syncArticleToDb(Long id|ArticleEsDto dto)`
  - `Boolean addViewsToEsDb(Long id, Long views)`、`Set<Long> addLikes(Map<String,Long>)` 等

- `ArticleElasticServiceImpl`（节选）
  - `Page<ArticleVo> queryVoBy(ArticleQueryPageDto dto, Boolean highlight, QueryColumn... columns)`
  - `ArticleVo getVoById(Long id)`、`Article getById(Long id)`
  - `ArticleVo update(ArticleUpdateDto dto)`、`Boolean deleteById(Long id)`
  - `Boolean addLikes(Long articleId, Long likes)`、`Set<Long> addLikes(Map<String, Long> likes)`

- `ArticleRedisService`（节选）
  - `Boolean cacheOrUpdateArticle(ArticleVo)`、`ArticleVo getCachedArticle(Long)`、`Boolean removeArticleCache(Long)`
  - `void increaseView(Long articleId)`、`Map<String, Long> getViewsAndDelete()`、`void decreaseViews(Long articleId, Long views)`
  - `Boolean like(Long id)`、`Boolean unlike(Long id)`、`Map<String, Long> getLikesAndRemove()`

- `MqRedesService`
  - `void postArticle(Article article)`、`void updateArticle(ArticleUpdateDto dto)`、`void deleteArticle(Long id)`
  - `void like(Long id)`、`void unlike(Long id)`

- `JwtFilter`
  - `doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)`：解析/校验 token、加载权限写入 `SecurityContext`，异常场景交由 `EntryPoint` 或放行

---

## 14. 环境与配置项（节选）

- `custom.elasticsearch.*`（`ElasticSearchConfig`）：
  - `host`：ES 地址（形如 http://host:9200）
  - `maxResultWindow`：最大分页窗口
  - 环境变量：`ELASTIC_SEARCH_USERNAME`、`ELASTIC_SEARCH_PASSWORD`
- JWT：`JwtProperties.secret`（Base64）、`JwtProperties.expiration`（毫秒）
- 数据源、Redis、RabbitMQ：采用 Spring Boot 标准配置（application.yml/application.properties 中管理）

---

## 15. 优化方向

- 为 MQ 增加明确的死信队列与重试策略（`MqMessageRecoverConfig` 已存在类名，建议补充具体策略说明）。
- 对 Redis Key 规范进行集中常量化，补充文档说明 Key 模式与过期策略。
- 对 MyBatis-Flex 的 Mapper 与实体建模进行 README 化，附建表 SQL 与字段说明（`src/main/resources/sql` 已存在）。
- 为 `structure.md` 后续维护提供脚本（如基于注解/包扫描生成文档的 Gradle/Maven 插件）。

