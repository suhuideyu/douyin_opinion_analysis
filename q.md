# 问答参考

---

## 1. 请简单介绍一下你的项目

本项目是一个**抖音多视频评论分析与可视化平台**，解决短视频创作者和运营人员"看得见评论但看不清数据"的痛点。

**核心流程**：用户粘贴抖音视频/图文链接 → 系统自动爬取评论 → 清洗去重 + 过滤表情标签 → SnowNLP 情感分析 → 多维可视化大屏展示。

**架构设计**：
- **Python 数据层**：DrissionPage 采集评论，SnowNLP 情感分析，jieba + Flask 常驻分词服务
- **SpringBoot 业务层**：RESTful API（Swagger 文档），BCrypt + JWT 认证鉴权，MyBatis-Plus 持久化，内存缓存加速，启动时自动拉起 Flask
- **Vue3 展示层**：Element Plus + ECharts（useECharts 组合式函数封装），Pinia 跨页面状态共享

**功能模块**：用户认证、数据采集（短链接自动解析，兼容 video/note 两种链接）、数据清洗（多选批量入库 + 原始数据导出）、多维分析（地区分布、情感占比、时间趋势、词云、桑基图、核心话题洞察）、评论列表、用户管理。

---

## 2. 项目中运用的技术栈说明

### 后端：SpringBoot 2.7 + MyBatis-Plus 3.5 + MySQL 8.0

**SpringBoot**：Java 主流微框架，自动配置 + 起步依赖。`@RestController` + `@RequestMapping` 定义接口，拦截器统一 JWT 校验，`@RestControllerAdvice` 全局异常处理，SLF4J 结构化日志。

**MyBatis-Plus**：MyBatis 增强工具，`BaseMapper<T>` 内置 CRUD，`LambdaQueryWrapper` 类型安全的查询构造器。
> 场景：`commentMapper.selectList(new LambdaQueryWrapper<Comment>().eq(Comment::getVideoId, videoId))` 替代手写 SQL，编译期字段名检查。

**BCrypt + JWT**：BCrypt 加盐哈希存储密码（兼容老 MD5 平滑过渡），JWT 签发无状态令牌，拦截器统一校验。

**Swagger (springdoc-openapi)**：API 文档自动生成，启动后访问 `/swagger-ui.html` 即可在线查看和调试所有接口。

**SLF4J + Logback**：统一日志框架，替代 `System.out.println`，支持日志级别控制和格式化输出。

**内存缓存**：`ConcurrentHashMap` + 过期时间戳实现轻量缓存，TTL 30 分钟，分析结果首次计算后缓存复用。

### 前端：Vue3 + Element Plus + ECharts 5

**Vue3 组合式 API**：`ref` / `reactive` 响应式数据，`watch` 监听变化驱动图表重绘。

**useECharts 组合式函数**：封装 ECharts 初始化/销毁/响应式绑定，消除 4 个图表组件中 90% 重复的 `onMounted/watch/onBeforeUnmount` 模板代码。

**Pinia**：Vue3 官方状态管理库。`useAppStore` 保存当前视频 ID，总览页和评论页共享选中状态，跨页面切换不丢失。

**ECharts 5**：百度开源可视化库，支持中国地图、环形饼图、平滑折线图、桑基图（128 次布局迭代自动排列）、词云（echarts-wordcloud 扩展）。

**Element Plus**：饿了么 UI 组件库，提供 Table（斑马纹+情感药丸标签）、Select（分组筛选）、Form 等企业级组件。

### 数据层：Python + DrissionPage + SnowNLP + Flask + jieba

**DrissionPage**：浏览器自动化库，直接操控 Chrome，比 Selenium 更轻量。监听抖音评论 API 逐页截取 JSON 保存为 CSV。

**SnowNLP**：中文自然语言处理库，基于贝叶斯模型的文本情感分析，返回 0~1 的情感得分。
> 场景：替代传统词典匹配方案，自动处理否定、程度、语境，判断评论为积极(>0.6)、消极(<0.4)、中性。

**jieba 分词**：中文分词库，支持精确模式和 TF-IDF 关键词提取，用于词云生成和核心话题关键词分析。

**Flask**：Python 轻量 Web 框架。将 jieba 包装为 HTTP 服务（`/wordcloud?video_id=xxx`），模型启动时预加载。SpringBoot 启动时自动拉起 Flask 进程，关闭时自动销毁，无需单独运维。

---

## 3. 主要核心功能讲解

### 3.1 短链接自动解析（兼容视频 + 图文）
用户可粘贴三种格式：**纯数字 ID**（直接使用）、**长链接** `douyin.com/video/xxx` 或 `douyin.com/note/xxx`（正则提取 ID）、**短链接** `v.douyin.com/xxx`（Java 端原样传给 Python，DrissionPage 真实浏览器打开短链后从 `dp.url` 提取真实视频 ID）。兼容 `/video/` 和 `/note/` 两种路径格式。

### 3.2 情感分析（SnowNLP 贝叶斯模型）
弃用传统词典匹配方案，改用 SnowNLP。SnowNLP 基于中文电商评论语料训练的贝叶斯模型，能自动理解"不难看"（正面）、"虽然可爱但是太贵了"（偏向正面）等复杂表达。得分 >0.6 判积极，<0.4 判消极，中间判中性。评论中的方括号表情标签（如 `[赞]` `[捂脸]`）在分析前自动过滤，避免干扰。

### 3.3 桑基图（评论流向图）
展示 **时段（每小时）→ 情感（积极/中性/消极）→ 地区（直辖市/自治区/23省/港澳台/其他）** 的评论流动路径，点赞数作为流量权重。后端按三维分组聚合，前端 ECharts sankey 128 次布局迭代自动排列节点，情感节点着色（绿/灰/红），容器高度 700px 避免重叠。

### 3.4 核心话题洞察（结论前置 + 风险优先）
输出结构化三段式：
1. **关键结论**：整体表现（评论数+好评率+情感倾向）+ 核心亮点 + **风险预警**（负面≥40% 的话题标红突出）
2. **话题数据明细**：每个词对场景列出评论数、正面/负面占比、评价，风险话题加注"重点风险"
3. **总结**：最受关注的方向 + 主要动机 + Top 地区 + 争议点 + 优化建议

方便运营人员一眼看到问题所在，快速决策。

---

## 4. 加分：棘手问题与解决方案

### 问题 1：CSV 编码不一致导致"清洗后 0 条"
**现象**：爬虫写 CSV 用 `utf-8-sig`（BOM），清洗读用 `utf-8`，首列标题 `﻿昵称` 无法匹配，300 条全被丢。
**解决**：统一读写均为 `utf-8-sig`。

### 问题 2：jieba 启动日志混入 JSON 导致解析失败
**现象**：`ProcessBuilder` 调 Python 分词时，jieba 的 "Building prefix dict..." 启动日志经 `redirectErrorStream` 与 JSON 混在一起。
**解决**：先改为逐行过滤（只取 `[...]` 格式的行）；最终升级为 Flask 常驻服务，模型只加载一次，彻底规避。

### 问题 3：桑基图节点过多导致重叠溢出
**现象**：24 时段 × 3 情感 × 30+ 地区 = 60+ 节点挤在 400px 卡片内。
**解决**：时段恢复每小时一组但图表高度增至 700px，128 次布局迭代，`nodeAlign: 'justify'`。情感节点手动分布垂直位置防止三者挤在一起。

### 问题 4：ECharts 图表首次加载空白
**现象**：组件 `onMounted` 时数据为空不初始化，异步数据到达后 `watch` 判断 `if(chart)` 但 chart 为 null，永远不渲染。
**解决**：`initChart()` 无条件调用，先 `dispose()` 再 `init()`。后抽象为 `useEChartsWithData()` 组合式函数统一处理，消除 4 个组件中重复代码。

### 问题 5：BCrypt 升级兼容老 MD5 密码
**现象**：老用户密码 MD5 存储，改 BCrypt 后无法登录。
**解决**：`login()` 判断密码前缀——`$2a$`/`$2b$` 走 BCrypt，否则走 MD5。修改密码后统一写 BCrypt，实现平滑迁移。

### 问题 6：情感分析准确度低
**现象**：基于词典匹配的规则引擎只能识别简单词汇，"不难看"误判为负面、"超级可爱"和"可爱"得分相同。
**解决**：弃用词典方案，改用 SnowNLP 贝叶斯模型。自动处理否定、程度和语境，无需手工维护词典和规则。同时过滤评论中的抖音表情标签 `[赞][哭哭][黑脸]`，避免表情文字干扰分析。
