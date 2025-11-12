package com.example.studyktflow.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.studyktflow.R
import com.example.studyktflow.databinding.ActivityLoginBinding
import com.example.studyktflow.ui.main.MainActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding

    private var isLoginMode = true
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeViewModel()
        setupListeners()
        updateUI()
    }
    
    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()

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
                val confirmPassword = binding.etConfirmPassword.text.toString()
                if (password != confirmPassword) {
                    Toast.makeText(this, R.string.password_not_match, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                viewModel.register(username, password, confirmPassword)
            }
        }
        
        binding.tvSwitch.setOnClickListener {
            isLoginMode = !isLoginMode
            updateUI()
            viewModel.resetState()
        }
    }
    
    private fun updateUI() {
        if (isLoginMode) {
            binding.btnLogin.text = getString(R.string.login)
            binding.tvSwitch.text = getString(R.string.no_account)
            binding.etConfirmPassword.visibility = View.GONE
        } else {
            binding.btnLogin.text = getString(R.string.register)
            binding.tvSwitch.text = getString(R.string.have_account)
            binding.etConfirmPassword.visibility = View.VISIBLE
        }
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                when (state) {
                    is LoginState.Idle -> {
                        binding.progressBar.visibility = View.GONE
                    }
                    is LoginState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is LoginState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@LoginActivity, R.string.login_success, Toast.LENGTH_SHORT).show()
                        navigateToMain()
                    }
                    is LoginState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        
        lifecycleScope.launch {
            viewModel.registerState.collect { state ->
                when (state) {
                    is LoginState.Idle -> {
                        binding.progressBar.visibility = View.GONE
                    }
                    is LoginState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is LoginState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@LoginActivity, R.string.register_success, Toast.LENGTH_SHORT).show()
                        navigateToMain()
                    }
                    is LoginState.Error -> {
                        binding.progressBar.visibility = View.GONE
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
