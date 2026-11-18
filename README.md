# PM_UD3_DemoCorrutinas

App de demostración para la sección **1.1. Conceptos de corrutinas** de la UD3.

Una pantalla con un **spinner que gira siempre** y un botón **«Púlsame»** con contador. Si el spinner se para o el contador no sube, el hilo principal está bloqueado.

| Botón | Qué hace | Qué se ve |
|---|---|---|
| 1. Esperar 3 s con `Thread.sleep` | Bloquea el hilo principal | El spinner se congela 3 s y «Púlsame» no responde |
| 2. Esperar 3 s con `delay` | Suspende una corrutina en `viewModelScope` | El spinner sigue girando y se puede pulsar |
| 3. Descarga en el hilo principal | `URL(...).readText()` sin corrutina | Muestra `NetworkOnMainThreadException` |
| 4. Descarga con `withContext(Dispatchers.IO)` | Descarga con una función *main-safe* | El spinner sigue girando y muestra los caracteres recibidos |

## Cómo abrir el proyecto

1. Descomprime el zip.
2. En Android Studio: **File → Open** y elige la carpeta `DemoCorrutinas`.
3. Espera a que termine la sincronización de Gradle (la primera vez descarga Gradle 9.5.0 y las dependencias).
4. Ejecuta en un emulador o en un móvil.

Versiones (estables en noviembre de 2026):

| Elemento | Versión |
|---|---|
| Android Gradle Plugin | 9.3.3 |
| Gradle | 9.5.0 |
| Kotlin (y plugin de Compose) | 2.4.10 |
| Compose BOM | 2026.09.00 |
| lifecycle-viewmodel-compose | 2.9.4 (la misma que en la UD2) |
| activity-compose | 1.11.0 |
| compileSdk / targetSdk / minSdk | 37 / 36 / 24 |

AGP 9 trae **Kotlin integrado**, por eso el proyecto no aplica el plugin `org.jetbrains.kotlin.android`. La versión de Kotlin se fija en `build.gradle.kts` (raíz) y debe coincidir con la del plugin de Compose (`kotlin` en `gradle/libs.versions.toml`).

Si tu Android Studio es más antiguo y no admite AGP 9.3.3, tienes dos opciones:
- Aceptar el asistente de actualización que te proponga.
- Crear un proyecto nuevo **Empty Activity** con nombre `DemoCorrutinas` y paquete `com.example.democorrutinas`, y copiar encima la carpeta `app/src/main/` de este zip. Después, añadir la dependencia `lifecycle-viewmodel-compose` como en la UD2.

## Guion para clase

1. **Botón 1.** Púlsalo y, durante la espera, toca «Púlsame» varias veces. El contador no sube hasta que termina la espera y entonces procesa los toques de golpe. Ni siquiera llega a verse «Esperando…», porque el hilo principal no puede redibujar.
2. **Botón 2.** Lo mismo con `delay`: el spinner sigue girando y el contador sube. El mensaje final dice que la corrutina terminó en el hilo `main`: se suspendió y se reanudó en el mismo hilo sin bloquearlo.
3. **Botón 3.** Android no deja hacer peticiones de red en el hilo principal. En una app real esta excepción la cerraría. Aquí se captura solo para enseñarla.
4. **Botón 4.** La descarga se hace en el hilo IO. Abre **Logcat**, filtra por `DemoCorrutinas` y verás `Descargando en el hilo DefaultDispatcher-worker-N`. El mensaje de la pantalla confirma que el resultado vuelve al hilo `main`.
5. **ANR.** En `DemoViewModel.kt`, cambia `ESPERA_MS` a `10000L`, pulsa el botón 1 y toca la pantalla: a los 5 s aparece el diálogo «La aplicación no responde».
6. **Sin conexión.** Activa el modo avión y pulsa el botón 4: se captura la `IOException` (`UnknownHostException`) y la app no se cierra.

## Estructura del código (`app/src/main/java/com/example/democorrutinas/`)

```
model/DemoUiState.kt        Estado de la pantalla (data class + sealed interface Resultado)
viewmodel/DemoViewModel.kt  Los cuatro casos
ui/DemoApp.kt               Único sitio que usa el ViewModel
ui/screens/DemoScreen.kt    Pantalla sin estado: recibe datos y lambdas
ui/theme/                   Tema de Material 3 (el que genera Android Studio)
MainActivity.kt             setContent { DemoCorrutinasTheme { DemoApp() } }
```
