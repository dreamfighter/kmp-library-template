package id.dreamfighter.kmp.json.layout.compose

import androidx.compose.runtime.Composable

@Composable
actual fun VideoPlayer(
    uris: List<String>,
    headers: List<Map<String, String>>,
    listener: (Int, String?) -> Unit
) {
}