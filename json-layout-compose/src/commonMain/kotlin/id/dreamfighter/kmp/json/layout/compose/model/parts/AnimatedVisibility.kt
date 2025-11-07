package id.dreamfighter.kmp.json.layout.compose.model.parts

import id.dreamfighter.kmp.json.layout.compose.model.type.Align
import id.dreamfighter.kmp.json.layout.compose.model.type.ItemColor
import id.dreamfighter.kmp.json.layout.compose.model.type.Type
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
@SerialName("ANIMATED_VISIBILITY")
data class AnimatedVisibility(
    val listItems: List<ListItems>? = null,
    val animationType: List<String> = listOf(),
    val enterDelay: Long = 10000,
    val exitDelay: Long = 10000,

    // Overridden properties
    //override val type: Type = Type.ANIMATED_VISIBILITY,
    override val alignment: Align = Align.NONE,
    override val weight: Float = 0f,
    override val backgroundColor: ItemColor = ItemColor.NONE,
    override val props: Props? = null,
    override val name: String = Uuid.random().toString()
) : ListItems