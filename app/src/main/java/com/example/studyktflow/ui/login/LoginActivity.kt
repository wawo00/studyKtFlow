package com.example.studyktflow.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.studyktflow.R
import com.example.studyktflow.ui.main.MainActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    
    private val viewModel: LoginViewModel by viewModels()
    
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvSwitch: TextView
    private lateinit var progressBar: ProgressBar
    
    private var isLoginMode = true
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        initViews()
        observeViewModel()
        setupListeners()
    }
    
    private fun initViews() {
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvSwitch = findViewById(R.id.tvSwitch)
        progressBar = findViewById(R.id.progressBar)
    }
    
    private fun setupListeners() {
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            
            if (username.isEmpty()) {
                Toast.makeText(this, R.string.please_input_username, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (password.isEmpty()) {
                Toast.makeText(this, R.string.please_input_password, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (isLoginMode) {
                viewModel.login(username, password)
            } else {
                val confirmPassword = etConfirmPassword.text.toString()
                if (password != confirmPassword) {
                    Toast.makeText(this, R.string.password_not_match, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                viewModel.register(username, password, confirmPassword)
            }
        }
        
        tvSwitch.setOnClickListener {
            isLoginMode = !isLoginMode
            updateUI()
            viewModel.resetState()
        }
    }
    
    private fun updateUI() {
        if (isLoginMode) {
            btnLogin.text = getString(R.string.login)
            tvSwitch.text = getString(R.string.no_account)
            etConfirmPassword.visibility = View.GONE
        } else {
            btnLogin.text = getString(R.string.register)
            tvSwitch.text = getString(R.string.have_account)
            etConfirmPassword.visibility = View.VISIBLE
        }
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                when (state) {
                    is LoginState.Idle -> {
                        progressBar.visibility = View.GONE
                    }
                    is LoginState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                    }
                    is LoginState.Success -> {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@LoginActivity, R.string.login_success, Toast.LENGTH_SHORT).show()
                        navigateToMain()
                    }
                    is LoginState.Error -> {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        
        lifecycleScope.launch {
            viewModel.registerState.collect { state ->
                when (state) {
                    is LoginState.Idle -> {
                        progressBar.visibility = View.GONE
                    }
                    is LoginState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                    }
                    is LoginState.Success -> {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@LoginActivity, R.string.register_success, Toast.LENGTH_SHORT).show()
                        navigateToMain()
                    }
                    is LoginState.Error -> {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
