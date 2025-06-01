# JSON修复功能测试用例

## 测试场景1：截断的JSON

**输入（模拟AI响应被截断）：**
```json
[
  {
    "title": "下列选项中不是项目与日常运作的区别的是",
    "content": "下列选项中不是项目与日常运作的区别的是( )。",
    "type": 1,
    "options": [
      "项目是以目标为导向的，日常运作是通过效率和有效性体现的。",
      "项目是通过项目经理及其团队工作完成的，而日常运作是职能式的线性管理。",
      "项目需要有专业知识的人来完成，而日常运作的完成无需特定专业知识。",
      "项目是一次性的，日常运作是重复性的。"
    ],
    "answer": "C",
    "analysis": "",
    "difficulty": 1,
    "score": 5
  },
  {
    "title": "以下都是日常运作和项目的共同之处，除了",
    "content": "以下都是日常运作和项目的共同之处，除了( )。",
    "type": 1,
    "options": [
      "由人来做",
      "受限于有限的资源",
      "需要规划、执行和控制",
      "都是重复性工作"
    ],
    "answer": "D",
    "analysis": "",
    "difficulty": 1,
    "score": 5
  },
  {
    "title": "下列选项中不是 PMBOK 的知识域的是",
    "content": "下列选项中不是 PMBOK 的知识域的是( )。",
    "type": 1,
    "options": [
      "招聘管理",
      "质量管理"
    ]
  }
]
```

**期望输出：**
- 系统应该能够识别JSON被截断
- 自动修复为完整的JSON数组
- 提取前2道完整的题目
- 忽略第3道不完整的题目

**修复后的JSON：**
```json
[
  {
    "title": "下列选项中不是项目与日常运作的区别的是",
    "content": "下列选项中不是项目与日常运作的区别的是( )。",
    "type": 1,
    "options": [
      "项目是以目标为导向的，日常运作是通过效率和有效性体现的。",
      "项目是通过项目经理及其团队工作完成的，而日常运作是职能式的线性管理。",
      "项目需要有专业知识的人来完成，而日常运作的完成无需特定专业知识。",
      "项目是一次性的，日常运作是重复性的。"
    ],
    "answer": "C",
    "analysis": "",
    "difficulty": 1,
    "score": 5
  },
  {
    "title": "以下都是日常运作和项目的共同之处，除了",
    "content": "以下都是日常运作和项目的共同之处，除了( )。",
    "type": 1,
    "options": [
      "由人来做",
      "受限于有限的资源",
      "需要规划、执行和控制",
      "都是重复性工作"
    ],
    "answer": "D",
    "analysis": "",
    "difficulty": 1,
    "score": 5
  }
]
```

## 测试场景2：完全损坏的JSON

**输入：**
```
这不是一个有效的JSON格式
```

**期望输出：**
- 返回空数组 `[]`
- 不抛出异常

## 测试场景3：部分可解析的JSON

**输入：**
```json
[
  {
    "title": "完整题目1",
    "content": "这是一道完整的题目",
    "type": 1,
    "options": ["A", "B", "C", "D"],
    "answer": "A",
    "analysis": "",
    "difficulty": 1,
    "score": 5
  },
  {
    "title": "不完整题目",
    "content": "这道题目不完整"
  }
]
```

**期望输出：**
- 提取第1道完整的题目
- 忽略第2道不完整的题目
- 返回包含1道题目的数组

## 测试方法

### 1. 单元测试
```java
@Test
public void testFixIncompleteJson() {
    String incompleteJson = "[{\"title\":\"test\",\"content\":\"test\"}";
    String fixedJson = aiQuestionService.fixIncompleteJson(incompleteJson);
    assertEquals("[]", fixedJson);
}

@Test
public void testExtractPartialQuestions() {
    String partialResponse = "[{\"title\":\"test\",\"type\":1,\"answer\":\"A\"}]";
    List<AiExtractedQuestion> questions = aiQuestionService.extractPartialQuestions(partialResponse);
    assertEquals(1, questions.size());
}
```

### 2. 集成测试
1. 准备长文本内容（超过8000字符）
2. 调用AI提取接口
3. 验证是否能正确处理截断响应
4. 检查提取的题目数量和质量

### 3. 用户界面测试
1. 在前端输入长文本
2. 点击"预览提取结果"
3. 观察错误处理和用户提示
4. 验证是否建议使用流式解析

## 性能指标

- **修复成功率**：>95%
- **部分提取成功率**：>90%
- **响应时间**：<1秒
- **内存使用**：稳定，无内存泄漏

## 监控指标

- JSON修复次数
- 部分提取成功次数
- 用户切换到流式解析的频率
- 错误类型分布

## 改进建议

1. **优化分割算法**：更智能地识别题目边界
2. **增强容错能力**：处理更多异常格式
3. **用户体验**：提供更详细的错误信息
4. **性能优化**：减少JSON解析开销 