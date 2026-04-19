package com.example.appcontribuyentessat_kmp_sqldelight

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appcontribuyentessatkmpsqldelight.database.ESTADOS
import com.example.appcontribuyentessatkmpsqldelight.database.MUNICIPIOS
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(viewModel: ContribuyenteViewModel) {
    // --- Recolección de estados del ViewModel (Reactividad) ---
    val nombre by viewModel.nombre.collectAsState()
    val paterno by viewModel.apellidoPaterno.collectAsState()
    val materno by viewModel.apellidoMaterno.collectAsState()
    val fecha by viewModel.fechaNacimiento.collectAsState()
    val rfc by viewModel.rfcGenerado.collectAsState()
    val curp by viewModel.curp.collectAsState()
    val correo by viewModel.correo.collectAsState()
    val telefono by viewModel.telefono.collectAsState()
    val cp by viewModel.codigoPostal.collectAsState()
    val vialidad by viewModel.tipoVialidad.collectAsState()
    val actividad by viewModel.actividadEconomica.collectAsState()
    val regimen by viewModel.regimenFiscal.collectAsState()

    val estados by viewModel.estados.collectAsState()
    val municipios by viewModel.municipios.collectAsState()

    // --- Estados locales para controlar los ComboBox ---
    var estadoExpanded by remember { mutableStateOf(false) }
    var estadoSeleccionado by remember { mutableStateOf<ESTADOS?>(null) }

    var municipioExpanded by remember { mutableStateOf(false) }
    var municipioSeleccionado by remember { mutableStateOf<MUNICIPIOS?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Registro de Contribuyente",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // --- SECCIÓN: DATOS PERSONALES ---
        OutlinedTextField(
            value = nombre,
            onValueChange = { viewModel.actualizarNombre(it) },
            label = { Text("Nombre(s)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = paterno,
            onValueChange = { viewModel.actualizarPaterno(it) },
            label = { Text("Apellido Paterno") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = materno, // Usamos la variable nombre para el materno si así lo deseas o cámbiala por la de materno
            onValueChange = { viewModel.actualizarMaterno(it) },
            label = { Text("Apellido Materno") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = fecha,
            onValueChange = { viewModel.actualizarFecha(it) },
            label = { Text("Fecha de Nacimiento (AAMMDD)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = curp,
            onValueChange = { viewModel.actualizarCurp(it) },
            label = { Text("CURP (18 caracteres)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { viewModel.actualizarCorreo(it) },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = telefono,
            onValueChange = { viewModel.actualizarTelefono(it) },
            label = { Text("Teléfono (10 dígitos)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = cp,
            onValueChange = { viewModel.actualizarCP(it) },
            label = { Text("Código Postal") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = vialidad,
            onValueChange = { viewModel.actualizarVialidad(it) },
            label = { Text("Tipo de Vialidad (Ej. Calle, Avenida)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = actividad,
            onValueChange = { viewModel.actualizarActividad(it) },
            label = { Text("Actividad Económica") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = regimen,
            onValueChange = { viewModel.actualizarRegimen(it) },
            label = { Text("Régimen Fiscal") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(20.dp))

        Spacer(modifier = Modifier.height(20.dp))

        // --- SECCIÓN: RFC (LECTURA) ---
        OutlinedTextField(
            value = rfc,
            onValueChange = {},
            readOnly = true,
            label = { Text("RFC Calculado Automáticamente") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )

        Spacer(modifier = Modifier.height(24.dp))
        Divider()
        Spacer(modifier = Modifier.height(24.dp))

        // --- SECCIÓN: UBICACIÓN (COMBOBOXES) ---

        // 1. COMBOBOX DE ESTADOS
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
                estados.forEach { estado ->
                    DropdownMenuItem(
                        text = { Text(estado.Nombre_Estado) },
                        onClick = {
                            estadoSeleccionado = estado
                            viewModel.seleccionarEstado(estado.Id_Estado) // Carga municipios
                            municipioSeleccionado = null // Limpia municipio anterior
                            estadoExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. COMBOBOX DE MUNICIPIOS
        ExposedDropdownMenuBox(
            expanded = municipioExpanded,
            onExpandedChange = { if (estadoSeleccionado != null) municipioExpanded = it }
        ) {
            OutlinedTextField(
                value = municipioSeleccionado?.Nombre_Municipio ?: "Selecciona un Municipio",
                onValueChange = {},
                readOnly = true,
                label = { Text("Municipio") },
                enabled = estadoSeleccionado != null, // Solo se habilita si hay estado
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = municipioExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = municipioExpanded,
                onDismissRequest = { municipioExpanded = false }
            ) {
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

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (estadoSeleccionado != null && municipioSeleccionado != null) {
                    viewModel.guardarContribuyenteReal(
                        idEstado = estadoSeleccionado!!.Id_Estado,
                        idMunicipio = municipioSeleccionado!!.Id_Municipio
                    )

                    // Limpiamos los menús
                    estadoExpanded = false
                    municipioExpanded = false
                    estadoSeleccionado = null
                    municipioSeleccionado = null
                }
            },
            modifier = Modifier.fillMaxWidth(),
            // La BD va a explotar si no mandas 18 del CURP y 10 del Teléfono, así que protegemos el botón
            enabled = curp.length == 18 && telefono.length == 10 && municipioSeleccionado != null
        ) {
            Text("Guardar Contribuyente")
        }
    }
}