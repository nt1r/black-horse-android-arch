package com.example.architecture.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.architecture.R
import com.example.architecture.model.UserDBDataSource
import com.example.architecture.model.UserEntity
import com.example.architecture.model.util.Encryptor
import com.example.architecture.repository.LoginResult
import com.example.architecture.repository.RegisterResult
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val loginResult: MutableLiveData<LoginResult> by lazy {
        MutableLiveData<LoginResult>()
    }

    private val registerResult: MutableLiveData<RegisterResult> by lazy {
        MutableLiveData<RegisterResult>()
    }
    private lateinit var context: Context
    private lateinit var userDBDataSource: UserRepository

    private val TAG = "Architecture"

    fun init(context: Context) {
        this.context = context
        userDBDataSource = UserDBDataSource(this.context)
    }

    fun login(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            var userFound: UserEntity? = null
            userDBDataSource.findByName(user.name)
                .subscribeBy(
                    onSuccess = { userEntity ->
                        userFound = userEntity
                    },
                    onComplete = {
                        // user not found
                    },
                    onError = {
                        it.printStackTrace()
                    }
                )
            if (userFound == null) {
                Log.d(TAG, "login failed, user not found")
                viewModelScope.launch(Dispatchers.Main) {
                    loginResult.value = LoginResult(false, context.getString(R.string.user_not_exist))
                }
            } else {
                if (isPasswordMatch(userFound!!, user.password)) {
                    Log.d(TAG, "login success")
                    viewModelScope.launch(Dispatchers.Main) {
                        loginResult.value = LoginResult(true, "")
                    }
                } else {
                    Log.d(TAG, "login success, password wrong")
                    viewModelScope.launch(Dispatchers.Main) {
                        loginResult.value = LoginResult(false, context.getString(R.string.password_wrong))
                    }
                }
            }
        }
    }

    private fun isPasswordMatch(userEntity: UserEntity, password: String): Boolean {
        return Encryptor.encryptTextWithSalt(password, userEntity.salt) == userEntity.password
    }

    fun fillData(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            userDBDataSource.save(Convertor.convertUser2UserEntity(user))
                .subscribeBy(
                    onComplete = {
                        viewModelScope.launch(Dispatchers.Main) {
                            registerResult.value = RegisterResult(true, "")
                        }
                    },
                    onError = {
                        viewModelScope.launch(Dispatchers.Main) {
                            registerResult.value = RegisterResult(false, context.getString(R.string.user_exist))
                        }
                    }
                )
        }
    }

    fun clearAll() {
        viewModelScope.launch(Dispatchers.IO) {
            userDBDataSource.clearAll()
        }
    }

    fun getLoginResult(): LiveData<LoginResult> {
        return loginResult
    }

    fun getRegisterResult(): LiveData<RegisterResult> {
        return registerResult
    }
}