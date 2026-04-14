package com.example.appcontribuyentessat_kmp_sqldelight

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}