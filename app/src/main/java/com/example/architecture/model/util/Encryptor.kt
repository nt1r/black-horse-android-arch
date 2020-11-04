package com.example.architecture.model.util

import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import kotlin.random.Random

class Encryptor {
    companion object {
        /*private val method = "MD5"*/
        private const val method = "SHA-512"
        private val messageDigest: MessageDigest = MessageDigest.getInstance(method)

        fun encrypt(plainText: String): String {
            val digestedByteArray: ByteArray = messageDigest.digest(plainText.toByteArray())

            // Convert byte array into signum representation
            val signumInt = BigInteger(1, digestedByteArray)

            // Convert into hex value
            var hashText = signumInt.toString(16)

            // Add preceding zeros to make it 32 bit
            while (hashText.length < 32) {
                hashText = "0$hashText"
            }

            return hashText
        }

        fun encryptTextWithSalt(plainText: String, salt: String): String {
            val firstHalfOfSalt = salt.substring(0, salt.length / 2)
            val secondHalfOfSalt = salt.substring(salt.length / 2)
            return encrypt("$firstHalfOfSalt$plainText$secondHalfOfSalt")
        }

        fun generateSalt(): String {
            val randomByteArray = Random(Date().time).nextBytes(32)
            return String(randomByteArray)
        }
    }
}