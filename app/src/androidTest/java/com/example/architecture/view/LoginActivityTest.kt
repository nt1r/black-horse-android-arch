package com.example.architecture.view

import android.content.Context
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.example.architecture.R
import org.hamcrest.Matchers.not
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityTest {
    @get:Rule
    var activityRule: ActivityTestRule<LoginActivity> = ActivityTestRule(LoginActivity::class.java)

    companion object {
        @BeforeClass
        fun insertData() {
            getRegisterButton()
                .perform(click())
        }

        private fun getRegisterButton(): ViewInteraction {
            return onView(withId(R.id.arch_fill_button))
        }
    }

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
        getUsernameEditText()
            .check(matches(hasErrorText(getContext().getString(R.string.username_length_error))))
    }

    @Test
    fun usernameEditText_lengthShouldNotLargerThan8_sameActivity() {
        getUsernameEditText()
            .perform(typeText("123456789101112"), closeSoftKeyboard())
        getLoginButton()
            .perform(click())
        getUsernameEditText()
            .check(matches(withText("12345678")))
    }

    @Test
    fun passwordEditText_lengthShouldNotBeEmpty_sameActivity() {
        getPasswordEditText()
            .perform(typeText(""), closeSoftKeyboard())
        getLoginButton()
            .perform(click())
        getPasswordEditText()
            .check(matches(hasErrorText(getContext().getString(R.string.password_empty_error))))
    }

    @Test
    fun shouldToastUserNotExist_sameActivity() {
        getUsernameEditText()
            .perform(typeText("leqi"), closeSoftKeyboard())
        getPasswordEditText()
            .perform(typeText("123"), closeSoftKeyboard())
        getLoginButton()
            .perform(click())
        checkToastAppears("登录失败，用户不存在")
    }

    @Test
    fun shouldToastPasswordWrong_sameActivity() {
        getUsernameEditText()
            .perform(typeText("android"), closeSoftKeyboard())
        getPasswordEditText()
            .perform(typeText("123"), closeSoftKeyboard())
        getLoginButton()
            .perform(click())
        checkToastAppears("登录失败，密码错误")
    }

    @Test
    fun shouldLoginSuccess_sameActivity() {
        getUsernameEditText()
            .perform(typeText("android"), closeSoftKeyboard())
        getPasswordEditText()
            .perform(typeText("123456"), closeSoftKeyboard())
        getLoginButton()
            .perform(click())
        checkToastAppears("登录成功")
    }

    private fun checkToastAppears(message: String) {
        onView(withText(message)).inRoot(withDecorView(not(activityRule.activity.window.decorView))).check(matches(isDisplayed()))
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

    private fun getContext(): Context {
        return activityRule.activity.applicationContext
    }
}