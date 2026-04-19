package com.example.appcontribuyentessat_kmp_sqldelight

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.appcontribuyentessat_kmp_sqldelight.database.DatabaseDriverFactory
import com.example.appcontribuyentessat_kmp_sqldelight.database.createDatabase
import androidx.compose.runtime.*

sealed class Pantalla {
    object Formulario : Pantalla()
}

@Composable
fun App(driverFactory: DatabaseDriverFactory) {
    MaterialTheme {
        // Inicializamos BD y ViewModel
        val database = remember { createDatabase(driverFactory) }
        val viewModel = remember { ContribuyenteViewModel(database) }

        // Nuestro "Controlador de Navegación" casero
        // Inicia por defecto en la pantalla Formulario
        var pantallaActual by remember { mutableStateOf<Pantalla>(Pantalla.Formulario) }

        // El motor que dibuja la pantalla correcta según el estado
        when (pantallaActual) {
            is Pantalla.Formulario -> {
                // Si tuviéramos que navegar a otra pantalla desde adentro,
                // le pasaríamos una función así: { pantallaActual = Pantalla.Listado }
                FormularioScreen(viewModel)
            }
            // is Pantalla.Listado -> ListadoScreen(viewModel)
        }
    }
}