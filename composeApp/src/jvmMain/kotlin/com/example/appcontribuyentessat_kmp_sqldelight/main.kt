package com.example.appcontribuyentessat_kmp_sqldelight

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.appcontribuyentessat_kmp_sqldelight.database.DatabaseDriverFactory

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "SAT Desktop") {
        App(driverFactory = DatabaseDriverFactory())
    }

}