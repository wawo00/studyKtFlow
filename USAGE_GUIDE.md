# 使用指南和示例代码

## 快速开始示例

### 1. 登录示例

```kotlin
// 在 LoginActivity 中
class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 观察登录状态
        lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                when (state) {
                    is LoginState.Loading -> {
                        // 显示加载中
                        progressBar.visibility = View.VISIBLE
                    }
                    is LoginState.Success -> {
                        // 登录成功，跳转到主页
                        Toast.makeText(this@LoginActivity, "登录成功", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                    is LoginState.Error -> {
                        // 显示错误信息
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                    is LoginState.Idle -> {
                        progressBar.visibility = View.GONE
                    }
                }
            }
        }
        
        // 点击登录按钮
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            viewModel.login(username, password)
        }
    }
}
```

### 2. 获取文章列表示例

```kotlin
// 在 MainActivity 中
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: ArticleAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 设置 RecyclerView
        adapter = ArticleAdapter { article ->
            // 点击文章跳转到详情页
            val intent = Intent(this, ArticleDetailActivity::class.java)
            intent.putExtra("article_id", article.id)
            intent.putExtra("article_title", article.title)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
        
        // 观察文章列表状态
        lifecycleScope.launch {
            viewModel.articleListState.collect { state ->
                when (state) {
                    is ArticleListState.Loading -> {
                        swipeRefresh.isRefreshing = true
                    }
                    is ArticleListState.Success -> {
                        swipeRefresh.isRefreshing = false
                        adapter.submitList(state.articles)
                    }
                    is ArticleListState.Error -> {
                        swipeRefresh.isRefreshing = false
                        Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                    is ArticleListState.Idle -> {
                        swipeRefresh.isRefreshing = false
                    }
                }
            }
        }
        
        // 加载文章
        viewModel.loadArticles()
        
        // 下拉刷新
        swipeRefresh.setOnRefreshListener {
            viewModel.loadArticles(refresh = true)
        }
    }
}
```

### 3. 收藏/取消收藏示例

```kotlin
// 在 ArticleDetailActivity 中
class ArticleDetailActivity : AppCompatActivity() {
    private val viewModel: ArticleDetailViewModel by viewModels()
    private var isCollected = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 观察收藏状态
        lifecycleScope.launch {
            viewModel.collectState.collect { state ->
                when (state) {
                    is CollectState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                    }
                    is CollectState.Success -> {
                        progressBar.visibility = View.GONE
                        isCollected = state.collected
                        // 更新收藏按钮状态
                        fabCollect.isSelected = isCollected
                        val message = if (isCollected) "收藏成功" else "取消收藏成功"
                        Toast.makeText(this@ArticleDetailActivity, message, Toast.LENGTH_SHORT).show()
                    }
                    is CollectState.Error -> {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@ArticleDetailActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                    is CollectState.Idle -> {
                        progressBar.visibility = View.GONE
                    }
                }
            }
        }
        
        // 点击收藏按钮
        fabCollect.setOnClickListener {
            val articleId = intent.getIntExtra("article_id", 0)
            viewModel.toggleCollect(articleId, isCollected)
        }
    }
}
```

## Kotlin Flow 核心概念详解

### 1. Flow 构建器

```kotlin
// 创建一个 Flow
fun getArticles(): Flow<List<Article>> = flow {
    // 发射数据
    emit(articles)
}

// 在 Repository 中使用
class ArticleRepository {
    fun getArticleList(page: Int): Flow<Result<List<Article>>> = flow {
        try {
            val response = apiService.getArticleList(page)
            if (response.errorCode == 0 && response.data != null) {
                emit(Result.success(response.data.datas))
            } else {
                emit(Result.failure(Exception(response.errorMsg)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)  // 在 IO 线程执行
}
```

### 2. StateFlow 状态管理

```kotlin
class MyViewModel : ViewModel() {
    // 私有的可变状态
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    
    // 公开的不可变状态
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val data = repository.getData()
                _uiState.value = UiState.Success(data)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message)
            }
        }
    }
}
```

### 3. Flow 操作符

```kotlin
// map - 转换数据
flow.map { article -> article.title }

// filter - 过滤数据
flow.filter { article -> article.collect }

// catch - 捕获异常
flow.catch { e -> 
    emit(Result.failure(e))
}

// onEach - 对每个元素执行操作
flow.onEach { article ->
    println(article.title)
}

// flowOn - 切换线程
flow.flowOn(Dispatchers.IO)
```

### 4. 收集 Flow

```kotlin
// 在协程中收集
lifecycleScope.launch {
    flow.collect { value ->
        // 处理数据
    }
}

// 使用 collectLatest (取消之前的收集)
lifecycleScope.launch {
    flow.collectLatest { value ->
        // 只处理最新的数据
    }
}
```

## 常见场景和最佳实践

### 1. 错误处理

```kotlin
// 在 Repository 中
fun login(username: String, password: String): Flow<Result<User>> = flow {
    try {
        val response = apiService.login(username, password)
        if (response.errorCode == 0) {
            emit(Result.success(response.data!!))
        } else {
            emit(Result.failure(Exception(response.errorMsg)))
        }
    } catch (e: Exception) {
        // 网络错误、解析错误等
        emit(Result.failure(e))
    }
}.flowOn(Dispatchers.IO)

// 在 ViewModel 中
fun login(username: String, password: String) {
    viewModelScope.launch {
        repository.login(username, password).collect { result ->
            result.onSuccess { user ->
                _state.value = LoginState.Success(user)
            }.onFailure { exception ->
                _state.value = LoginState.Error(exception.message ?: "未知错误")
            }
        }
    }
}
```

### 2. 加载状态管理

```kotlin
sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

// 使用泛型状态
class MyViewModel : ViewModel() {
    private val _state = MutableStateFlow<UiState<List<Article>>>(UiState.Idle)
    val state: StateFlow<UiState<List<Article>>> = _state.asStateFlow()
    
    fun loadData() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            repository.getArticles().collect { result ->
                _state.value = result.fold(
                    onSuccess = { UiState.Success(it) },
                    onFailure = { UiState.Error(it.message ?: "加载失败") }
                )
            }
        }
    }
}
```

### 3. 下拉刷新

```kotlin
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始加载
        viewModel.loadArticles()
        
        // 下拉刷新
        swipeRefresh.setOnRefreshListener {
            viewModel.loadArticles(refresh = true)
        }
        
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is ArticleListState.Loading -> {
                        // 如果不是下拉刷新，显示进度条
                        if (!swipeRefresh.isRefreshing) {
                            progressBar.visibility = View.VISIBLE
                        }
                    }
                    is ArticleListState.Success -> {
                        progressBar.visibility = View.GONE
                        swipeRefresh.isRefreshing = false
                        adapter.submitList(state.articles)
                    }
                    is ArticleListState.Error -> {
                        progressBar.visibility = View.GONE
                        swipeRefresh.isRefreshing = false
                    }
                    else -> {
                        progressBar.visibility = View.GONE
                        swipeRefresh.isRefreshing = false
                    }
                }
            }
        }
    }
}
```

### 4. 生命周期管理

```kotlin
// 使用 lifecycleScope 自动管理生命周期
class MyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 会在 Activity destroy 时自动取消
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                // 更新 UI
            }
        }
    }
}

// 使用 repeatOnLifecycle 在特定状态收集
class MyFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    // 只在 STARTED 状态收集
                }
            }
        }
    }
}
```

## 调试技巧

### 1. 使用日志

```kotlin
flow
    .onEach { println("Emitting: $it") }
    .catch { e -> println("Error: ${e.message}") }
    .collect { value ->
        println("Collected: $value")
    }
```

### 2. 使用 Retrofit 日志拦截器

```kotlin
// 在 RetrofitClient 中已添加
private val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}
```

### 3. 状态日志

```kotlin
lifecycleScope.launch {
    viewModel.state.collect { state ->
        Log.d("MainActivity", "State changed: $state")
        when (state) {
            // 处理状态
        }
    }
}
```

## 测试建议

### 1. Repository 测试

```kotlin
@Test
fun `login should emit success when api returns valid user`() = runTest {
    // Given
    val mockUser = User(/* ... */)
    coEvery { apiService.login(any(), any()) } returns ApiResponse(
        data = mockUser,
        errorCode = 0,
        errorMsg = ""
    )
    
    // When
    val result = repository.login("username", "password").first()
    
    // Then
    assertTrue(result.isSuccess)
    assertEquals(mockUser, result.getOrNull())
}
```

### 2. ViewModel 测试

```kotlin
@Test
fun `login should update state to success when repository returns success`() = runTest {
    // Given
    val mockUser = User(/* ... */)
    coEvery { repository.login(any(), any()) } returns flowOf(Result.success(mockUser))
    
    // When
    viewModel.login("username", "password")
    
    // Then
    val state = viewModel.loginState.value
    assertTrue(state is LoginState.Success)
    assertEquals(mockUser, (state as LoginState.Success).user)
}
```

## 性能优化建议

1. **使用 StateFlow 而不是 LiveData** - StateFlow 更轻量，且与 Kotlin 协程集成更好
2. **合理使用 flowOn** - 将耗时操作切换到 IO 线程
3. **避免在 UI 线程收集大量数据** - 使用分页或虚拟化列表
4. **使用 SharedFlow 处理事件** - 对于一次性事件（如导航、Toast），使用 SharedFlow
5. **及时取消协程** - 使用 lifecycleScope 或 viewModelScope 自动管理

## 常见问题

### Q: Flow 和 LiveData 有什么区别？
A: Flow 是冷流，只有在被收集时才开始执行；LiveData 是热的，始终活跃。Flow 提供了更多的操作符和更好的协程集成。

### Q: 什么时候使用 StateFlow，什么时候使用 SharedFlow？
A: StateFlow 用于状态管理，总是有一个当前值；SharedFlow 用于事件，可以没有初始值。

### Q: 如何避免内存泄漏？
A: 使用 lifecycleScope 或 viewModelScope，它们会在组件销毁时自动取消协程。

### Q: 如何处理配置更改（如屏幕旋转）？
A: 使用 ViewModel，它在配置更改时保留数据。StateFlow 的状态也会保留。
