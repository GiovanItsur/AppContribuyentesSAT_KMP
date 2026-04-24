package com.example.appcontribuyentessat_kmp_sqldelight

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.example.appcontribuyentessat_kmp_sqldelight.database.DatabaseDriverFactory
import com.example.appcontribuyentessat_kmp_sqldelight.database.createDatabase

// ========================================================================
// 1. DEFINICIÓN DE RUTAS (MAPA DE NAVEGACIÓN)
// ========================================================================
// Usamos una 'sealed class' para tener un control estricto y a prueba de errores
// de las pantallas que existen. Si en el futuro agregas más (ej. Configuración), van aquí.
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
        // Usamos 'remember' para que la base de datos y el ViewModel (el cerebro)
        // se creen UNA SOLA VEZ al arrancar. Así no se borran los datos al cambiar de pantalla.
        val database = remember { createDatabase(driverFactory) }
        val viewModel = remember { ContribuyenteViewModel(database) }

        // ========================================================================
        // 3. ESTADO GLOBAL DE NAVEGACIÓN
        // ========================================================================
        // Por defecto, la aplicación arranca mostrando la lista principal.
        var pantallaActual by remember { mutableStateOf<Pantalla>(Pantalla.Lista) }

        // ========================================================================
        // 4. MOTOR DE RENDERIZADO (El "Router" Declarativo)
        // ========================================================================
        // Compose vigila 'pantallaActual'. Cuando esta variable cambia, destruye
        // la pantalla vieja de la memoria y dibuja la nueva al instante.
        when (pantallaActual) {
            is Pantalla.Lista -> {
                ListaScreen(
                    viewModel = viewModel,
                    // Le pasamos el control remoto para que ListaScreen pueda saltar a Formulario
                    onNavigateToForm = { pantallaActual = Pantalla.Formulario }
                )
            }
            is Pantalla.Formulario -> {
                FormularioScreen(
                    viewModel = viewModel,
                    // Le pasamos el control remoto para que FormularioScreen nos regrese a la lista
                    onNavigateToList = { pantallaActual = Pantalla.Lista }
                )
            }
        }
    }
}