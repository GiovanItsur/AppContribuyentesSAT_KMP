package com.example.appcontribuyentessat_kmp_sqldelight

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ListaScreen(
    viewModel: ContribuyenteViewModel,
    onNavigateToForm: () -> Unit
) {
    // ========================================================================
    // 1. ESTADOS Y REACTIVIDAD (Escuchando a la Base de Datos)
    // ========================================================================
    // Estas variables se actualizan solas cada vez que hay un INSERT, UPDATE o DELETE en SQL
    val fisicas by viewModel.listaPersonas.collectAsState()
    val morales by viewModel.listaPersonasMorales.collectAsState()

    // Memoria de la interfaz: Recuerda en qué pestaña estamos (0 = Físicas, 1 = Morales)
    var tabSeleccionado by remember { mutableStateOf(0) }


    // ========================================================================
    // 2. DISEÑO PRINCIPAL
    // ========================================================================
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {

        // --- ENCABEZADO Y BOTÓN FLOTANTE ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Contribuyentes Registrados",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = {
                    viewModel.limpiarFormulario() // Aseguramos que el formulario esté en blanco

                    // Truco UX: Si le da a "Nuevo" estando en la pestaña Morales,
                    // el formulario se abre directamente en la sección de Morales.
                    viewModel.cambiarTipoPersona(tabSeleccionado == 0)

                    onNavigateToForm() // Brincamos de pantalla
                }
            ) {
                Text("Nuevo +")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ========================================================================
        // 3. PESTAÑAS (TABS) DE NAVEGACIÓN INTERNA
        // ========================================================================
        TabRow(selectedTabIndex = tabSeleccionado) {
            Tab(
                selected = tabSeleccionado == 0,
                onClick = { tabSeleccionado = 0 },
                text = { Text("Personas Físicas") }
            )
            Tab(
                selected = tabSeleccionado == 1,
                onClick = { tabSeleccionado = 1 },
                text = { Text("Personas Morales") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ========================================================================
        // 4. RENDERIZADO CONDICIONAL (Físicas vs Morales)
        // ========================================================================
        if (tabSeleccionado == 0) {

            // --------------------------------------------------
            // PESTAÑA: PERSONAS FÍSICAS
            // --------------------------------------------------
            if (fisicas.isEmpty()) {
                // Pantalla vacía (Empty State)
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay Personas Físicas registradas.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                // Lista eficiente en memoria
                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(fisicas) { persona ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Lado Izquierdo: Datos de la persona
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "${persona.Nombre} ${persona.Apellido}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "CURP: ${persona.CURP}", style = MaterialTheme.typography.bodyMedium)
                                    Text(text = "Tel: ${persona.Telefono} | Régimen: ${persona.Regimen_Fiscal}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                // Lado Derecho: Botones de Acción (Editar y Borrar)
                                Row {
                                    IconButton(onClick = { viewModel.cargarPersonaParaEditar(persona); onNavigateToForm() }) {
                                        Text("✏️", style = MaterialTheme.typography.titleLarge)
                                    }
                                    IconButton(onClick = { viewModel.borrarPersona(persona.CURP) }) {
                                        Text("🗑️", style = MaterialTheme.typography.titleLarge)
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } else {

            // --------------------------------------------------
            // PESTAÑA: PERSONAS MORALES
            // --------------------------------------------------
            if (morales.isEmpty()) {
                // Pantalla vacía (Empty State)
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay Personas Morales registradas.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                // Lista eficiente en memoria
                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(morales) { empresa ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Lado Izquierdo: Datos de la empresa
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = empresa.Denominacion_Social, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "RFC: ${empresa.RFC}", style = MaterialTheme.typography.bodyMedium)
                                    Text(text = "Capital: ${empresa.Regimen_Capital} | Escritura: ${empresa.Num_Escritura}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                // Lado Derecho: Botones de Acción (Editar y Borrar)
                                Row {
                                    IconButton(onClick = { viewModel.cargarPersonaMoralParaEditar(empresa); onNavigateToForm() }) {
                                        Text("✏️", style = MaterialTheme.typography.titleLarge)
                                    }
                                    IconButton(onClick = { viewModel.borrarPersonaMoral(empresa.RFC) }) {
                                        Text("🗑️", style = MaterialTheme.typography.titleLarge)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}