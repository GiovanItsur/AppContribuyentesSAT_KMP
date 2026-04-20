package com.example.appcontribuyentessat_kmp_sqldelight

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ListaScreen(
    viewModel: ContribuyenteViewModel,
    onNavigateToForm: () -> Unit // Función puente para cambiar a la pantalla del formulario
) {
    // ========================================================================
    // 1. ESTADO REACTIVO DE LA BASE DE DATOS
    // ========================================================================
    // Escucha la tabla de PERSONAS_FISICAS. Si alguien se agrega o borra, se actualiza sola.
    val personas by viewModel.listaPersonas.collectAsState()

    // ========================================================================
    // 2. DISEÑO DE LA PANTALLA PRINCIPAL
    // ========================================================================
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- ENCABEZADO Y BOTÓN DE NUEVO ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Contribuyentes",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = {
                    viewModel.limpiarFormulario() // Nos aseguramos de entrar en modo "Crear"
                    onNavigateToForm()
                }
            ) {
                Text("Nuevo +")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        // ========================================================================
        // 3. RENDERIZADO CONDICIONAL (VACÍO VS LLENO)
        // ========================================================================
        if (personas.isEmpty()) {
            // Pantalla cuando no hay datos
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay personas registradas aún.\n¡Haz clic en 'Nuevo' para empezar!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // ========================================================================
            // 4. LISTA DINÁMICA EFICIENTE (LAZY COLUMN)
            // ========================================================================
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre tarjetas
            ) {
                items(personas) { persona ->

                    // Diseño de cada "Tarjeta" de contribuyente
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Información del lado izquierdo
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${persona.Nombre} ${persona.Apellido}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "CURP: ${persona.CURP}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Tel: ${persona.Telefono} | Régimen: ${persona.Regimen_Fiscal}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Botones de Acción (Emoji Hack) del lado derecho
                            Row {
                                IconButton(
                                    onClick = {
                                        viewModel.cargarPersonaParaEditar(persona) // Carga los datos al ViewModel
                                        onNavigateToForm() // Brinca de pantalla
                                    }
                                ) {
                                    Text("✏️", style = MaterialTheme.typography.titleLarge)
                                }
                                IconButton(
                                    onClick = { viewModel.borrarPersona(persona.CURP) }
                                ) {
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