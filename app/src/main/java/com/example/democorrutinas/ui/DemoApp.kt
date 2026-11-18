package com.example.democorrutinas.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.democorrutinas.ui.screens.DemoScreen
import com.example.democorrutinas.viewmodel.DemoViewModel

/** Único sitio que conoce el ViewModel. La pantalla solo recibe datos y lambdas. */
@Composable
fun DemoApp(
    modifier: Modifier = Modifier,
    viewModel: DemoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    DemoScreen(
        uiState = uiState,
        onThreadSleep = { viewModel.esperarConThreadSleep() },
        onDelay = { viewModel.esperarConDelay() },
        onRedHiloPrincipal = { viewModel.redEnHiloPrincipal() },
        onRedCorrutina = { viewModel.redConCorrutina() },
        modifier = modifier
    )
}
