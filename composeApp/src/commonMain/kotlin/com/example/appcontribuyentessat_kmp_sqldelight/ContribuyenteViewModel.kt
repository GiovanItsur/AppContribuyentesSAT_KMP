package com.example.appcontribuyentessat_kmp_sqldelight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.appcontribuyentessat_kmp_sqldelight.database.SatDatabase
import com.example.appcontribuyentessatkmpsqldelight.database.PERSONAS_FISICAS
import com.example.appcontribuyentessatkmpsqldelight.database.PERSONAS_MORALES
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class ContribuyenteViewModel(private val db: SatDatabase) : ViewModel() {

    // ========================================================================
    // 1. CONFIGURACIÓN INICIAL Y BASE DE DATOS
    // ========================================================================
    private val queries = db.satDatabaseQueries

    init {
        // Arrancamos llenando los catálogos si la BD está limpia
        inicializarDatosDePrueba()
    }


    // ========================================================================
    // 2. CONTROL DE PESTAÑAS Y ESTADOS DE EDICIÓN
    // ========================================================================

    // Switch principal: ¿Estamos viendo Físicas (true) o Morales (false)?
    val esPersonaFisica = MutableStateFlow(true)

    // Banderas de seguridad: Si tienen texto, estamos editando; si son null, es registro nuevo
    val curpEnEdicion = MutableStateFlow<String?>(null)
    val rfcEnEdicion = MutableStateFlow<String?>(null)


    // ========================================================================
    // 3. VARIABLES DEL FORMULARIO (LO QUE EL USUARIO TECLEA)
    // ========================================================================

    // --- Personas Físicas ---
    val curp = MutableStateFlow("")
    val nombre = MutableStateFlow("")
    val apellidoPaterno = MutableStateFlow("")
    val apellidoMaterno = MutableStateFlow("")
    val fechaNacimiento = MutableStateFlow("")

    // --- Personas Morales ---
    val rfcMoral = MutableStateFlow("")
    val denominacionSocial = MutableStateFlow("")
    val fechaConstitucion = MutableStateFlow("")
    val rfcRepresentante = MutableStateFlow("")
    val numEscritura = MutableStateFlow("")
    val regimenCapital = MutableStateFlow("")

    // --- Datos Compartidos (Contacto y Domicilio) ---
    val correo = MutableStateFlow("")
    val telefono = MutableStateFlow("")
    val codigoPostal = MutableStateFlow("")
    val tipoVialidad = MutableStateFlow("")
    val actividadEconomica = MutableStateFlow("")
    val regimenFiscal = MutableStateFlow("")


    // ========================================================================
    // 4. DATOS CALCULADOS Y LISTAS REACTIVAS (SOLO LECTURA)
    // ========================================================================

    // Cálculo instantáneo del RFC usando las variables de la persona física
    val rfcGenerado = combine(
        nombre, apellidoPaterno, apellidoMaterno, fechaNacimiento
    ) { nom, pat, mat, fecha ->
        if (nom.isEmpty() || pat.isEmpty() || mat.isEmpty() || fecha.length < 6) ""
        else "${pat.take(2)}${mat.take(1)}${nom.take(1)}${fecha.take(6)}".uppercase()
    }.stateIn(viewModelScope, SharingStarted.Lazily, "")

    // Listas vivas de la BD que alimentan las tarjetas en la pantalla principal
    val listaPersonas = queries.listarPersonasFisicas().asFlow().mapToList(Dispatchers.IO).stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val listaPersonasMorales = queries.listarPersonasMorales().asFlow().mapToList(Dispatchers.IO).stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Catálogos dinámicos para los menús desplegables
    val estados = queries.obtenerEstados().asFlow().mapToList(Dispatchers.IO).stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Escucha al estado seleccionado y trae solo sus municipios correspondientes
    private val _estadoSeleccionadoId = MutableStateFlow<Long?>(null)
    val municipios = _estadoSeleccionadoId.flatMapLatest { idEstado ->
        if (idEstado == null) flowOf(emptyList())
        else queries.obtenerMunicipiosPorEstado(idEstado).asFlow().mapToList(Dispatchers.IO)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    // ========================================================================
    // 5. FUNCIONES DE ACTUALIZACIÓN (EL PUENTE CON LA INTERFAZ)
    // ========================================================================

    fun cambiarTipoPersona(esFisica: Boolean) { esPersonaFisica.value = esFisica }
    fun seleccionarEstado(id: Long) { _estadoSeleccionadoId.value = id }

    // --- Reglas de Físicas ---
    fun actualizarCurp(nuevo: String) { curp.value = nuevo.take(18).uppercase() }
    fun actualizarNombre(nuevo: String) { nombre.value = nuevo }
    fun actualizarPaterno(nuevo: String) { apellidoPaterno.value = nuevo }
    fun actualizarMaterno(nuevo: String) { apellidoMaterno.value = nuevo }
    fun actualizarFecha(nuevo: String) { fechaNacimiento.value = nuevo }

    // --- Reglas de Morales ---
    fun actualizarRfcMoral(nuevo: String) { rfcMoral.value = nuevo.take(13).uppercase() }
    fun actualizarDenominacion(nuevo: String) { denominacionSocial.value = nuevo }
    fun actualizarFechaConstitucion(nuevo: String) { fechaConstitucion.value = nuevo }
    fun actualizarRfcRepresentante(nuevo: String) { rfcRepresentante.value = nuevo.take(13).uppercase() }
    fun actualizarNumEscritura(nuevo: String) { numEscritura.value = nuevo }
    fun actualizarRegimenCapital(nuevo: String) { regimenCapital.value = nuevo }

    // --- Reglas de Compartidos ---
    fun actualizarCorreo(nuevo: String) { correo.value = nuevo }
    fun actualizarTelefono(nuevo: String) { telefono.value = nuevo.filter { it.isDigit() }.take(10) }
    fun actualizarCP(nuevo: String) { codigoPostal.value = nuevo.filter { it.isDigit() }.take(5) }
    fun actualizarVialidad(nuevo: String) { tipoVialidad.value = nuevo }
    fun actualizarActividad(nuevo: String) { actividadEconomica.value = nuevo }
    fun actualizarRegimen(nuevo: String) { regimenFiscal.value = nuevo }


    // ========================================================================
    // 6. OPERACIONES CRUD (CREAR, LEER, ACTUALIZAR, BORRAR)
    // ========================================================================

    fun guardarContribuyenteReal(idEstado: Long, idMunicipio: Long) {
        if (esPersonaFisica.value) {
            // === RUTA: PERSONA FÍSICA ===
            val apellidosJuntos = "${apellidoPaterno.value} ${apellidoMaterno.value}"
            if (curpEnEdicion.value != null) {
                queries.actualizarPersonaFisica(nombre.value, apellidosJuntos, fechaNacimiento.value, correo.value, telefono.value, codigoPostal.value, tipoVialidad.value, actividadEconomica.value, regimenFiscal.value, curpEnEdicion.value!!)
            } else {
                queries.insertarPersonaFisicaReal(curp.value, nombre.value, apellidosJuntos, fechaNacimiento.value, correo.value, telefono.value, codigoPostal.value, tipoVialidad.value, actividadEconomica.value, regimenFiscal.value)
            }
        } else {
            // === RUTA: PERSONA MORAL ===
            if (rfcEnEdicion.value != null) {
                queries.actualizarPersonaMoral(denominacionSocial.value, fechaConstitucion.value, rfcRepresentante.value, numEscritura.value, regimenCapital.value, actividadEconomica.value, rfcEnEdicion.value!!)
            } else {
                queries.insertarPersonaMoralReal(rfcMoral.value, denominacionSocial.value, fechaConstitucion.value, rfcRepresentante.value, numEscritura.value, regimenCapital.value, actividadEconomica.value)
            }
        }
        limpiarFormulario()
    }

    // Funciones para inyectar datos de la BD hacia la pantalla cuando queremos editar
    fun cargarPersonaParaEditar(persona: PERSONAS_FISICAS) {
        esPersonaFisica.value = true
        curpEnEdicion.value = persona.CURP

        curp.value = persona.CURP
        nombre.value = persona.Nombre

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

    fun cargarPersonaMoralParaEditar(persona: PERSONAS_MORALES) {
        esPersonaFisica.value = false
        rfcEnEdicion.value = persona.RFC

        rfcMoral.value = persona.RFC
        denominacionSocial.value = persona.Denominacion_Social
        fechaConstitucion.value = persona.Fecha_Constitucion
        rfcRepresentante.value = persona.RFC_Representante
        numEscritura.value = persona.Num_Escritura
        regimenCapital.value = persona.Regimen_Capital
        actividadEconomica.value = persona.Actividad_Economica
    }

    // Eliminación rápida
    fun borrarPersona(curpParaBorrar: String) = queries.borrarPersonaFisica(curpParaBorrar)
    fun borrarPersonaMoral(rfcParaBorrar: String) = queries.borrarPersonaMoral(rfcParaBorrar)

    fun limpiarFormulario() {
        // Restauramos banderas de edición
        curpEnEdicion.value = null
        rfcEnEdicion.value = null
        esPersonaFisica.value = true

        // Vaciamos físicas
        curp.value = ""
        nombre.value = ""
        apellidoPaterno.value = ""
        apellidoMaterno.value = ""
        fechaNacimiento.value = ""

        // Vaciamos morales
        rfcMoral.value = ""
        denominacionSocial.value = ""
        fechaConstitucion.value = ""
        rfcRepresentante.value = ""
        numEscritura.value = ""
        regimenCapital.value = ""

        // Vaciamos compartidos
        correo.value = ""
        telefono.value = ""
        codigoPostal.value = ""
        tipoVialidad.value = ""
        actividadEconomica.value = ""
        regimenFiscal.value = ""
    }

    // ========================================================================
    // 7. FUNCIONES PRIVADAS (HELPER)
    // ========================================================================

    private fun inicializarDatosDePrueba() {
        // Solo inyectamos los catálogos si es la primera vez que corre la app
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