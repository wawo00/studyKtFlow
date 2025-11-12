# 快速参考卡 - Quick Reference Card

## 🚀 快速启动

### 1. 克隆并打开项目
```bash
git clone https://github.com/wawo00/studyKtFlow.git
cd studyKtFlow
# 使用 Android Studio 打开项目
```

### 2. 运行应用
- 连接 Android 设备或启动模拟器
- 点击 Run 按钮
- 应用将启动到登录页面

### 3. 测试账号
可以注册新账号或使用玩Android网站的测试账号

## 📱 应用导航流程

```
登录页面 → 注册/登录 → 首页文章列表
                         ↓
               点击文章查看详情 ← 点击FAB查看收藏
                         ↓
                   收藏/取消收藏
```

## 🏗️ 核心架构速查

### MVVM 层次
```kotlin
// Activity (View)
class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels()
    // 观察 ViewModel 的状态
}

// ViewModel
class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> = _state.asStateFlow()
}

// Repository
class AuthRepository {
    fun login(): Flow<Result<User>> = flow { /* ... */ }
}
```

### Flow 数据流
```kotlin
// 1. Repository 返回 Flow
fun getData(): Flow<Result<T>> = flow {
    emit(result)
}.flowOn(Dispatchers.IO)

// 2. ViewModel 收集并更新状态
viewModelScope.launch {
    repository.getData().collect { result ->
        _state.value = result.toState()
    }
}

// 3. Activity 观察状态
lifecycleScope.launch {
    viewModel.state.collect { state ->
        updateUI(state)
    }
}
```

## 🔑 关键类说明

### 数据模型
- `User` - 用户信息
- `Article` - 文章数据
- `ApiResponse<T>` - API 响应封装

### 网络层
- `ApiService` - Retrofit API 接口
- `RetrofitClient` - 网络客户端配置
- `PersistentCookieJar` - Cookie 管理

### 状态类型
```kotlin
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}
```

## 📝 常用代码片段

### 1. 在 Activity 中观察 Flow
```kotlin
lifecycleScope.launch {
    viewModel.state.collect { state ->
        when (state) {
            is State.Loading -> showLoading()
            is State.Success -> showSuccess(state.data)
            is State.Error -> showError(state.message)
        }
    }
}
```

### 2. 在 ViewModel 中执行异步操作
```kotlin
fun loadData() {
    viewModelScope.launch {
        _state.value = State.Loading
        repository.getData().collect { result ->
            _state.value = result.toState()
        }
    }
}
```

### 3. 在 Repository 中调用 API
```kotlin
fun getData(): Flow<Result<T>> = flow {
    try {
        val response = apiService.getData()
        if (response.errorCode == 0) {
            emit(Result.success(response.data!!))
        } else {
            emit(Result.failure(Exception(response.errorMsg)))
        }
    } catch (e: Exception) {
        emit(Result.failure(e))
    }
}.flowOn(Dispatchers.IO)
```

## 🔧 配置文件位置

```
项目根目录/
├── build.gradle              # 项目级配置
├── settings.gradle           # 项目设置
├── gradle.properties         # Gradle 属性
└── app/
    ├── build.gradle          # 应用级配置（依赖在这里）
    └── src/main/
        ├── AndroidManifest.xml  # 清单文件
        └── java/...            # 源代码
```

## 📚 文档导航

- **README.md** - 从这里开始，了解项目概览
- **ARCHITECTURE.md** - 深入理解架构设计
- **USAGE_GUIDE.md** - 查看详细使用示例
- **PROJECT_SUMMARY.md** - 查看完整实现总结

## 🐛 常见问题速查

### Q: 编译错误？
A: 确保使用 Android Studio Arctic Fox 或更高版本，JDK 8+

### Q: 网络请求失败？
A: 检查 AndroidManifest.xml 中的网络权限和 usesCleartextTraffic 设置

### Q: Cookie 没有保存？
A: 确保 Application 类正确初始化了 RetrofitClient

### Q: Flow 没有收集到数据？
A: 检查是否在协程作用域中调用 collect

### Q: 状态更新但 UI 不刷新？
A: 确保在 lifecycleScope 中收集 StateFlow

## 📦 依赖版本

| 库 | 版本 |
|---|---|
| Kotlin | 1.9.0 |
| Coroutines | 1.7.3 |
| Retrofit | 2.9.0 |
| OkHttp | 4.11.0 |
| Lifecycle | 2.6.2 |
| Material | 1.10.0 |

## 🎯 学习路径建议

1. **第一天**: 阅读 README.md，了解项目结构
2. **第二天**: 学习 ARCHITECTURE.md，理解架构设计
3. **第三天**: 运行项目，跟踪代码执行流程
4. **第四天**: 阅读 USAGE_GUIDE.md，学习 Flow 使用
5. **第五天**: 修改代码，添加新功能练习

## 🔗 有用链接

- [Kotlin Flow 官方文档](https://kotlinlang.org/docs/flow.html)
- [Android ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [玩Android API](https://www.wanandroid.com/blog/show/2)
- [Retrofit 文档](https://square.github.io/retrofit/)

## 💡 快速命令

```bash
# 查看项目结构
tree app/src/main/java

# 查看所有 Kotlin 文件
find . -name "*.kt" | grep -v build

# 查看所有布局文件
find app/src/main/res/layout -name "*.xml"

# 统计代码行数
find app/src/main/java -name "*.kt" | xargs wc -l
```

---
**提示**: 这是一个学习项目，随意修改和实验！
