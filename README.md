# studyKtFlow - Kotlin Flow 学习示例

这是一个使用 MVVM 架构和 Kotlin Flow 构建的 Android 示例应用，集成了 [玩Android API](https://www.wanandroid.com/blog/show/2)，帮助 Android 开发者学习 Kotlin Flow 的实际应用。

## 功能特性

### 1. 用户认证
- ✅ 用户登录
- ✅ 用户注册
- ✅ Cookie 自动持久化（使用 SharedPreferences）

### 2. 文章浏览
- ✅ 首页文章列表（RecyclerView）
- ✅ 下拉刷新
- ✅ 文章详情页
- ✅ 点击列表项查看详情

### 3. 收藏功能
- ✅ 收藏/取消收藏文章
- ✅ 收藏状态实时更新
- ✅ 个人收藏列表
- ✅ 通过 FloatingActionButton 访问收藏

## 技术栈

### 架构
- **MVVM** - Model-View-ViewModel 架构模式
- **Repository Pattern** - 数据层抽象
- **单一数据流** - 使用 StateFlow 管理状态

### 核心库
- **Kotlin Flow** - 异步数据流处理
- **Coroutines** - 协程用于异步操作
- **Retrofit** - 网络请求
- **OkHttp** - HTTP 客户端
- **Gson** - JSON 序列化/反序列化
- **ViewModel** - UI 相关数据管理
- **LiveData** - 生命周期感知的数据观察

### UI 组件
- **RecyclerView** - 列表展示
- **SwipeRefreshLayout** - 下拉刷新
- **Material Components** - Material Design 组件
- **ConstraintLayout** - 灵活的布局管理

## 项目结构

```
app/src/main/java/com/example/studyktflow/
├── data/
│   ├── model/              # 数据模型
│   │   ├── ApiResponse.kt  # API 响应封装
│   │   ├── Article.kt      # 文章模型
│   │   └── User.kt         # 用户模型
│   ├── network/            # 网络层
│   │   ├── ApiService.kt   # API 接口定义
│   │   ├── CookieJar.kt    # Cookie 持久化
│   │   └── RetrofitClient.kt # Retrofit 客户端
│   └── repository/         # 数据仓库
│       ├── AuthRepository.kt    # 认证相关
│       └── ArticleRepository.kt # 文章相关
├── ui/
│   ├── login/              # 登录模块
│   │   ├── LoginActivity.kt
│   │   └── LoginViewModel.kt
│   ├── main/               # 主页模块
│   │   ├── MainActivity.kt
│   │   ├── MainViewModel.kt
│   │   └── ArticleAdapter.kt
│   ├── detail/             # 详情模块
│   │   ├── ArticleDetailActivity.kt
│   │   └── ArticleDetailViewModel.kt
│   └── favorites/          # 收藏模块
│       ├── FavoritesActivity.kt
│       └── FavoritesViewModel.kt
└── MyApplication.kt        # Application 类

```

## Kotlin Flow 使用示例

### 1. Repository 层使用 Flow

```kotlin
class AuthRepository {
    fun login(username: String, password: String): Flow<Result<User>> = flow {
        try {
            val response = apiService.login(username, password)
            if (response.errorCode == 0 && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.errorMsg)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}
```

### 2. ViewModel 层使用 StateFlow

```kotlin
class LoginViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()
    
    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            repository.login(username, password).collect { result ->
                result.onSuccess { user ->
                    _loginState.value = LoginState.Success(user)
                }.onFailure { exception ->
                    _loginState.value = LoginState.Error(exception.message ?: "登录失败")
                }
            }
        }
    }
}
```

### 3. Activity 层观察 StateFlow

```kotlin
class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                when (state) {
                    is LoginState.Loading -> showLoading()
                    is LoginState.Success -> navigateToMain()
                    is LoginState.Error -> showError(state.message)
                    is LoginState.Idle -> hideLoading()
                }
            }
        }
    }
}
```

## API 接口

本项目使用 [玩Android 开放 API](https://www.wanandroid.com/blog/show/2)：

| 功能 | 方法 | 端点 |
|------|------|------|
| 登录 | POST | `/user/login` |
| 注册 | POST | `/user/register` |
| 首页文章列表 | GET | `/article/list/{page}/json` |
| 收藏文章 | POST | `/lg/collect/{id}/json` |
| 取消收藏 | POST | `/lg/uncollect_originId/{id}/json` |
| 收藏列表 | GET | `/lg/collect/list/{page}/json` |

## 如何运行

### 环境要求
- Android Studio Arctic Fox 或更高版本
- JDK 8 或更高版本
- Android SDK API 24 (Android 7.0) 或更高版本

### 步骤
1. 克隆仓库
```bash
git clone https://github.com/wawo00/studyKtFlow.git
```

2. 在 Android Studio 中打开项目

3. 等待 Gradle 同步完成

4. 运行应用（选择模拟器或真机）

## 使用流程

1. **启动应用** - 进入登录页面
2. **注册账号** - 点击"没有账号？立即注册"，输入用户名和密码
3. **登录** - 使用注册的账号登录
4. **浏览文章** - 在首页查看文章列表，下拉可刷新
5. **查看详情** - 点击文章项进入详情页
6. **收藏文章** - 在详情页点击右下角的收藏按钮
7. **查看收藏** - 返回首页，点击右下角的 FAB 进入收藏列表

## 学习要点

### Kotlin Flow 核心概念

1. **Flow 构建器** - 使用 `flow { }` 创建冷流
2. **操作符** - `flowOn()` 切换线程
3. **StateFlow** - 状态管理的热流
4. **collect** - 收集 Flow 发射的值
5. **viewModelScope** - 在 ViewModel 中使用协程

### MVVM 架构优势

- **分离关注点** - UI、业务逻辑、数据层分离
- **可测试性** - 各层可独立测试
- **生命周期感知** - ViewModel 自动管理生命周期
- **状态管理** - 使用 StateFlow 统一状态管理

## 待改进功能

- [ ] 添加分页加载
- [ ] 添加搜索功能
- [ ] 添加错误重试机制
- [ ] 添加本地数据库缓存（Room）
- [ ] 添加夜间模式
- [ ] 添加单元测试和 UI 测试

## 许可证

本项目仅用于学习目的。

## 参考资料

- [Kotlin Flow 官方文档](https://kotlinlang.org/docs/flow.html)
- [Android Jetpack ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [玩Android API](https://www.wanandroid.com/blog/show/2)
