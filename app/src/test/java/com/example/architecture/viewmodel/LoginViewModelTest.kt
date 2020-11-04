package com.example.architecture.viewmodel

import org.junit.After
import org.junit.Before
import org.junit.Test

class LoginViewModelTest {

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun shouldLogin() {
        val user = User("leqi", "123")
        val loginViewModel = LoginViewModel()
        loginViewModel.login(user)
    }

    @Test
    fun fillData() {
    }

    @Test
    fun clearAll() {
    }
}