package com.example.architecture.model

import android.content.Context
import androidx.room.Room
import androidx.test.rule.ActivityTestRule
import com.example.architecture.model.util.Encryptor
import com.example.architecture.view.LoginActivity
import com.example.architecture.viewmodel.UserRepository
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UserDBDataSourceTest {
    private lateinit var userDatabase: UserDatabase
    private lateinit var userRepository: UserRepository

    @get:Rule
    var activityTestRule: ActivityTestRule<LoginActivity> = ActivityTestRule(LoginActivity::class.java)

    val userEntity = UserEntity(
        "1",
        "android",
        Encryptor.encryptTextWithSalt("123456", "abcdefgh"),
        "abcdefgh"
    )

    @Before
    fun setUp() {
        userDatabase = Room.inMemoryDatabaseBuilder(getApplicationContext(), UserDatabase::class.java).build()
        userRepository = UserDBDataSource(getApplicationContext()).apply {
            this.setDBInstance(userDatabase)
        }
        userDatabase.clearAllTables()
    }

    @After
    fun tearDown() {
    }

    @Test
    fun shouldSaveUserSuccess() {
        val completable = userRepository.save(userEntity)
        completable.test()
            .assertComplete()
    }

    @Test
    fun shouldSaveUserFailedWhenExist() {
        var completable = userRepository.save(userEntity)
        completable.test()
            .assertComplete()
        completable = userRepository.save(userEntity)
        completable.test()
            .assertError { throwable ->
                throwable.message == "Username ${userEntity.name} already exists!"
            }
    }

    @Test
    fun shouldFindByNameSuccess() {
        userRepository.save(userEntity)
        val localUserEntityMaybe = userRepository.findByName(userEntity.name)

        localUserEntityMaybe.test()
            .assertValue { actualUserEntity ->
                actualUserEntity.id == "1" &&
                        actualUserEntity.name == "android" &&
                        actualUserEntity.salt == "abcdefgh"
            }
    }

    @Test
    fun shouldFindByNameFailedWhenUserNotExist() {
        userRepository.save(userEntity)
        val localUserEntityMaybe = userRepository.findByName("dwight")

        localUserEntityMaybe.test()
            .assertComplete()
    }

    @Test
    fun shouldClearAllUsers() {
        userRepository.save(userEntity)
        var localUserEntityMaybe = userRepository.findByName("android")

        localUserEntityMaybe.test()
            .assertValue { actualUserEntity ->
                actualUserEntity.id == "1" &&
                        actualUserEntity.name == "android" &&
                        actualUserEntity.salt == "abcdefgh"
            }

        userRepository.clearAll()
        localUserEntityMaybe = userRepository.findByName("android")
        localUserEntityMaybe.test()
            .assertComplete()
    }

    private fun getApplicationContext(): Context {
        return activityTestRule.activity.applicationContext
    }
}