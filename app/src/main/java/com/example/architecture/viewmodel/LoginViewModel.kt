package com.example.architecture.viewmodel

import android.content.Context
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
    private val _loginResult: MutableLiveData<LoginResult> = MutableLiveData()
    val loginResult: LiveData<LoginResult>
        get() = _loginResult

    private val _registerResult: MutableLiveData<RegisterResult> = MutableLiveData()
    val registerResult: LiveData<RegisterResult>
        get() = _registerResult

    private lateinit var context: Context
    private lateinit var userDBDataSource: UserRepository

    private val TAG = "Architecture"

    fun init(context: Context) {
        this.context = context
        userDBDataSource = UserDBDataSource(this.context)
    }

    fun init(context: Context, dataSource: UserDBDataSource) {
        this.context = context
        userDBDataSource = dataSource
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
                // Log.d(TAG, "login failed, user not found")
                /*viewModelScope.launch(Dispatchers.Main) {
                    loginResult.value = LoginResult(false, context.getString(R.string.user_not_exist))
                }*/
                // loginResult.value = LoginResult(false, context.getString(R.string.user_not_exist))
                _loginResult.postValue(LoginResult(false, context.getString(R.string.user_not_exist)))
            } else {
                if (isPasswordMatch(userFound!!, user.password)) {
                    // Log.d(TAG, "login success")
                    // _loginResult.value = LoginResult(true, "")
                    _loginResult.postValue(LoginResult(true, ""))
                } else {
                    // Log.d(TAG, "login success, password wrong")
                    _loginResult.postValue(LoginResult(false, context.getString(R.string.password_wrong)))
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
                        _registerResult.postValue(RegisterResult(true, ""))
                    },
                    onError = {
                        _registerResult.postValue(RegisterResult(false, context.getString(R.string.user_exist)))
                    }
                )
        }
    }

    fun clearAll() {
        viewModelScope.launch(Dispatchers.IO) {
            userDBDataSource.clearAll()
        }
    }

    fun getDataSource(): UserRepository {
        return userDBDataSource
    }

    fun setDataSource(userRepository: UserRepository) {
        userDBDataSource = userRepository
    }
}