package com.example.appcontribuyentessat_kmp_sqldelight

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform