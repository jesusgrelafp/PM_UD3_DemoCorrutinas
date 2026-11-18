package com.example.democorrutinas.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.democorrutinas.R
import com.example.democorrutinas.model.DemoUiState
import com.example.democorrutinas.model.Resultado
import com.example.democorrutinas.ui.theme.DemoCorrutinasTheme

@Composable
fun DemoScreen(
    uiState: DemoUiState,
    onThreadSleep: () -> Unit,
    onDelay: () -> Unit,
    onRedHiloPrincipal: () -> Unit,
    onRedCorrutina: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Contador local: si no sube al pulsar, la app no está respondiendo a los toques.
    var pulsaciones by rememberSaveable { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Gira siempre. Si se para, el hilo principal está bloqueado.
        CircularProgressIndicator(modifier = Modifier.size(72.dp))

        Text(
            text = textoResultado(uiState.resultado),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        if (uiState.cargando) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        } else {
            // Mismo alto que la barra para que los botones no se muevan.
            HorizontalDivider(modifier = Modifier.height(4.dp))
        }

        OutlinedButton(onClick = { pulsaciones++ }) {
            Text(text = stringResource(R.string.pulsame, pulsaciones))
        }

        Button(onClick = onThreadSleep, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(R.string.boton_thread_sleep))
        }
        Button(onClick = onDelay, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(R.string.boton_delay))
        }
        Button(onClick = onRedHiloPrincipal, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(R.string.boton_red_main))
        }
        Button(onClick = onRedCorrutina, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(R.string.boton_red_corrutina))
        }
    }
}

@Composable
private fun textoResultado(resultado: Resultado): String = when (resultado) {
    is Resultado.Inicio -> stringResource(R.string.resultado_inicio)
    is Resultado.Esperando -> stringResource(R.string.resultado_esperando)
    is Resultado.EsperaTerminada ->
        stringResource(R.string.resultado_espera, resultado.metodo, resultado.hilo)
    is Resultado.Descargado ->
        stringResource(R.string.resultado_descarga, resultado.caracteres, resultado.hilo)
    is Resultado.Error -> stringResource(R.string.resultado_error, resultado.excepcion)
}

@Preview(showBackground = true)
@Composable
fun DemoScreenPreview() {
    DemoCorrutinasTheme {
        DemoScreen(
            uiState = DemoUiState(),
            onThreadSleep = {},
            onDelay = {},
            onRedHiloPrincipal = {},
            onRedCorrutina = {}
        )
    }
}
