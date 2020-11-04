package com.example.architecture.viewmodel

import com.example.architecture.model.UserEntity
import com.example.architecture.model.util.Encryptor
import java.util.*

class Convertor {
    companion object {
        fun convertUser2UserEntity(user: User): UserEntity {
            val salt: String = Encryptor.generateSalt()
            val encryptedPassword = Encryptor.encryptTextWithSalt(user.password, salt)
            return UserEntity(
                Date().time.toString(),
                user.name,
                encryptedPassword,
                salt
            )
        }
    }
}