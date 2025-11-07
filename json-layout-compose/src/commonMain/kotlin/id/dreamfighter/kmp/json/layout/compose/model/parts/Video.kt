package id.dreamfighter.kmp.json.layout.compose.model.parts

import id.dreamfighter.kmp.json.layout.compose.model.type.Align
import id.dreamfighter.kmp.json.layout.compose.model.type.ItemColor
import id.dreamfighter.kmp.json.layout.compose.model.type.Type
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
@OptIn(ExperimentalUuidApi::class)
@SerialName("VIDEO")
data class Video(
    val imageAlign: Align = Align.CENTER,
    val url: String? = null,
    val headers: Map<String, String>? = null,

    // Overridden properties
    //override val type: Type = Type.VIDEO,
    override val alignment: Align = Align.CENTER,
    override val weight: Float = 0f,
    override val backgroundColor: ItemColor = ItemColor.NONE,
    override val props: Props? = null,
    override val name: String = Uuid.random().toString()
) : ListItems