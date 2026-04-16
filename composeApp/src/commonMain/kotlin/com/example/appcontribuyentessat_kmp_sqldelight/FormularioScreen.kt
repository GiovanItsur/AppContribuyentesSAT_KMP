package com.example.appcontribuyentessat_kmp_sqldelight

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appcontribuyentessatkmpsqldelight.database.ESTADOS
import com.example.appcontribuyentessatkmpsqldelight.database.MUNICIPIOS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(viewModel: ContribuyenteViewModel) {
    val estados by viewModel.estados.collectAsState()
    val municipios by viewModel.municipios.collectAsState()

    var estadoExpanded by remember { mutableStateOf(false) }
    var estadoSeleccionado by remember { mutableStateOf<ESTADOS?>(null) }

    var municipioExpanded by remember { mutableStateOf(false) }
    var municipioSeleccionado by remember { mutableStateOf<MUNICIPIOS?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text("Registro de Contribuyente", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        // COMBOBOX DE ESTADOS
        ExposedDropdownMenuBox(
            expanded = estadoExpanded,
            onExpandedChange = { estadoExpanded = it }
        ) {
            OutlinedTextField(
                value = estadoSeleccionado?.Nombre_Estado ?: "Selecciona un Estado",
                onValueChange = {},
                readOnly = true,
                label = { Text("Estado") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = estadoExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = estadoExpanded,
                onDismissRequest = { estadoExpanded = false }
            ) {
                if (estados.isEmpty()) {
                    DropdownMenuItem(text = { Text("No hay estados (BD vacía)") }, onClick = {})
                }
                estados.forEach { estado ->
                    DropdownMenuItem(
                        text = { Text(estado.Nombre_Estado) },
                        onClick = {
                            estadoSeleccionado = estado
                            viewModel.seleccionarEstado(estado.Id_Estado)
                            municipioSeleccionado = null // Reseteamos el municipio
                            estadoExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // COMBOBOX DE MUNICIPIOS (Reactivo)
        ExposedDropdownMenuBox(
            expanded = municipioExpanded,
            onExpandedChange = { if (estadoSeleccionado != null) municipioExpanded = it }
        ) {
            OutlinedTextField(
                value = municipioSeleccionado?.Nombre_Municipio ?: "Selecciona un Municipio",
                onValueChange = {},
                readOnly = true,
                label = { Text("Municipio") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = municipioExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                enabled = estadoSeleccionado != null // Se deshabilita si no hay estado
            )
            ExposedDropdownMenu(
                expanded = municipioExpanded,
                onDismissRequest = { municipioExpanded = false }
            ) {
                if (municipios.isEmpty() && estadoSeleccionado != null) {
                    DropdownMenuItem(text = { Text("Sin municipios registrados") }, onClick = {})
                }
                municipios.forEach { municipio ->
                    DropdownMenuItem(
                        text = { Text(municipio.Nombre_Municipio) },
                        onClick = {
                            municipioSeleccionado = municipio
                            municipioExpanded = false
                        }
                    )
                }
            }
        }
    }
}