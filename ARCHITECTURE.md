# 项目架构说明

## MVVM 架构图

```
┌─────────────────────────────────────────────────────────────┐
│                         UI Layer (View)                      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │LoginActivity │  │MainActivity  │  │DetailActivity│      │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘      │
│         │                  │                  │              │
└─────────┼──────────────────┼──────────────────┼──────────────┘
          │                  │                  │
          ▼                  ▼                  ▼
┌─────────────────────────────────────────────────────────────┐
│                      ViewModel Layer                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │LoginViewModel│  │MainViewModel │  │DetailViewModel│     │
│  │              │  │              │  │              │      │
│  │ StateFlow ◄──┤  │ StateFlow ◄──┤  │ StateFlow ◄──┤      │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘      │
└─────────┼──────────────────┼──────────────────┼──────────────┘
          │                  │                  │
          ▼                  ▼                  ▼
┌─────────────────────────────────────────────────────────────┐
│                    Repository Layer (Model)                  │
│  ┌──────────────────────┐  ┌──────────────────────┐        │
│  │  AuthRepository      │  │  ArticleRepository   │        │
│  │                      │  │                      │        │
│  │  fun login()         │  │  fun getArticles()   │        │
│  │     : Flow<Result>   │  │     : Flow<Result>   │        │
│  └──────────┬───────────┘  └──────────┬───────────┘        │
└─────────────┼──────────────────────────┼─────────────────────┘
              │                          │
              ▼                          ▼
┌─────────────────────────────────────────────────────────────┐
│                      Network Layer                           │
│  ┌───────────────────────────────────────────────┐          │
│  │            RetrofitClient                      │          │
│  │  ┌─────────────────┐  ┌─────────────────┐   │          │
│  │  │   ApiService    │  │   CookieJar     │   │          │
│  │  │                 │  │  (Persistence)  │   │          │
│  │  └─────────────────┘  └─────────────────┘   │          │
│  └───────────────────────────────────────────────┘          │
└─────────────────────────────────────────────────────────────┘
                          │
                          ▼
                  WanAndroid API
```

## Kotlin Flow 数据流

```
Repository Layer:
┌─────────────────────────────────────┐
│ fun login(): Flow<Result<User>> =   │
│   flow {                            │
│     val response = apiService.login │
│     emit(Result.success(response))  │
│   }.flowOn(Dispatchers.IO)          │
└─────────────────┬───────────────────┘
                  │ Flow<Result<User>>
                  ▼
ViewModel Layer:
┌─────────────────────────────────────┐
│ repository.login()                  │
│   .collect { result ->              │
│     _state.value = when(result) {   │
│       success -> Success(user)      │
│       failure -> Error(msg)         │
│     }                               │
│   }                                 │
└─────────────────┬───────────────────┘
                  │ StateFlow<LoginState>
                  ▼
Activity Layer:
┌─────────────────────────────────────┐
│ lifecycleScope.launch {             │
│   viewModel.state.collect { state->│
│     when(state) {                   │
│       Loading -> showLoading()      │
│       Success -> navigate()         │
│       Error -> showError()          │
│     }                               │
│   }                                 │
│ }                                   │
└─────────────────────────────────────┘
```

## 页面流程图

```
                    ┌──────────────┐
                    │ 启动应用      │
                    └──────┬───────┘
                           ▼
                    ┌──────────────┐
                    │ LoginActivity│
                    │              │
        ┌───────────┤  登录/注册    │
        │           └──────┬───────┘
        │                  │ 成功
        │                  ▼
        │           ┌──────────────┐
        │           │MainActivity  │◄────┐
        │           │  (首页列表)   │     │
        │           └──┬───────┬───┘     │
        │              │       │         │
        │    点击FAB   │       │ 点击Item │
        │              ▼       ▼         │
        │      ┌──────────┐ ┌──────────┐│
        │      │Favorites │ │  Detail  ││
        │      │Activity  │ │ Activity ││
        │      │(收藏列表) │ │(文章详情) ││
        │      └──────────┘ └────┬─────┘│
        │              │         │ 收藏  │
        └──────────────┴─────────┴──────┘
                   点击Item返回
```

## 状态管理

### LoginState
```kotlin
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}
```

### ArticleListState
```kotlin
sealed class ArticleListState {
    object Idle : ArticleListState()
    object Loading : ArticleListState()
    data class Success(val articles: List<Article>) : ArticleListState()
    data class Error(val message: String) : ArticleListState()
}
```

### CollectState
```kotlin
sealed class CollectState {
    object Idle : CollectState()
    object Loading : CollectState()
    data class Success(val collected: Boolean) : CollectState()
    data class Error(val message: String) : CollectState()
}
```

## 主要特性说明

### 1. Cookie 持久化
- 使用 `SharedPreferences` 存储 Cookie
- 自动在每次请求中携带 Cookie
- 实现无缝的用户认证状态保持

### 2. Flow 冷流 vs 热流
- **冷流 (Cold Flow)**: Repository 层返回的 Flow，只有被收集时才开始执行
- **热流 (Hot Flow)**: ViewModel 中的 StateFlow，始终活跃并保持最新状态

### 3. 协程作用域
- `viewModelScope`: 自动绑定到 ViewModel 生命周期
- `lifecycleScope`: 自动绑定到 Activity/Fragment 生命周期

### 4. 线程切换
- `flowOn(Dispatchers.IO)`: 在 IO 线程执行网络请求
- UI 更新自动在主线程执行

## 学习建议

1. **先理解 MVVM**: 从 Activity → ViewModel → Repository 的单向数据流
2. **掌握 Flow 基础**: flow 构建器、collect、flowOn 等操作
3. **学习 StateFlow**: 状态管理的最佳实践
4. **实践协程**: 理解协程的作用域和生命周期
5. **网络层实现**: Retrofit + Flow 的集成方式
