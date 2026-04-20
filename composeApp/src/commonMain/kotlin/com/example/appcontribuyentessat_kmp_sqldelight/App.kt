package com.example.appcontribuyentessat_kmp_sqldelight

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.example.appcontribuyentessat_kmp_sqldelight.database.DatabaseDriverFactory
import com.example.appcontribuyentessat_kmp_sqldelight.database.createDatabase

// ========================================================================
// 1. DEFINICIÓN DE RUTAS (MAPA DE NAVEGACIÓN)
// ========================================================================
// Usamos una 'sealed class' (clase sellada) para tener un control estricto
// de las pantallas que existen. Si en el futuro agregas más, las pones aquí.
sealed class Pantalla {
    object Lista : Pantalla()
    object Formulario : Pantalla()
}

@Composable
fun App(driverFactory: DatabaseDriverFactory) {
    MaterialTheme {
        // ========================================================================
        // 2. INYECCIÓN DE DEPENDENCIAS (Instancias Únicas)
        // ========================================================================
        // Usamos 'remember' para que la base de datos y el ViewModel se creen
        // UNA SOLA VEZ al abrir la app y no se estén reiniciando cada vez que la pantalla cambia.
        val database = remember { createDatabase(driverFactory) }
        val viewModel = remember { ContribuyenteViewModel(database) }

        // ========================================================================
        // 3. ESTADO GLOBAL DE NAVEGACIÓN
        // ========================================================================
        // Por defecto, la aplicación arranca mostrando la lista de personas.
        var pantallaActual by remember { mutableStateOf<Pantalla>(Pantalla.Lista) }

        // ========================================================================
        // 4. MOTOR DE RENDERIZADO (El "Router" Casero)
        // ========================================================================
        // Compose lee la variable 'pantallaActual'. Si el estado cambia, destruye la
        // pantalla anterior y dibuja la nueva al instante.
        when (pantallaActual) {
            is Pantalla.Lista -> {
                ListaScreen(
                    viewModel = viewModel,
                    onNavigateToForm = { pantallaActual = Pantalla.Formulario }
                )
            }
            is Pantalla.Formulario -> {
                FormularioScreen(
                    viewModel = viewModel,
                    onNavigateToList = { pantallaActual = Pantalla.Lista }
                )
            }
        }
    }
}