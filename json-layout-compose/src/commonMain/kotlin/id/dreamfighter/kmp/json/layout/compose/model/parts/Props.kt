package id.dreamfighter.kmp.json.layout.compose.model.parts

import kotlinx.serialization.Serializable

/**
 * This class REPLACES Map<String, Any>
 * It holds all possible properties you might find in a "props" block.
 */
@Serializable
data class Props(
    // Common layout
    val fillMaxWidth: Boolean? = null,
    val fillMaxHeight: Boolean? = null,
    val fillMaxSize: Boolean? = null,
    val height: Double? = null,
    val width: Double? = null,
    val padding: Padding? = null,
    val fillWeight: Float? = null,
    val weight: Float? = null,
    val align: String? = null, // For Box children
    val hidden: Boolean? = null,
    val animateContentSize: Boolean? = null,

    // Style
    val background: String? = null,
    val gradientBackground: GradientBackground? = null,
    val contentScale: String? = null,
    val clip: Clip? = null,
    val shapeType: String? = null, // For ShapePart
    val cornerSize: Double? = null, // For ShapePart
    val offset: Double? = null, // For ShapePart
    val rightOffset: Double? = null, // For ShapePart
    val leftOffset: Double? = null, // For ShapePart
    val start: SerializablePointF? = null, // For ShapePart
    val points: List<SerializablePointF>? = null, // For ShapePart

    // Component-specific
    val basicMarquee: Boolean? = null, // For Text
    val animated: AnimatedProps? = null, // For Text
    val intrinsicSizeMax: Boolean? = null, // For Row
    val url: String? = null, // For GlideImage/Video
    val headers: Map<String, String>? = null, // For GlideImage/Video
    val swing: Boolean? = null // For GlideImage
)

@Serializable
data class Padding(
    val start: Double? = null,
    val end: Double? = null,
    val top: Double? = null,
    val bottom: Double? = null
)

@Serializable
data class Clip(
    val type: String? = null,
    val topEnd: Double? = null,
    val topStart: Double? = null,
    val bottomStart: Double? = null,
    val bottomEnd: Double? = null,
    val offset: Double? = null,
    val rightOffset: Double? = null,
    val leftOffset: Double? = null
)

@Serializable
data class AnimatedProps(
    val type: String? = null
)

@Serializable
data class GradientBackground(
    val angle: Double? = null,
    val colors: List<String>? = null
)

@Serializable
data class SerializablePointF(
    val x: Float,
    val y: Float
)