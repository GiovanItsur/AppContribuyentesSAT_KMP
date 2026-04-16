package com.example.appcontribuyentessat_kmp_sqldelight.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(SatDatabase.Schema, context, "sat_proyect.db")
    }

}