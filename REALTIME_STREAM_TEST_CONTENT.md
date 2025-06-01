# 实时流式提取测试内容

## 使用说明
将以下内容复制到AI提取页面的文本框中，然后点击"实时流式提取"按钮，您将看到AI每提取到一个题目就立即显示在屏幕上。

## 测试内容

1. 以下哪个是Java的关键字？
A. class
B. Class
C. CLASS
D. clazz
答案：A
解析：class是Java的关键字，用于定义类。Class是Java中的一个类名，CLASS和clazz都不是关键字。

2. Java是面向对象的编程语言吗？
答案：是
解析：Java是一种面向对象的编程语言，支持封装、继承和多态等面向对象的特性。

3. 以下哪些是Java的基本数据类型？
A. int
B. String
C. double
D. boolean
答案：A、C、D
解析：Java的基本数据类型包括byte、short、int、long、float、double、char、boolean。String是引用类型，不是基本数据类型。

4. Spring框架的核心特性是什么？
答案：依赖注入（DI）和面向切面编程（AOP）
解析：Spring框架的两个核心特性是依赖注入（Dependency Injection）和面向切面编程（Aspect-Oriented Programming），这两个特性使得Spring成为了一个强大的企业级应用开发框架。

5. 判断：Java中的String是可变的。
答案：错误
解析：Java中的String对象是不可变的（immutable），一旦创建就不能修改。如果需要可变的字符串，可以使用StringBuilder或StringBuffer。

6. 以下关于Java集合框架的说法正确的是？
A. ArrayList是线程安全的
B. HashMap允许null键和null值
C. TreeSet中的元素是无序的
D. LinkedList实现了RandomAccess接口
答案：B
解析：ArrayList不是线程安全的；HashMap允许一个null键和多个null值；TreeSet中的元素是有序的；LinkedList没有实现RandomAccess接口。

7. Java中的垃圾回收机制的作用是什么？
答案：自动管理内存，回收不再使用的对象所占用的内存空间
解析：Java的垃圾回收机制（Garbage Collection，GC）是Java虚拟机的一个重要特性，它能够自动检测和回收不再被程序引用的对象，释放内存空间，避免内存泄漏。

8. 以下哪些是Java的访问修饰符？
A. public
B. private
C. protected
D. default
答案：A、B、C、D
解析：Java有四种访问修饰符：public（公共的）、private（私有的）、protected（受保护的）和default（默认的，包访问权限）。

9. 判断：Java中的接口可以包含具体的方法实现。
答案：正确
解析：从Java 8开始，接口可以包含默认方法（default method）和静态方法（static method），这些方法可以有具体的实现。

10. 什么是Java中的多态？
答案：多态是指同一个接口可以有多种不同的实现方式，在运行时根据对象的实际类型来调用相应的方法
解析：多态是面向对象编程的重要特性之一，它允许不同类的对象对同一消息做出响应，但具体的响应行为由各个对象的实际类型决定。

11. 以下哪个不是Java的基本数据类型？
A. byte
B. short
C. String
D. long
答案：C
解析：String是Java中的引用类型，不是基本数据类型。Java的8种基本数据类型是：byte、short、int、long、float、double、char、boolean。

12. Java中的final关键字的作用是什么？
答案：final关键字可以修饰类、方法和变量，表示不可改变的
解析：final修饰类时，该类不能被继承；修饰方法时，该方法不能被重写；修饰变量时，该变量不能被重新赋值。

13. 判断：Java支持多重继承。
答案：错误
解析：Java不支持类的多重继承，一个类只能继承一个父类。但是Java支持接口的多重继承，一个接口可以继承多个接口，一个类也可以实现多个接口。

14. 以下关于Java异常处理的说法正确的是？
A. try块必须与catch块一起使用
B. finally块中的代码一定会执行
C. 一个try块只能有一个catch块
D. throw和throws的作用相同
答案：B
解析：try块可以单独与finally块使用；finally块中的代码在正常情况下一定会执行；一个try块可以有多个catch块；throw用于抛出异常，throws用于声明方法可能抛出的异常。

15. Java中的static关键字的作用是什么？
答案：static关键字用于创建独立于对象的类成员，可以修饰变量、方法和代码块
解析：static修饰的成员属于类而不是对象，可以通过类名直接访问，不需要创建对象实例。static变量在类加载时初始化，所有对象共享同一份static变量。

16. 以下哪个是正确的Java数组声明方式？
A. int[] arr = new int[5];
B. int arr[] = new int[5];
C. int[] arr = {1, 2, 3, 4, 5};
D. 以上都正确
答案：D
解析：Java支持多种数组声明方式，包括指定大小的声明和直接初始化的声明。

17. Java中的包装类的作用是什么？
答案：包装类为基本数据类型提供了对象形式，使基本类型可以参与面向对象的操作
解析：Java为每个基本数据类型都提供了对应的包装类，如Integer、Double、Boolean等，这样基本类型就可以作为对象使用，参与集合操作等。

18. 判断：Java中的方法重载要求方法名相同，参数列表不同。
答案：正确
解析：方法重载（Overloading）是指在同一个类中定义多个同名方法，但这些方法的参数列表必须不同（参数个数、类型或顺序不同）。

19. 以下关于Java构造方法的说法错误的是？
A. 构造方法的名称必须与类名相同
B. 构造方法可以有返回值
C. 一个类可以有多个构造方法
D. 如果没有定义构造方法，系统会提供默认构造方法
答案：B
解析：构造方法不能有返回值，包括void也不能有。构造方法用于创建对象时初始化对象的状态。

20. Java中的this关键字的作用是什么？
答案：this关键字用于引用当前对象，可以访问当前对象的属性和方法
解析：this关键字在实例方法中使用，用于区分局部变量和实例变量，或者调用当前对象的其他方法和构造方法。 