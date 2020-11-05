package com.example.architecture.view

import android.content.Context
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.architecture.R
import org.hamcrest.Matchers.not
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {
    @get:Rule
    var activityTestRule: ActivityTestRule<LoginActivity> = ActivityTestRule(LoginActivity::class.java)

    /*@get:Rule
    var activityScenario: ActivityScenario<LoginActivity> = ActivityScenario.launch(LoginActivity::class.java)

    @get:Rule
    var activityScenarioRule: ActivityScenarioRule<LoginActivity> = ActivityScenarioRule(LoginActivity::class.java)*/

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun usernameEditText_lengthShouldNotBeEmpty_sameActivity() {
        getUsernameEditText()
            .perform(typeText(""), closeSoftKeyboard())
        getLoginButton()
            .perform(click())

        Thread.sleep(1000L)
        getUsernameEditText()
            .check(matches(hasErrorText("用户名长度应为1～8")))
    }

    @Test
    fun usernameEditText_lengthShouldNotLargerThan8_sameActivity() {
        getUsernameEditText()
            .perform(typeText("123456789101112"), closeSoftKeyboard())
        getLoginButton()
            .perform(click())

        Thread.sleep(1000L)
        getUsernameEditText()
            .check(matches(withText("12345678")))
    }

    @Test
    fun passwordEditText_lengthShouldNotBeEmpty_sameActivity() {
        getPasswordEditText()
            .perform(typeText(""), closeSoftKeyboard())
        getLoginButton()
            .perform(click())

        Thread.sleep(1000L)
        getPasswordEditText()
            .check(matches(hasErrorText("密码不能为空")))
    }

    @Test
    fun shouldToastUserNotExist_sameActivity() {
        val decorView = activityTestRule.activity.window.decorView
        getUsernameEditText()
            .perform(typeText("leqi"), closeSoftKeyboard())
        getPasswordEditText()
            .perform(typeText("123"), closeSoftKeyboard())
        getLoginButton()
            .perform(click())

        Thread.sleep(1000L)
        checkToastAppears("登录失败，用户不存在", decorView)
    }

    @Test
    fun shouldToastPasswordWrong_sameActivity() {
        val decorView = activityTestRule.activity.window.decorView
        getUsernameEditText()
            .perform(typeText("android"), closeSoftKeyboard())
        getPasswordEditText()
            .perform(typeText("123"), closeSoftKeyboard())
        getLoginButton()
            .perform(click())

        Thread.sleep(1000L)
        checkToastAppears("登录失败，密码错误", decorView)
    }

    @Test
    fun shouldLoginSuccess_sameActivity() {
        val decorView = activityTestRule.activity.window.decorView
        getUsernameEditText()
            .perform(typeText("android"), closeSoftKeyboard())
        getPasswordEditText()
            .perform(typeText("123456"), closeSoftKeyboard())
        getLoginButton()
            .perform(click())

        Thread.sleep(1000L)
        // run blocking not working here
        checkToastAppears("登录成功", decorView)
    }

    private fun checkToastAppears(message: String, decorView: View) {
        onView(withText(message))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()))
    }

    private fun getUsernameEditText(): ViewInteraction {
        return onView(withId(R.id.arch_username_edit_text))
    }

    private fun getPasswordEditText(): ViewInteraction {
        return onView(withId(R.id.arch_password_edit_text))
    }

    private fun getLoginButton(): ViewInteraction {
        return onView(withId(R.id.arch_login_button))
    }

    private fun getRegisterButton(): ViewInteraction {
        return onView(withId(R.id.arch_fill_button))
    }
}