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
@SerialName("TEXT") // This must match the 'type' value in the JSON
data class Text(
    val message: String,
    val textAlign: Align = Align.CENTER,
    val textFont: String = "DEFAULT",
    val color: String = "#FF000000",
    val maxLines: Int = Int.MAX_VALUE,
    val fontWeight: String? = "NORMAL",
    var fontFamily: String? = null,

    // Overridden properties from ListItems
    //override val type: Type = Type.TEXT,
    override val alignment: Align = Align.CENTER,
    override val weight: Float = 0f,
    override val backgroundColor: ItemColor = ItemColor.NONE,
    override val props: Props? = null,
    override val name: String = Uuid.random().toString()
) : ListItems