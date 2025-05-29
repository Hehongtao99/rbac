# 户外广告管理系统

基于SpringBoot3.x + MyBatis-Plus + Vue3 + Element Plus的户外广告管理系统

## 技术栈

### 后端
- SpringBoot 3.2.0
- MyBatis-Plus 3.5.4
- MySQL 8.0
- JWT
- Lombok
- BCrypt密码加密

### 前端
- Vue 3.3.4
- Vue Router 4.2.4
- Element Plus 2.3.8
- Axios 1.4.0
- Animate.css 4.1.1
- Vite 4.4.5

## 功能特性

- ✅ 用户注册/登录（取消参数校验）
- ✅ JWT身份认证
- ✅ RBAC权限模型
- ✅ 动态菜单系统
- ✅ 功能按钮权限控制
- ✅ 用户完整的增删改查功能
- ✅ 角色完整的增删改查功能
- ✅ 用户角色管理
- ✅ 管理员权限分配
- ✅ 管理员超级权限（始终显示所有菜单和按钮）
- ✅ 实时权限更新（无需刷新页面）
- ✅ 响应式设计
- ✅ 动画效果
- ✅ 密码加密存储
- ✅ 户外广告位管理
- ✅ 广告申请审核功能
- ✅ 组织架构管理
- ✅ 修改密码功能

## 项目结构

```
outdoor-ad-system/
├── src/main/java/com/example/          # 后端源码
│   ├── entity/                         # 实体类
│   ├── dto/                           # 数据传输对象
│   ├── vo/                            # 视图对象
│   ├── mapper/                        # MyBatis映射器
│   ├── service/                       # 业务逻辑层
│   ├── controller/                    # 控制器层
│   ├── config/                        # 配置类
│   ├── util/                          # 工具类
│   └── common/                        # 通用类
├── src/main/resources/                # 资源文件
│   └── application.yml                # 配置文件
├── frontend/                          # 前端项目
│   ├── src/
│   │   ├── views/                     # 页面组件
│   │   ├── api/                       # API接口
│   │   ├── utils/                     # 工具类
│   │   └── router/                    # 路由配置
│   ├── package.json                   # 前端依赖
│   └── vite.config.js                 # Vite配置
├── database/                          # 数据库文件
│   └── init.sql                       # 初始化SQL
├── pom.xml                           # Maven配置
└── README.md                         # 项目说明
```

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Node.js 16+

### 2. 数据库初始化

```sql
-- 执行database/init.sql文件初始化数据库
mysql -u root -p < database/init.sql
```

### 3. 后端启动

```bash
# 修改application.yml中的数据库连接信息
# 启动SpringBoot应用
mvn spring-boot:run
```

### 4. 前端启动

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

### 5. 访问系统

- 前端地址：http://localhost:3000
- 后端地址：http://localhost:8080

## 默认账户

| 用户名 | 密码   | 角色   |
|--------|--------|--------|
| admin  | 123456 | 管理员 |
| user   | 123456 | 用户   |

## 权限控制

系统采用RBAC（基于角色的访问控制）模型：

- **菜单权限**：根据用户角色动态显示侧边栏菜单
- **按钮权限**：使用 `v-if="hasPermission('权限码')"` 控制按钮显示
- **角色分配**：管理员可以为用户分配角色
- **权限分配**：管理员可以为角色分配菜单和按钮权限
- **管理员超级权限**：拥有ADMIN角色编码的用户始终显示所有菜单和按钮，不受权限分配影响

## API接口

### 认证接口

- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `GET /api/auth/userinfo` - 获取用户信息

### 管理员接口

#### 用户管理
- `GET /api/admin/users` - 获取所有用户
- `POST /api/admin/users` - 新增用户
- `GET /api/admin/users/{id}` - 获取单个用户信息
- `PUT /api/admin/users/{id}` - 更新用户信息
- `DELETE /api/admin/users/{id}` - 删除用户（软删除）
- `POST /api/admin/assign-role` - 分配用户角色

#### 角色权限管理
- `GET /api/admin/roles` - 获取所有角色
- `POST /api/admin/roles` - 新增角色
- `GET /api/admin/roles/{id}` - 获取单个角色信息
- `PUT /api/admin/roles/{id}` - 更新角色信息
- `DELETE /api/admin/roles/{id}` - 删除角色（软删除）
- `GET /api/admin/menus` - 获取菜单树
- `GET /api/admin/role-menus/{roleId}` - 获取角色菜单权限
- `POST /api/admin/assign-menu` - 分配角色菜单权限

## 数据库表结构

### sys_user (用户表)
- id: 用户ID
- username: 用户名
- password: 密码（BCrypt加密）
- nickname: 昵称
- email: 邮箱
- phone: 手机号
- status: 状态（0-禁用，1-启用）

### sys_role (角色表)
- id: 角色ID
- role_name: 角色名称
- role_code: 角色编码
- description: 角色描述
- status: 状态（0-禁用，1-启用）

### sys_user_role (用户角色关联表)
- id: 主键ID
- user_id: 用户ID
- role_id: 角色ID

### sys_menu (菜单表)
- id: 菜单ID
- menu_name: 菜单名称
- menu_code: 菜单编码
- parent_id: 父菜单ID
- menu_type: 菜单类型（MENU-菜单，BUTTON-按钮）
- path: 路由路径
- component: 组件路径
- icon: 图标
- sort: 排序

### sys_role_menu (角色菜单关联表)
- id: 主键ID
- role_id: 角色ID
- menu_id: 菜单ID

## 开发规范

1. 使用MyBatis-Plus的LambdaQuery，禁止使用注解方式
2. 使用Lombok避免手写getter/setter
3. 严禁使用外键约束
4. 前端路径引用使用相对路径，不使用@符号
5. 遵循MVC架构，Controller层不写业务逻辑
6. 使用统一的返回结果格式

## 许可证

MIT License 