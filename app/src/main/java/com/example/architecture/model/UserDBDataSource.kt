package com.example.architecture.model

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.architecture.viewmodel.UserRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.kotlin.subscribeBy

class UserDBDataSource(private val context: Context) : UserRepository {
    private var databaseInstance: UserDatabase? = null

    private val TAG = "Architecture"

    private fun getDBInstance(): UserDatabase {
        if (databaseInstance == null) {
            databaseInstance =
                Room.databaseBuilder(context, UserDatabase::class.java, "architecture").build()
        }
        return databaseInstance!!
    }

    override fun findByName(name: String): Maybe<UserEntity> {
        var userEntity: UserEntity? = null
        try {
            userEntity = getDBInstance().userDao().findByName(name)
        } catch (exception: Exception) {
            return Maybe.error(exception)
        }

        return if (userEntity == null) {
            Maybe.empty()
        } else {
            Maybe.just(userEntity)
        }
    }

    override fun save(userEntity: UserEntity): Completable {
        if (isUsernameExist(userEntity.name)) {
            return Completable.error(RuntimeException("Username ${userEntity.name} already exists!"))
        }
        val lineNumber = getDBInstance().userDao().save(userEntity)
        return if (lineNumber > -1) {
            Completable.complete()
        } else {
            Completable.error(RuntimeException("Database insert error"))
        }
    }

    override fun clearAll() {
        getDBInstance().clearAllTables()
    }

    private fun isUsernameExist(name: String): Boolean {
        var isExist = false
        findByName(name)
            .subscribeBy(
                onSuccess = {
                    Log.d(TAG, "findByName onSuccess")
                    isExist = true
                },
                onComplete = {
                    Log.d(TAG, "findByName onComplete")
                    isExist = false
                },
                onError = {
                    it.printStackTrace()
                    isExist = false
                }
            )
        return isExist
    }
}