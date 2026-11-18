package com.example.democorrutinas.viewmodel

import android.os.NetworkOnMainThreadException
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.democorrutinas.model.DemoUiState
import com.example.democorrutinas.model.Resultado
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL

class DemoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DemoUiState())
    val uiState: StateFlow<DemoUiState> = _uiState.asStateFlow()

    // 1. ✗ Bloquea el hilo principal: el camarero se queda esperando en la cocina.
    //    Fíjate en que ni siquiera llega a verse "Esperando…": el hilo no puede redibujar.
    fun esperarConThreadSleep() {
        _uiState.update { it.copy(cargando = true, resultado = Resultado.Esperando) }
        Thread.sleep(ESPERA_MS)
        _uiState.update {
            it.copy(cargando = false, resultado = Resultado.EsperaTerminada("Thread.sleep", hilo()))
        }
    }

    // 2. ✓ Suspende la corrutina: el camarero deja la comanda y sigue atendiendo.
    fun esperarConDelay() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, resultado = Resultado.Esperando) }
            delay(ESPERA_MS)
            _uiState.update {
                it.copy(cargando = false, resultado = Resultado.EsperaTerminada("delay", hilo()))
            }
        }
    }

    // 3. ✗ Petición de red en el hilo principal: Android lo prohíbe.
    //    Capturamos la excepción solo para enseñarla en pantalla en lugar de cerrar la app.
    fun redEnHiloPrincipal() {
        try {
            val json = URL(URL_PRODUCTOS).readText()
            _uiState.update { it.copy(resultado = Resultado.Descargado(json.length, hilo())) }
        } catch (e: NetworkOnMainThreadException) {
            Log.e(TAG, "Red en el hilo principal", e)
            _uiState.update { it.copy(resultado = Resultado.Error("NetworkOnMainThreadException")) }
        }
    }

    // 4. ✓ Petición de red con una función main-safe.
    fun redConCorrutina() {
        viewModelScope.launch {                                   // hilo Main
            _uiState.update { it.copy(cargando = true, resultado = Resultado.Esperando) }
            try {
                val json = descargar()                            // se suspende: Main queda libre
                _uiState.update {                                 // de vuelta en Main
                    it.copy(cargando = false, resultado = Resultado.Descargado(json.length, hilo()))
                }
            } catch (e: IOException) {                            // sin conexión, servidor caído…
                _uiState.update {
                    it.copy(cargando = false, resultado = Resultado.Error(e::class.simpleName ?: "IOException"))
                }
            }
        }
    }

    // Función main-safe: ella misma se va al hilo IO y devuelve el resultado.
    private suspend fun descargar(): String = withContext(Dispatchers.IO) {
        Log.d(TAG, "Descargando en el hilo ${hilo()}")            // DefaultDispatcher-worker-N
        URL(URL_PRODUCTOS).readText()
    }

    private fun hilo(): String = Thread.currentThread().name

    companion object {
        private const val TAG = "DemoCorrutinas"
        // Prueba a subirlo a 10000 y toca la pantalla durante la espera del botón 1: aparecerá el ANR.
        private const val ESPERA_MS = 3000L
        private const val URL_PRODUCTOS = "https://dummyjson.com/products?limit=5"
    }
}
