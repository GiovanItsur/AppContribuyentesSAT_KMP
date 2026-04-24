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
    onNavigateToList: () -> Unit
) {
    // ========================================================================
    // 1. RECOLECCIÓN DE ESTADOS (LO QUE VAMOS A DIBUJAR)
    // ========================================================================

    // Switch de Pestañas
    val esFisica by viewModel.esPersonaFisica.collectAsState()

    // Bandera para saber si el título dice "Crear" o "Editar"
    val modoEdicionFisica by viewModel.curpEnEdicion.collectAsState()
    val modoEdicionMoral by viewModel.rfcEnEdicion.collectAsState()

    // Datos de Persona Física
    val curp by viewModel.curp.collectAsState()
    val nombre by viewModel.nombre.collectAsState()
    val paterno by viewModel.apellidoPaterno.collectAsState()
    val materno by viewModel.apellidoMaterno.collectAsState()
    val fecha by viewModel.fechaNacimiento.collectAsState()
    val rfcGenerado by viewModel.rfcGenerado.collectAsState()

    // Datos de Persona Moral
    val rfcMoral by viewModel.rfcMoral.collectAsState()
    val denominacion by viewModel.denominacionSocial.collectAsState()
    val fechaConst by viewModel.fechaConstitucion.collectAsState()
    val rfcRep by viewModel.rfcRepresentante.collectAsState()
    val escritura by viewModel.numEscritura.collectAsState()
    val capital by viewModel.regimenCapital.collectAsState()

    // Datos Compartidos (Contacto y Domicilio)
    val correo by viewModel.correo.collectAsState()
    val telefono by viewModel.telefono.collectAsState()
    val cp by viewModel.codigoPostal.collectAsState()
    val vialidad by viewModel.tipoVialidad.collectAsState()
    val actividad by viewModel.actividadEconomica.collectAsState()
    val regimen by viewModel.regimenFiscal.collectAsState()

    // Catálogos
    val estados by viewModel.estados.collectAsState()
    val municipios by viewModel.municipios.collectAsState()

    // Control local para los menús desplegables
    var estadoExpanded by remember { mutableStateOf(false) }
    var estadoSeleccionado by remember { mutableStateOf<ESTADOS?>(null) }
    var municipioExpanded by remember { mutableStateOf(false) }
    var municipioSeleccionado by remember { mutableStateOf<MUNICIPIOS?>(null) }


    // ========================================================================
    // 2. DISEÑO PRINCIPAL DE LA PANTALLA
    // ========================================================================
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())
    ) {
        // --- TÍTULO DINÁMICO ---
        Text(
            text = if (modoEdicionFisica != null || modoEdicionMoral != null) "Editar Contribuyente" else "Registro de Contribuyente",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- PESTAÑAS (FÍSICA / MORAL) ---
        TabRow(selectedTabIndex = if (esFisica) 0 else 1) {
            Tab(
                selected = esFisica,
                onClick = { viewModel.cambiarTipoPersona(true) },
                text = { Text("Persona Física") }
            )
            Tab(
                selected = !esFisica,
                onClick = { viewModel.cambiarTipoPersona(false) },
                text = { Text("Persona Moral") }
            )
        }
        Spacer(modifier = Modifier.height(20.dp))


        // ========================================================================
        // 3. FORMULARIOS DINÁMICOS (CAMBIAN SEGÚN LA PESTAÑA)
        // ========================================================================
        if (esFisica) {
            // ---> MODO: PERSONA FÍSICA <---
            OutlinedTextField(value = curp, onValueChange = { viewModel.actualizarCurp(it) }, label = { Text("CURP (18 caracteres)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = nombre, onValueChange = { viewModel.actualizarNombre(it) }, label = { Text("Nombre(s)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = paterno, onValueChange = { viewModel.actualizarPaterno(it) }, label = { Text("Apellido Paterno") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = materno, onValueChange = { viewModel.actualizarMaterno(it) }, label = { Text("Apellido Materno") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = fecha, onValueChange = { viewModel.actualizarFecha(it) }, label = { Text("Fecha de Nacimiento (AAMMDD)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = correo, onValueChange = { viewModel.actualizarCorreo(it) }, label = { Text("Correo Electrónico") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = telefono, onValueChange = { viewModel.actualizarTelefono(it) }, label = { Text("Teléfono (10 dígitos)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = regimen, onValueChange = { viewModel.actualizarRegimen(it) }, label = { Text("Régimen Fiscal") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            // RFC Autocalculado
            OutlinedTextField(value = rfcGenerado, onValueChange = {}, readOnly = true, label = { Text("RFC Calculado Automáticamente") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant, unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant))

        } else {
            // ---> MODO: PERSONA MORAL <---
            OutlinedTextField(value = rfcMoral, onValueChange = { viewModel.actualizarRfcMoral(it) }, label = { Text("RFC de la Empresa") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = denominacion, onValueChange = { viewModel.actualizarDenominacion(it) }, label = { Text("Denominación Social (Nombre)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = fechaConst, onValueChange = { viewModel.actualizarFechaConstitucion(it) }, label = { Text("Fecha de Constitución") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = rfcRep, onValueChange = { viewModel.actualizarRfcRepresentante(it) }, label = { Text("RFC del Representante Legal") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = escritura, onValueChange = { viewModel.actualizarNumEscritura(it) }, label = { Text("Número de Escritura") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = capital, onValueChange = { viewModel.actualizarRegimenCapital(it) }, label = { Text("Régimen de Capital (Ej. S.A. de C.V.)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))


        // ========================================================================
        // 4. DATOS COMPARTIDOS Y DOMICILIO
        // ========================================================================
        Text("Datos Generales y Domicilio", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = actividad, onValueChange = { viewModel.actualizarActividad(it) }, label = { Text("Actividad Económica Principal") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = cp, onValueChange = { viewModel.actualizarCP(it) }, label = { Text("Código Postal") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = vialidad, onValueChange = { viewModel.actualizarVialidad(it) }, label = { Text("Tipo de Vialidad (Ej. Calle, Avenida)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(modifier = Modifier.height(16.dp))

        // --- MENÚ DESPLEGABLE: ESTADOS ---
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
                            viewModel.seleccionarEstado(estado.Id_Estado) // Detona la búsqueda de municipios
                            municipioSeleccionado = null
                            estadoExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // --- MENÚ DESPLEGABLE: MUNICIPIOS ---
        ExposedDropdownMenuBox(
            expanded = municipioExpanded,
            onExpandedChange = { if (estadoSeleccionado != null) municipioExpanded = it }
        ) {
            OutlinedTextField(
                value = municipioSeleccionado?.Nombre_Municipio ?: "Selecciona un Municipio",
                onValueChange = {}, readOnly = true, label = { Text("Municipio") },
                enabled = estadoSeleccionado != null, // Protegido: Solo se abre si ya hay estado
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
        // 5. BOTONES DE ACCIÓN FINAL
        // ========================================================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // BOTÓN: CANCELAR
            OutlinedButton(
                onClick = {
                    viewModel.limpiarFormulario()
                    onNavigateToList()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }

            // Lógica inteligente para habilitar el botón de Guardar
            val formularioValido = if (esFisica) {
                curp.length == 18 && telefono.length == 10 && municipioSeleccionado != null
            } else {
                rfcMoral.isNotBlank() && municipioSeleccionado != null
            }

            // BOTÓN: GUARDAR / ACTUALIZAR
            Button(
                onClick = {
                    if (estadoSeleccionado != null && municipioSeleccionado != null) {
                        viewModel.guardarContribuyenteReal(
                            idEstado = estadoSeleccionado!!.Id_Estado,
                            idMunicipio = municipioSeleccionado!!.Id_Municipio
                        )
                        // Limpiamos los menús al terminar
                        estadoExpanded = false
                        municipioExpanded = false
                        estadoSeleccionado = null
                        municipioSeleccionado = null
                        onNavigateToList()
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = formularioValido // Se bloquea si faltan datos importantes
            ) {
                Text(if (modoEdicionFisica != null || modoEdicionMoral != null) "Actualizar" else "Guardar")
            }
        }
    }
}