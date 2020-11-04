package com.example.architecture.model.util

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class EncryptorTest {

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun shouldEncryptWithSHA512() {
        val actual = Encryptor.encrypt("Hello World")
        val expected =
            "2c74fd17edafd80e8447b0d46741ee243b7eb74dd2149a0ab1b9246fb30382f27e853d8585719e0e67cbda0daa8f51671064615d645ae27acb15bfb1447f459b"
        assertEquals(expected, actual)
    }

    @Test
    fun shouldEncryptWithSaltUsingSHA512() {
        val salt = "12345678"
        val plainText = "Hello World"
        val actual = Encryptor.encryptTextWithSalt(plainText, salt)
        val expected =
            "d530300febf7ca483fbe3c450c6d5098c243942b44ba7b481d7d329e337a9d3a4312444a60c20e3088254b7435d556fd27b078c8799bfe6c20e5a7175d8e60aa"
        assertEquals(expected, actual)
    }
}