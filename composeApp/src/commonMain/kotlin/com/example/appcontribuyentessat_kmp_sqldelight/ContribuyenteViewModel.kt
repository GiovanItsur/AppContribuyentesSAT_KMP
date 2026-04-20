package com.example.appcontribuyentessat_kmp_sqldelight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.appcontribuyentessat_kmp_sqldelight.database.SatDatabase
import com.example.appcontribuyentessatkmpsqldelight.database.PERSONAS_FISICAS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class ContribuyenteViewModel(private val db: SatDatabase) : ViewModel() {

    // ========================================================================
    // CONEXIÓN A LA BASE DE DATOS
    // ========================================================================
    private val queries = db.satDatabaseQueries

    init {
        // Al nacer el ViewModel, llenamos la BD con datos de prueba si está vacía
        inicializarDatosDePrueba()
    }

    // ========================================================================
    // 2. ESTADOS REACTIVOS: CATÁLOGOS (ESTADOS Y MUNICIPIOS)
    // ========================================================================
    // Lee la tabla ESTADOS y se actualiza solo si hay cambios.
    val estados = queries.obtenerEstados()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Guarda el ID del estado que el usuario seleccionó en la pantalla
    private val _estadoSeleccionadoId = MutableStateFlow<Long?>(null)

    fun seleccionarEstado(id: Long) {
        _estadoSeleccionadoId.value = id
    }

    // "Escucha" a _estadoSeleccionadoId. Si cambia, va a la BD y trae sus municipios (Ej. Guanajuato -> Uriangato, Moroleón...)
    val municipios = _estadoSeleccionadoId.flatMapLatest { idEstado ->
        if (idEstado == null) {
            flowOf(emptyList()) // Si no hay estado, devuelve lista vacía
        } else {
            queries.obtenerMunicipiosPorEstado(idEstado)
                .asFlow()
                .mapToList(Dispatchers.IO)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    // ========================================================================
    // 3. ESTADOS REACTIVOS: DATOS DEL FORMULARIO
    // ========================================================================
    // Estas variables (StateFlows) son el "espejo" de lo que el usuario escribe
    val curp = MutableStateFlow("")
    val nombre = MutableStateFlow("")
    val apellidoPaterno = MutableStateFlow("")
    val apellidoMaterno = MutableStateFlow("")
    val fechaNacimiento = MutableStateFlow("")
    val correo = MutableStateFlow("")
    val telefono = MutableStateFlow("")
    val codigoPostal = MutableStateFlow("")
    val tipoVialidad = MutableStateFlow("")
    val actividadEconomica = MutableStateFlow("")
    val regimenFiscal = MutableStateFlow("")

    // Funciones que la interfaz llama cada vez que el usuario teclea una letra
    fun actualizarCurp(nuevo: String) { curp.value = nuevo.take(18).uppercase() }
    fun actualizarNombre(nuevo: String) { nombre.value = nuevo }
    fun actualizarPaterno(nuevo: String) { apellidoPaterno.value = nuevo }
    fun actualizarMaterno(nuevo: String) { apellidoMaterno.value = nuevo }
    fun actualizarFecha(nuevo: String) { fechaNacimiento.value = nuevo }
    fun actualizarCorreo(nuevo: String) { correo.value = nuevo }
    fun actualizarTelefono(nuevo: String) { telefono.value = nuevo.filter { it.isDigit() }.take(10) }
    fun actualizarCP(nuevo: String) { codigoPostal.value = nuevo.filter { it.isDigit() }.take(5) }
    fun actualizarVialidad(nuevo: String) { tipoVialidad.value = nuevo }
    fun actualizarActividad(nuevo: String) { actividadEconomica.value = nuevo }
    fun actualizarRegimen(nuevo: String) { regimenFiscal.value = nuevo }


    // ========================================================================
    // 4. MAGIA REACTIVA: CÁLCULO DE RFC Y LISTADO
    // ========================================================================
    // Observa 4 variables al mismo tiempo. Si alguna cambia, recalcula el RFC al instante.
    val rfcGenerado = combine(
        nombre, apellidoPaterno, apellidoMaterno, fechaNacimiento
    ) { nom, pat, mat, fecha ->
        if (nom.isEmpty() || pat.isEmpty() || mat.isEmpty() || fecha.length < 6) ""
        else "${pat.take(2)}${mat.take(1)}${nom.take(1)}${fecha.take(6)}".uppercase()
    }.stateIn(viewModelScope, SharingStarted.Lazily, "")

    // Escucha la tabla de PERSONAS_FISICAS en tiempo real para pintar la lista
    val listaPersonas = queries.listarPersonasFisicas()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    // ========================================================================
    // 5. LÓGICA DE NEGOCIO: CRUD (CREAR, LEER, ACTUALIZAR, BORRAR)
    // ========================================================================

    // Bandera: Si es NULL, creamos uno nuevo. Si tiene texto, actualizamos ese registro.
    val curpEnEdicion = MutableStateFlow<String?>(null)

    fun guardarContribuyenteReal(idEstado: Long, idMunicipio: Long) {
        val apellidosJuntos = "${apellidoPaterno.value} ${apellidoMaterno.value}"

        if (curpEnEdicion.value != null) {
            // UPDATE: Actualizar persona existente
            queries.actualizarPersonaFisica(
                Nombre = nombre.value,
                Apellido = apellidosJuntos,
                Fecha_Nacimiento = fechaNacimiento.value,
                Correo = correo.value,
                Telefono = telefono.value,
                Codigo_Postal = codigoPostal.value,
                Tipo_Vialidad = tipoVialidad.value,
                Actividad_Economica = actividadEconomica.value,
                Regimen_Fiscal = regimenFiscal.value,
                CURP = curpEnEdicion.value!!
            )
        } else {
            // INSERT: Crear persona nueva
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
            // El domicilio solo se inserta la primera vez
            queries.insertarDomicilioReal(
                id_propietario = curp.value,
                codigoPostal = codigoPostal.value,
                tipoVialidad = tipoVialidad.value,
                Id_Estado = idEstado,
                Id_Municipio = idMunicipio
            )
        }
        limpiarFormulario()
    }

    fun borrarPersona(curpParaBorrar: String) {
        queries.borrarPersonaFisica(curpParaBorrar)
    }

    fun cargarPersonaParaEditar(persona: PERSONAS_FISICAS) {
        curpEnEdicion.value = persona.CURP
        curp.value = persona.CURP
        nombre.value = persona.Nombre

        // Separamos el apellido compuesto
        val partesApellido = persona.Apellido.split(" ")
        apellidoPaterno.value = partesApellido.getOrNull(0) ?: ""
        apellidoMaterno.value = partesApellido.getOrNull(1) ?: ""

        fechaNacimiento.value = persona.Fecha_Nacimiento
        correo.value = persona.Correo
        telefono.value = persona.Telefono
        codigoPostal.value = persona.Codigo_Postal
        tipoVialidad.value = persona.Tipo_Vialidad
        actividadEconomica.value = persona.Actividad_Economica
        regimenFiscal.value = persona.Regimen_Fiscal
    }

    fun limpiarFormulario() {
        curpEnEdicion.value = null
        curp.value = ""
        nombre.value = ""
        apellidoPaterno.value = ""
        apellidoMaterno.value = ""
        fechaNacimiento.value = ""
        correo.value = ""
        telefono.value = ""
        codigoPostal.value = ""
        tipoVialidad.value = ""
        actividadEconomica.value = ""
        regimenFiscal.value = ""
    }

    // ========================================================================
    // FUNCIONES PRIVADAS (HELPER)
    // ========================================================================
    private fun inicializarDatosDePrueba() {
        // Verificamos si la base de datos está vacía
        if (queries.contarEstados().executeAsOne() == 0L) {

            // ==========================================
            // LOS 32 ESTADOS DE LA REPÚBLICA MEXICANA
            // ==========================================
            queries.insertarEstado(1, "Aguascalientes")
            queries.insertarEstado(2, "Baja California")
            queries.insertarEstado(3, "Baja California Sur")
            queries.insertarEstado(4, "Campeche")
            queries.insertarEstado(5, "Coahuila")
            queries.insertarEstado(6, "Colima")
            queries.insertarEstado(7, "Chiapas")
            queries.insertarEstado(8, "Chihuahua")
            queries.insertarEstado(9, "Ciudad de México")
            queries.insertarEstado(10, "Durango")
            queries.insertarEstado(11, "Guanajuato")
            queries.insertarEstado(12, "Guerrero")
            queries.insertarEstado(13, "Hidalgo")
            queries.insertarEstado(14, "Jalisco")
            queries.insertarEstado(15, "Estado de México")
            queries.insertarEstado(16, "Michoacán")
            queries.insertarEstado(17, "Morelos")
            queries.insertarEstado(18, "Nayarit")
            queries.insertarEstado(19, "Nuevo León")
            queries.insertarEstado(20, "Oaxaca")
            queries.insertarEstado(21, "Puebla")
            queries.insertarEstado(22, "Querétaro")
            queries.insertarEstado(23, "Quintana Roo")
            queries.insertarEstado(24, "San Luis Potosí")
            queries.insertarEstado(25, "Sinaloa")
            queries.insertarEstado(26, "Sonora")
            queries.insertarEstado(27, "Tabasco")
            queries.insertarEstado(28, "Tamaulipas")
            queries.insertarEstado(29, "Tlaxcala")
            queries.insertarEstado(30, "Veracruz")
            queries.insertarEstado(31, "Yucatán")
            queries.insertarEstado(32, "Zacatecas")

            // ==========================================
            // MUNICIPIOS REPRESENTATIVOS (Por Estado)
            // ==========================================

            // 11. GUANAJUATO
            queries.insertarMunicipio(1, 11, "Uriangato")
            queries.insertarMunicipio(2, 11, "Moroleón")
            queries.insertarMunicipio(3, 11, "Yuriria")
            queries.insertarMunicipio(4, 11, "León")
            queries.insertarMunicipio(5, 11, "Irapuato")
            queries.insertarMunicipio(6, 11, "Celaya")
            queries.insertarMunicipio(7, 11, "Salamanca")
            queries.insertarMunicipio(8, 11, "Guanajuato")
            queries.insertarMunicipio(9, 11, "San Miguel de Allende")

            // 16. MICHOACÁN
            queries.insertarMunicipio(10, 16, "Morelia")
            queries.insertarMunicipio(11, 16, "Cuitzeo")
            queries.insertarMunicipio(12, 16, "Uruapan")
            queries.insertarMunicipio(13, 16, "Zamora")
            queries.insertarMunicipio(14, 16, "Pátzcuaro")

            // 14. JALISCO
            queries.insertarMunicipio(15, 14, "Guadalajara")
            queries.insertarMunicipio(16, 14, "Zapopan")
            queries.insertarMunicipio(17, 14, "Tlaquepaque")
            queries.insertarMunicipio(18, 14, "Tonalá")
            queries.insertarMunicipio(19, 14, "Puerto Vallarta")

            // 9. CIUDAD DE MÉXICO
            queries.insertarMunicipio(20, 9, "Coyoacán")
            queries.insertarMunicipio(21, 9, "Cuauhtémoc")
            queries.insertarMunicipio(22, 9, "Miguel Hidalgo")
            queries.insertarMunicipio(23, 9, "Tlalpan")

            // 19. NUEVO LEÓN
            queries.insertarMunicipio(24, 19, "Monterrey")
            queries.insertarMunicipio(25, 19, "San Pedro Garza García")
            queries.insertarMunicipio(26, 19, "Guadalupe")
            queries.insertarMunicipio(27, 19, "Apodaca")
        }
    }
}