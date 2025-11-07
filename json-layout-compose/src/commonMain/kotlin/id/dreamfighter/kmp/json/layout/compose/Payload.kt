package id.dreamfighter.kmp.json.layout.compose

import id.dreamfighter.kmp.json.layout.compose.model.parts.ListItems
import kotlinx.serialization.Serializable

@Serializable
data class Payload(
    val listItems: List<ListItems> = emptyList()
)