package id.dreamfighter.kmp.json.layout.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun VideoPlayer(
    modifier: Modifier, // <-- ADDED MODIFIER
    uris: List<String>,
    headers: List<Map<String, String>>,
    listener: (Int, String?) -> Unit
) {
}