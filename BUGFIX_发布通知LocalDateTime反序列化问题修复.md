# 发布通知LocalDateTime反序列化问题修复

## 问题描述

在发布通知功能中出现以下错误：
```
JSON parse error: Cannot deserialize value of type `java.time.LocalDateTime` from String "2025-05-09 00:00:00": Failed to deserialize java.time.LocalDateTime: (java.time.format.DateTimeParseException) Text '2025-05-09 00:00:00' could not be parsed at index 10
```

## 问题原因

1. **前端发送格式**：前端使用Element Plus的日期选择器，设置为`"YYYY-MM-DD HH:mm:ss"`格式
2. **后端期望格式**：Spring Boot默认的LocalDateTime反序列化器期望ISO 8601格式或其他标准格式
3. **格式不匹配**：`"2025-05-09 00:00:00"`格式与默认的LocalDateTime反序列化格式不匹配

## 解决方案

### 方案1：配置全局Jackson日期格式（主要方案）

**文件：** `src/main/resources/application.yml`

```yaml
spring:
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: Asia/Shanghai
    serialization:
      write-dates-as-timestamps: false
    deserialization:
      fail-on-unknown-properties: false
```

**说明：**
- `date-format`: 设置全局日期格式
- `time-zone`: 设置时区为上海（北京时间）
- `write-dates-as-timestamps`: 禁用时间戳格式输出
- `fail-on-unknown-properties`: 容忍未知属性

### 方案2：创建Jackson配置类（保险方案）

**文件：** `src/main/java/com/example/config/JacksonConfig.java`

```java
@Configuration
public class JacksonConfig {

    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FORMAT);

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return Jackson2ObjectMapperBuilder.json()
                .modules(javaTimeModule())
                .build();
    }

    @Bean
    public JavaTimeModule javaTimeModule() {
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DATETIME_FORMATTER));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DATETIME_FORMATTER));
        return module;
    }
}
```

**说明：**
- 专门针对LocalDateTime类型配置序列化和反序列化
- 使用自定义的DateTimeFormatter
- 确保前后端日期格式一致

### 方案3：前端数据处理优化

**文件：** `frontend/src/views/NoticeManagement.vue`

```javascript
// 确认提交
const confirmNotice = async () => {
  // ... 表单验证 ...
  
  // 处理提交数据，确保日期格式正确
  const submitData = { ...noticeForm }
  
  // 如果没有设置发布时间但状态是发布，则设置为当前时间
  if (submitData.status === 1 && !submitData.publishTime) {
    const now = new Date()
    submitData.publishTime = now.getFullYear() + '-' + 
      String(now.getMonth() + 1).padStart(2, '0') + '-' + 
      String(now.getDate()).padStart(2, '0') + ' ' + 
      String(now.getHours()).padStart(2, '0') + ':' + 
      String(now.getMinutes()).padStart(2, '0') + ':' + 
      String(now.getSeconds()).padStart(2, '0')
  }
  
  // 如果状态不是发布，清空发布时间
  if (submitData.status !== 1) {
    submitData.publishTime = null
  }
  
  // 提交数据...
}
```

**说明：**
- 智能处理发布时间：发布状态但未设置时间时自动设置当前时间
- 非发布状态时清空发布时间
- 增强错误处理和日志记录

## 涉及的数据传输

### NoticeDTO结构
```java
@Data
public class NoticeDTO {
    private Long id;
    private String title;
    private String content;
    private Integer status;
    private LocalDateTime publishTime; // 关键字段
}
```

### 前端Element Plus日期选择器配置
```vue
<el-date-picker
  v-model="noticeForm.publishTime"
  type="datetime"
  placeholder="选择发布时间"
  format="YYYY-MM-DD HH:mm:ss"
  value-format="YYYY-MM-DD HH:mm:ss"
/>
```

## 测试验证

### 测试用例
1. **新增通知（草稿状态）** - publishTime为null
2. **新增通知（发布状态，指定时间）** - 使用用户选择的时间
3. **新增通知（发布状态，未指定时间）** - 自动使用当前时间
4. **编辑通知，修改状态** - 状态变化时正确处理时间
5. **直接发布按钮** - 使用当前时间发布

### 验证点
- [x] 日期格式正确序列化/反序列化
- [x] 时区处理正确（Asia/Shanghai）
- [x] 空值处理正确
- [x] 前后端数据格式一致

## 相关文件修改

### 后端文件
- `src/main/resources/application.yml` - 全局Jackson配置
- `src/main/java/com/example/config/JacksonConfig.java` - Jackson配置类
- `src/main/java/com/example/dto/NoticeDTO.java` - 数据传输对象
- `src/main/java/com/example/service/impl/NoticeServiceImpl.java` - 业务逻辑

### 前端文件
- `frontend/src/views/NoticeManagement.vue` - 通知管理页面
- `frontend/src/api/notice.js` - API接口

## 技术要点

1. **时区一致性** - 前后端都使用Asia/Shanghai时区
2. **格式统一** - 统一使用"yyyy-MM-dd HH:mm:ss"格式
3. **空值处理** - 正确处理null值的序列化
4. **容错机制** - 提供详细的错误信息和日志

## 预防措施

1. **统一标准** - 项目中所有日期时间字段都使用相同格式
2. **文档说明** - 在API文档中明确日期时间格式要求
3. **测试覆盖** - 对日期时间相关功能进行充分测试
4. **配置管理** - 将日期格式配置化，便于后续调整 