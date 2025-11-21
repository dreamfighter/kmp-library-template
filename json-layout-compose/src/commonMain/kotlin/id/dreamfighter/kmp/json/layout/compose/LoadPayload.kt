package id.dreamfighter.android.compose.tojson

import id.dreamfighter.kmp.json.layout.compose.Payload
import id.dreamfighter.kmp.json.layout.compose.model.parts.* // Import all parts
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

// 1. This module now registers all your serializable data classes
val componentSerializersModule = SerializersModule {
    polymorphic(ListItems::class) {
        subclass(AnimatedVisibility::class)
        subclass(Box::class)
        //subclass(Button::class)
        subclass(CardPart::class)
        subclass(Column::class)
        subclass(GlideImagePart::class)
        subclass(Image::class)
        subclass(Row::class)
        subclass(ShapePart::class)
        subclass(Spacer::class)
        subclass(Text::class)
        subclass(Video::class)
        subclass(Web::class)
        subclass(Audio::class) // <-- Add this line
    }
}

// 2. Your AppJson configuration is correct.
val AppJson = Json {
    // This tells the parser to use the "type" field to pick the class
    //classDiscriminator = "type"

    serializersModule = componentSerializersModule
    ignoreUnknownKeys = true
    isLenient = true
}

fun loadPayload(jsonPayload: String): Payload {
    // This will now work
    return AppJson.decodeFromString<Payload>(
        jsonPayload
    )
}