# 流式解析调试测试

## 测试用例：长文本流式解析

### 测试内容（复制以下内容到AI提取页面）

```
1. 以下哪个是Java的关键字？
A. class
B. Class
C. CLASS
D. clazz
答案：A

2. Java是面向对象的编程语言吗？
答案：是

3. 以下哪些是Java的基本数据类型？
A. int
B. String
C. double
D. boolean
答案：A、C、D

4. Spring框架的核心特性是什么？
答案：依赖注入（DI）和面向切面编程（AOP）

5. 判断：Java中的String是可变的。
答案：错误

6. 以下关于Java集合框架的说法正确的是？
A. ArrayList是线程安全的
B. HashMap允许null键和null值
C. TreeSet中的元素是无序的
D. LinkedList实现了RandomAccess接口
答案：B

7. Java中的垃圾回收机制的作用是什么？
答案：自动管理内存，回收不再使用的对象所占用的内存空间

8. 以下哪些是Java的访问修饰符？
A. public
B. private
C. protected
D. default
答案：A、B、C、D

9. 判断：Java中的接口可以包含具体的方法实现。
答案：正确

10. 什么是Java中的多态？
答案：多态是指同一个接口可以有多种不同的实现方式，在运行时根据对象的实际类型来调用相应的方法

11. 以下哪个不是Java的基本数据类型？
A. byte
B. short
C. String
D. long
答案：C

12. Java中的final关键字的作用是什么？
答案：final关键字可以修饰类、方法和变量，表示不可改变的

13. 判断：Java支持多重继承。
答案：错误

14. 以下关于Java异常处理的说法正确的是？
A. try块必须与catch块一起使用
B. finally块中的代码一定会执行
C. 一个try块只能有一个catch块
D. throw和throws的作用相同
答案：B

15. Java中的static关键字的作用是什么？
答案：static关键字用于创建独立于对象的类成员，可以修饰变量、方法和代码块

16. 以下哪个是Java中的包装类？
A. int
B. Integer
C. double
D. boolean
答案：B

17. 判断：Java中的数组是对象。
答案：正确

18. 以下关于Java构造方法的说法错误的是？
A. 构造方法的名称必须与类名相同
B. 构造方法可以有返回值
C. 一个类可以有多个构造方法
D. 构造方法在创建对象时自动调用
答案：B

19. Java中的this关键字的作用是什么？
答案：this关键字用于引用当前对象，可以访问当前对象的属性和方法

20. 以下哪个不是Java的循环结构？
A. for
B. while
C. do-while
D. switch
答案：D
```

### 预期行为

1. **第一次解析**：
   - 应该提取前几道题目（大约1-10题）
   - 显示"继续解析"按钮
   - 进度条显示部分进度

2. **点击继续解析**：
   - 提取剩余题目
   - 如果还有内容，继续显示"继续解析"按钮
   - 最终提取完所有20道题目

### 调试信息检查

打开浏览器开发者工具，查看控制台输出：

1. **流式解析响应**：
   ```javascript
   {
     questionsCount: 5,  // 本次提取的题目数量
     hasMore: true,      // 是否还有更多内容
     nextPosition: 1234, // 下次开始位置
     currentPosition: 0, // 当前位置
     totalLength: 2468,  // 总长度
     processedContentLength: 1234 // 已处理长度
   }
   ```

2. **更新后的状态**：
   ```javascript
   {
     hasMoreContent: true,  // 应该为true才会显示继续解析按钮
     currentPosition: 1234,
     processedLength: 1234
   }
   ```

### 问题排查

如果没有显示"继续解析"按钮，检查：

1. **hasMore字段**：后端返回的hasMore是否为true
2. **内容长度**：输入内容是否足够长（建议>8000字符）
3. **分割逻辑**：后端是否正确识别了题目分割点

### 后端日志检查

查看后端日志，应该看到类似输出：
```
流式解析开始，总长度: 2468, 开始位置: 0
分段处理，当前段: 0 - 1234, 还有更多内容
本次处理内容长度: 1234
本次提取到 5 道题目，hasMore: true
```

如果看到：
```
最后一段处理，范围: 0 - 2468
本次提取到 20 道题目，hasMore: false
```

说明内容被当作一段处理了，需要检查分割逻辑。 