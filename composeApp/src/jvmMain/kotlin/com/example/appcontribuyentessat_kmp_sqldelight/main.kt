package com.example.appcontribuyentessat_kmp_sqldelight

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "appcontribuyentessat_kmp_sqldelight",
    ) {
        App()
    }
}