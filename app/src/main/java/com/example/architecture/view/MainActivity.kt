package com.example.architecture.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.architecture.R

class MainActivity : AppCompatActivity() {
    private lateinit var enterArchButton: Button
    private lateinit var enterLoginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initComponents()
    }

    private fun initComponents() {
        enterArchButton = findViewById(R.id.enter_arch_button)
        enterLoginButton = findViewById(R.id.enter_login_button)

        enterArchButton.setOnClickListener {
            startActivity(Intent(this, ArchActivity::class.java))
        }
        enterLoginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}