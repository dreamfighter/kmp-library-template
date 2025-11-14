package id.dreamfighter.kmp.json.layout.compose

@androidx.compose.runtime.Composable
actual fun VideoPlayer(
    modifier: androidx.compose.ui.Modifier,
    uris: List<String>,
    headers: List<Map<String, String>>,
    listener: (Int, String?) -> Unit
) {
}