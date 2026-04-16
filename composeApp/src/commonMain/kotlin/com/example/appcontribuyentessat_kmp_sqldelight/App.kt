package com.example.appcontribuyentessat_kmp_sqldelight

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appcontribuyentessat_kmp_sqldelight.database.DatabaseDriverFactory
import com.example.appcontribuyentessat_kmp_sqldelight.database.createDatabase

@Composable
fun App(driverFactory: DatabaseDriverFactory) {
    MaterialTheme {
        val database = remember { createDatabase(driverFactory) }

        val viewModel = remember { ContribuyenteViewModel(database) }

        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "formulario") {
            composable("formulario") {
                FormularioScreen(viewModel)

            }
        }
    }
}