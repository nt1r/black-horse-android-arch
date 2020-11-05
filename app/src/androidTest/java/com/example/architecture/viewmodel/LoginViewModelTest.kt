package com.example.architecture.viewmodel

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import com.example.architecture.R
import com.example.architecture.model.UserDBDataSource
import com.example.architecture.model.UserEntity
import com.example.architecture.model.util.Encryptor
import com.example.architecture.repository.LoginResult
import com.example.architecture.view.LoginActivity
import io.mockk.MockKAnnotations
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import io.reactivex.rxjava3.core.Maybe
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {
    @MockK
    lateinit var mockedUserDBDataSource: UserDBDataSource

    @get:Rule
    var activityTestRule: ActivityTestRule<LoginActivity> = ActivityTestRule(LoginActivity::class.java)

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun shouldToastUserNotExist() {
        val decorView = activityTestRule.activity.window.decorView
        val user = User("leqi", "123")

        activityTestRule.activity.loginViewModel.setDataSource(mockedUserDBDataSource)
        every { mockedUserDBDataSource.findByName(user.name) } returns Maybe.empty()

        getUsernameEditText()
            .perform(ViewActions.typeText("leqi"), ViewActions.closeSoftKeyboard())
        getPasswordEditText()
            .perform(ViewActions.typeText("123"), ViewActions.closeSoftKeyboard())

        getLoginButton()
            .perform(ViewActions.click())

        Thread.sleep(1000L)
        verify {
            mockedUserDBDataSource.findByName(user.name)
        }
        confirmVerified(mockedUserDBDataSource)
        checkToastAppears("登录失败，用户不存在", decorView)
        assertEquals(false, activityTestRule.activity.loginViewModel.loginResult.value?.isLoginSuccess)
        assertEquals("用户不存在", activityTestRule.activity.loginViewModel.loginResult.value?.errorMessage)
    }

    @Test
    fun shouldToastPasswordWrong() {
        val decorView = activityTestRule.activity.window.decorView
        val user = User("android", "123")
        val salt = "abcdefg"
        val correctPassword = "123456"

        activityTestRule.activity.loginViewModel.setDataSource(mockedUserDBDataSource)
        every { mockedUserDBDataSource.findByName(user.name) } returns Maybe.just(UserEntity(
            "1",
            "android",
            Encryptor.encryptTextWithSalt(correctPassword, salt),
            salt
        ))

        getUsernameEditText()
            .perform(ViewActions.typeText("android"), ViewActions.closeSoftKeyboard())
        getPasswordEditText()
            .perform(ViewActions.typeText("123"), ViewActions.closeSoftKeyboard())

        getLoginButton()
            .perform(ViewActions.click())

        Thread.sleep(1000L)
        verify {
            mockedUserDBDataSource.findByName(user.name)
        }
        confirmVerified(mockedUserDBDataSource)
        checkToastAppears("登录失败，密码错误", decorView)
        assertEquals(false, activityTestRule.activity.loginViewModel.loginResult.value?.isLoginSuccess)
        assertEquals("密码错误", activityTestRule.activity.loginViewModel.loginResult.value?.errorMessage)
    }

    @Test
    fun shouldLoginSuccess() {
        val decorView = activityTestRule.activity.window.decorView
        val user = User("android", "123")
        val salt = "abcdefg"
        val correctPassword = "123456"

        activityTestRule.activity.loginViewModel.setDataSource(mockedUserDBDataSource)
        every { mockedUserDBDataSource.findByName(user.name) } returns Maybe.just(UserEntity(
            "1",
            "android",
            Encryptor.encryptTextWithSalt(correctPassword, salt),
            salt
        ))

        getUsernameEditText()
            .perform(ViewActions.typeText("android"), ViewActions.closeSoftKeyboard())
        getPasswordEditText()
            .perform(ViewActions.typeText("123456"), ViewActions.closeSoftKeyboard())

        getLoginButton()
            .perform(ViewActions.click())

        Thread.sleep(1000L)
        verify {
            mockedUserDBDataSource.findByName(user.name)
        }
        confirmVerified(mockedUserDBDataSource)
        checkToastAppears("登录成功", decorView)
        assertEquals(true, activityTestRule.activity.loginViewModel.loginResult.value?.isLoginSuccess)
    }

    private fun checkToastAppears(message: String, decorView: View) {
        Espresso.onView(ViewMatchers.withText(message))
            .inRoot(RootMatchers.withDecorView(Matchers.not(decorView)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    private fun getUsernameEditText(): ViewInteraction {
        return Espresso.onView(ViewMatchers.withId(R.id.arch_username_edit_text))
    }

    private fun getPasswordEditText(): ViewInteraction {
        return Espresso.onView(ViewMatchers.withId(R.id.arch_password_edit_text))
    }

    private fun getLoginButton(): ViewInteraction {
        return Espresso.onView(ViewMatchers.withId(R.id.arch_login_button))
    }

    /*private fun getMockedDataSource(): UserDBDataSource {

    }*/
}