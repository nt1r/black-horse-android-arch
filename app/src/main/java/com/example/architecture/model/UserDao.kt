package com.example.architecture.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Query("select * from user_entity where name = :name")
    fun findByName(name: String): UserEntity?

    @Insert
    fun save(userEntity: UserEntity): Long
}