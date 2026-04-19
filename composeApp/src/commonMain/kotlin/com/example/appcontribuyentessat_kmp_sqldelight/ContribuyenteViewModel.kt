package com.example.appcontribuyentessat_kmp_sqldelight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.appcontribuyentessat_kmp_sqldelight.database.SatDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.*

class ContribuyenteViewModel(private val db: SatDatabase) : ViewModel() {
    private val queries = db.satDatabaseQueries

    val estados = queries.obtenerEstados()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    private val _estadoSeleccionadoId = MutableStateFlow<Long?>(null)

    val municipios = _estadoSeleccionadoId.flatMapLatest { idEstado ->
        if (idEstado == null) {
            flowOf(emptyList())
        } else {
            queries.obtenerMunicipiosPorEstado(idEstado)
                .asFlow()
                .mapToList(Dispatchers.IO)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val nombre = MutableStateFlow("")
    val apellidoPaterno = MutableStateFlow("")
    val apellidoMaterno = MutableStateFlow("")
    val fechaNacimiento = MutableStateFlow("") // Formato: YYMMDD
    val curp = MutableStateFlow("") // La BD exige 18 caracteres exactos
    val correo = MutableStateFlow("")
    val telefono = MutableStateFlow("") // La BD exige 10 caracteres exactos
    val codigoPostal = MutableStateFlow("")
    val tipoVialidad = MutableStateFlow("")
    val actividadEconomica = MutableStateFlow("")
    val regimenFiscal = MutableStateFlow("")



    val rfcGenerado = combine(
        nombre, apellidoPaterno, apellidoMaterno, fechaNacimiento
    ) { nom, pat, mat, fecha ->
        generarRFCSimple(nom, pat, mat, fecha)
    }.stateIn(viewModelScope, SharingStarted.Lazily, "")

    // Función auxiliar básica (se puede mejorar la regla después)
    private fun generarRFCSimple(n: String, p: String, m: String, f: String): String {
        if (n.isEmpty() || p.isEmpty() || m.isEmpty() || f.length < 6) return ""
        return "${p.take(2)}${m.take(1)}${n.take(1)}${f.take(6)}".uppercase()
    }

    // Funciones para actualizar los textos desde la UI
    fun actualizarNombre(nuevo: String) { nombre.value = nuevo }
    fun actualizarPaterno(nuevo: String) { apellidoPaterno.value = nuevo }
    fun actualizarMaterno(nuevo: String) { apellidoMaterno.value = nuevo }
    fun actualizarFecha(nuevo: String) { fechaNacimiento.value = nuevo }
    fun actualizarCurp(nuevo: String) { curp.value = nuevo.take(18).uppercase() }
    fun actualizarCorreo(nuevo: String) { correo.value = nuevo }
    fun actualizarTelefono(nuevo: String) { telefono.value = nuevo.filter { it.isDigit() }.take(10) }
    fun actualizarCP(nuevo: String) { codigoPostal.value = nuevo.filter { it.isDigit() }.take(5) }
    fun actualizarVialidad(nuevo: String) { tipoVialidad.value = nuevo }
    fun actualizarActividad(nuevo: String) { actividadEconomica.value = nuevo }
    fun actualizarRegimen(nuevo: String) { regimenFiscal.value = nuevo }

    fun guardarContribuyenteReal(idEstado: Long, idMunicipio: Long) {
        val apellidosJuntos = "${apellidoPaterno.value} ${apellidoMaterno.value}"

        // 1. Guardamos Persona
        queries.insertarPersonaFisicaReal(
            CURP = curp.value,
            Nombre = nombre.value,
            Apellido = apellidosJuntos,
            Fecha_Nacimiento = fechaNacimiento.value,
            Correo = correo.value,
            Telefono = telefono.value,
            Codigo_Postal = codigoPostal.value,
            Tipo_Vialidad = tipoVialidad.value,
            Actividad_Economica = actividadEconomica.value,
            Regimen_Fiscal = regimenFiscal.value
        )

        // 2. Guardamos Domicilio
        queries.insertarDomicilioReal(
            id_propietario = curp.value,
            codigoPostal = codigoPostal.value,
            tipoVialidad = tipoVialidad.value,
            Id_Estado = idEstado,
            Id_Municipio = idMunicipio
        )
    }

    init {
        inicializarDatosDePrueba()
    }

    private fun inicializarDatosDePrueba() {
        //Verificamos si la base de datos está vacía
        val cantidadEstados = queries.contarEstados().executeAsOne()

        if (cantidadEstados == 0L) {
            // Insertamos Guanajuato (ID: 1) y otro estado
            queries.insertarEstado(1, "Guanajuato")
            queries.insertarEstado(2, "Michoacán")

            // Insertamos municipios ligados a Guanajuato (Id_Estado = 1)
            queries.insertarMunicipio(1, 1, "Uriangato")
            queries.insertarMunicipio(2, 1, "Moroleón")
            queries.insertarMunicipio(3, 1, "León")

            // Insertamos municipios ligados a Michoacán (Id_Estado = 2)
            queries.insertarMunicipio(4, 2, "Morelia")
            queries.insertarMunicipio(5, 2, "Cuitzeo")
        }
    }

    fun seleccionarEstado(id: Long) {
        _estadoSeleccionadoId.value = id
    }

}