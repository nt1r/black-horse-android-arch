package com.example.architecture.viewmodel

import com.example.architecture.model.UserEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe

// 应该放到view-model层
interface UserRepository {
    fun findByName(name: String): Maybe<UserEntity>

    fun save(userEntity: UserEntity): Completable

    fun clearAll()
}