package com.example.appcontribuyentessat_kmp_sqldelight

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appcontribuyentessatkmpsqldelight.database.ESTADOS
import com.example.appcontribuyentessatkmpsqldelight.database.MUNICIPIOS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(
    viewModel: ContribuyenteViewModel,
    onNavigateToList: () -> Unit // Función puente para regresar a la lista
) {
    // ========================================================================
    // 1. RECOLECCIÓN DE DATOS DEL VIEWMODEL (REACTIVIDAD)
    // ========================================================================
    // 'collectAsState()' hace que la pantalla se redibuje sola si el ViewModel cambia
    val curp by viewModel.curp.collectAsState()
    val nombre by viewModel.nombre.collectAsState()
    val paterno by viewModel.apellidoPaterno.collectAsState()
    val materno by viewModel.apellidoMaterno.collectAsState()
    val fecha by viewModel.fechaNacimiento.collectAsState()
    val correo by viewModel.correo.collectAsState()
    val telefono by viewModel.telefono.collectAsState()
    val cp by viewModel.codigoPostal.collectAsState()
    val vialidad by viewModel.tipoVialidad.collectAsState()
    val actividad by viewModel.actividadEconomica.collectAsState()
    val regimen by viewModel.regimenFiscal.collectAsState()

    val rfc by viewModel.rfcGenerado.collectAsState()
    val modoEdicion by viewModel.curpEnEdicion.collectAsState()

    // Catálogos para los menús
    val estados by viewModel.estados.collectAsState()
    val municipios by viewModel.municipios.collectAsState()

    // ========================================================================
    // 2. ESTADOS LOCALES (SOLO PARA LA INTERFAZ VISUAL)
    // ========================================================================
    // 'remember' guarda estos datos temporales mientras la pantalla está abierta
    var estadoExpanded by remember { mutableStateOf(false) }
    var estadoSeleccionado by remember { mutableStateOf<ESTADOS?>(null) }

    var municipioExpanded by remember { mutableStateOf(false) }
    var municipioSeleccionado by remember { mutableStateOf<MUNICIPIOS?>(null) }


    // ========================================================================
    // 3. DISEÑO DE LA PANTALLA (UI)
    // ========================================================================
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()) // Permite deslizar hacia abajo
    ) {
        // Título dinámico
        Text(
            text = if (modoEdicion != null) "Editar Contribuyente" else "Registro de Contribuyente",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // --- SECCIÓN A: DATOS PERSONALES ---
        OutlinedTextField(
            value = curp, onValueChange = { viewModel.actualizarCurp(it) },
            label = { Text("CURP (18 caracteres)") }, modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = nombre, onValueChange = { viewModel.actualizarNombre(it) },
            label = { Text("Nombre(s)") }, modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = paterno, onValueChange = { viewModel.actualizarPaterno(it) },
            label = { Text("Apellido Paterno") }, modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = materno, onValueChange = { viewModel.actualizarMaterno(it) },
            label = { Text("Apellido Materno") }, modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = fecha, onValueChange = { viewModel.actualizarFecha(it) },
            label = { Text("Fecha de Nacimiento (AAMMDD)") }, modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        // --- SECCIÓN B: CONTACTO Y DOMICILIO ---
        OutlinedTextField(
            value = correo, onValueChange = { viewModel.actualizarCorreo(it) },
            label = { Text("Correo Electrónico") }, modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = telefono, onValueChange = { viewModel.actualizarTelefono(it) },
            label = { Text("Teléfono (10 dígitos)") }, modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = cp, onValueChange = { viewModel.actualizarCP(it) },
            label = { Text("Código Postal") }, modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = vialidad, onValueChange = { viewModel.actualizarVialidad(it) },
            label = { Text("Tipo de Vialidad (Ej. Calle, Avenida)") }, modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        // --- SECCIÓN C: FISCAL ---
        OutlinedTextField(
            value = actividad, onValueChange = { viewModel.actualizarActividad(it) },
            label = { Text("Actividad Económica") }, modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = regimen, onValueChange = { viewModel.actualizarRegimen(it) },
            label = { Text("Régimen Fiscal") }, modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        Spacer(modifier = Modifier.height(20.dp))

        // --- SECCIÓN D: RFC CALCULADO AUTOMÁTICAMENTE ---
        OutlinedTextField(
            value = rfc,
            onValueChange = {},
            readOnly = true, // El usuario no puede editar esto manualmente
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

        // --- SECCIÓN E: UBICACIÓN (CATÁLOGOS) ---
        // Menú de Estados
        ExposedDropdownMenuBox(
            expanded = estadoExpanded,
            onExpandedChange = { estadoExpanded = it }
        ) {
            OutlinedTextField(
                value = estadoSeleccionado?.Nombre_Estado ?: "Selecciona un Estado",
                onValueChange = {}, readOnly = true, label = { Text("Estado") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = estadoExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = estadoExpanded, onDismissRequest = { estadoExpanded = false }
            ) {
                estados.forEach { estado ->
                    DropdownMenuItem(
                        text = { Text(estado.Nombre_Estado) },
                        onClick = {
                            estadoSeleccionado = estado
                            viewModel.seleccionarEstado(estado.Id_Estado) // Detona la carga de municipios
                            municipioSeleccionado = null
                            estadoExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Menú de Municipios
        ExposedDropdownMenuBox(
            expanded = municipioExpanded,
            onExpandedChange = { if (estadoSeleccionado != null) municipioExpanded = it }
        ) {
            OutlinedTextField(
                value = municipioSeleccionado?.Nombre_Municipio ?: "Selecciona un Municipio",
                onValueChange = {}, readOnly = true, label = { Text("Municipio") },
                enabled = estadoSeleccionado != null, // Protegido: Solo se habilita si hay estado
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = municipioExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = municipioExpanded, onDismissRequest = { municipioExpanded = false }
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

        // ========================================================================
        // 4. BOTONES DE ACCIÓN FINAL
        // ========================================================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Botón Cancelar
            OutlinedButton(
                onClick = {
                    viewModel.limpiarFormulario()
                    onNavigateToList()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }

            // Botón Guardar / Actualizar
            Button(
                onClick = {
                    if (estadoSeleccionado != null && municipioSeleccionado != null) {
                        viewModel.guardarContribuyenteReal(
                            idEstado = estadoSeleccionado!!.Id_Estado,
                            idMunicipio = municipioSeleccionado!!.Id_Municipio
                        )
                        estadoExpanded = false
                        municipioExpanded = false
                        estadoSeleccionado = null
                        municipioSeleccionado = null
                        onNavigateToList()
                    }
                },
                modifier = Modifier.weight(1f),
                // Regla estricta para habilitar el botón y no crashear la base de datos
                enabled = curp.length == 18 && telefono.length == 10 && municipioSeleccionado != null
            ) {
                Text(if (modoEdicion != null) "Actualizar" else "Guardar")
            }
        }
    }
}