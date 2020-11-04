package com.example.architecture.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.architecture.R
import com.example.architecture.viewmodel.LoginExceptionHandler
import com.example.architecture.viewmodel.LoginViewModel
import com.example.architecture.viewmodel.User
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {
    private lateinit var loginButton: AppCompatButton
    private lateinit var fillButton: AppCompatButton
    private lateinit var clearButton: AppCompatButton
    private lateinit var usernameEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText

    private val TAG = "Architecture"
    private val USERNAME_MAX_LENGTH = 8

    private val loginViewModel: LoginViewModel by viewModels()

    private val exceptionHandler: Thread.UncaughtExceptionHandler = LoginExceptionHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler)

        initComponents()
        initLoginViewModel()
    }

    private fun initLoginViewModel() {
        loginViewModel.init(applicationContext)
        loginViewModel.getLoginResult().observe(this, { loginResult ->
            if (loginResult.isLoginSuccess) {
                Toast.makeText(this, R.string.login_success, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(
                    this,
                    "${getString(R.string.login_failed)}${getString(R.string.comma)}${loginResult.errorMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
        loginViewModel.getRegisterResult().observe(this, { registerResult ->
            if (registerResult.isRegisterSuccess) {
                Toast.makeText(this@LoginActivity, R.string.save_user_success, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(
                    this@LoginActivity,
                    "${getString(R.string.save_user_failed)}${getString(R.string.comma)}${registerResult.errorMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun initComponents() {
        loginButton = findViewById(R.id.arch_login_button)
        fillButton = findViewById(R.id.arch_fill_button)
        clearButton = findViewById(R.id.arch_clear_button)
        usernameEditText = findViewById(R.id.arch_username_edit_text)
        passwordEditText = findViewById(R.id.arch_password_edit_text)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (isEditTextValid(username, password)) {
                // view model scope
                val user = User(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
                )
                loginViewModel.login(user)
            }
        }

        fillButton.setOnClickListener {
            val user = User(
                "android",
                "123456"
            )
            loginViewModel.fillData(user)
        }

        clearButton.setOnClickListener {
            loginViewModel.clearAll()
        }
    }

    private fun isEditTextValid(username: String, password: String): Boolean {
        var isValid = true
        if (username.length > USERNAME_MAX_LENGTH || username.isEmpty()) {
            usernameEditText.error = getString(R.string.username_length_error)
            isValid = false
        }

        if (password.isEmpty()) {
            passwordEditText.error = getString(R.string.password_empty_error)
            isValid = false
        }

        return isValid
    }
}