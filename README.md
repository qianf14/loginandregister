# Android 登录注册应用

一个基于 Material Design 设计的 Android 登录注册应用，提供美观的用户界面和完整的本地账户管理功能。

## 架构说明

本项目已重构为 **MVVM 架构**，核心页面（登录、注册、主页面）均采用 ViewModel 进行业务逻辑与 UI 解耦，提升了代码的可维护性和可扩展性。

- **Model**：数据实体与本地存储（如 User.java）
- **View**：Activity 负责 UI 展示和事件监听
- **ViewModel**：负责业务逻辑、输入校验、数据状态管理，使用 LiveData 实现 UI 响应式更新

## 功能特点

### 登录功能
- 支持用户名和密码登录
- 记住密码功能
- 记住最近登录用户（最多保存5个）
- 用户名自动完成提示
- 实时输入验证和错误提示

### 注册功能
- 新用户注册
- 用户名唯一性检查
- 密码强度验证
- 密码确认验证
- 实时错误提示

### 主页面
- 显示欢迎信息
- 显示当前登录用户
- 支持退出登录

### 安全特性
- 密码规则验证（最少8位，必须包含数字和字母）
- 本地安全存储（使用 SharedPreferences）
- 输入验证和错误提示

## 技术特点

- 使用 Material Design 3 组件
- 现代化的 UI 设计
- 响应式布局
- 输入框实时验证
- 数据持久化存储
- **MVVM 架构，业务逻辑与 UI 解耦**
- **ViewModel + LiveData 实现响应式 UI**

## 界面预览

- 登录界面：Material Design 风格的登录表单
- 注册界面：用户友好的注册流程
- 主页面：简洁的欢迎界面

## 开发环境

- Android Studio
- Minimum SDK: Android 21 (5.0)
- Target SDK: Android 33
- Java 8

## 使用说明

1. **注册新用户**
   - 点击登录页面的"注册"按钮
   - 输入用户名和密码
   - 确认密码
   - 点击注册按钮

2. **登录**
   - 输入用户名和密码
   - 可选择"记住密码"
   - 点击登录按钮
   - 支持从最近用户列表选择用户名

3. **退出登录**
   - 在主页面点击"退出登录"按钮
   - 将返回登录界面

## 项目结构

```
app/src/main/
├── java/com/example/loginandregister/
│   ├── model/                // 数据实体类（如 User.java）
│   ├── repository/           // 数据仓库（可扩展）
│   ├── viewmodel/            // ViewModel 层（如 LoginViewModel.java 等）
│   ├── LoginActivity.java    // 登录页面
│   ├── RegisterActivity.java // 注册页面
│   └── HomeActivity.java     // 主页面（原MainActivity）
└── res/
    ├── layout/
    │   ├── activity_login.xml    // 登录页面布局
    │   ├── activity_register.xml // 注册页面布局
    │   └── activity_main.xml     // 主页面布局
    └── values/
        ├── colors.xml
        ├── strings.xml
        └── themes.xml
```

## 安装说明

1. 克隆项目
```bash
git clone https://github.com/qianf14/loginandregister.git
```

2. 在 Android Studio 中打开项目

3. 运行项目在模拟器或实际设备上

## 贡献

欢迎提交 Issue 和 Pull Request 来帮助改进项目。

## 许可证

[MIT License](LICENSE) 