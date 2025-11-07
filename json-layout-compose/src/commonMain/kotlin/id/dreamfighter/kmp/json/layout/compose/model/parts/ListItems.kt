package id.dreamfighter.kmp.json.layout.compose.model.parts

import id.dreamfighter.kmp.json.layout.compose.model.type.Align
import id.dreamfighter.kmp.json.layout.compose.model.type.ItemColor
import id.dreamfighter.kmp.json.layout.compose.model.type.Type
import kotlinx.serialization.Serializable

/**
 * Changed to a sealed interface. This is the new base for all components.
 */
@Serializable
sealed interface ListItems {
    // This is the "discriminator" property that kotlinx.serialization
    // will use to identify which subclass to use.
    //val type: Type

    // These are the common properties all components must have.
    val alignment: Align
    val weight: Float
    val backgroundColor: ItemColor
    val props: Props? // <-- Fixed: Now uses the type-safe Props class
    val name: String
}