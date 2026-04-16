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

    fun seleccionarEstado(id: Long) {
        _estadoSeleccionadoId.value = id
    }
}