package com.example.democorrutinas.model

/**
 * Estado de la pantalla de la demo.
 *
 * @property cargando true mientras hay una espera o una descarga en marcha.
 * @property resultado qué mensaje hay que mostrar.
 */
data class DemoUiState(
    val cargando: Boolean = false,
    val resultado: Resultado = Resultado.Inicio
)

/** Los mensajes posibles. La pantalla los convierte en textos de strings.xml. */
sealed interface Resultado {
    data object Inicio : Resultado
    data object Esperando : Resultado
    data class EsperaTerminada(val metodo: String, val hilo: String) : Resultado
    data class Descargado(val caracteres: Int, val hilo: String) : Resultado
    data class Error(val excepcion: String) : Resultado
}
