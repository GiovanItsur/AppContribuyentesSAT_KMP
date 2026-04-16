package com.example.appcontribuyentessat_kmp_sqldelight.database

import app.cash.sqldelight.db.SqlDriver
// Importamos la BD que generó SQLDelight
import com.example.appcontribuyentessat_kmp_sqldelight.database.SatDatabase

expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DatabaseDriverFactory): SatDatabase {
    val driver = driverFactory.createDriver()
    return SatDatabase(driver)

}
