package id.dreamfighter.kmp.json.layout.compose.model.utils

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import id.dreamfighter.kmp.json.layout.compose.model.parts.ListItems
import id.dreamfighter.kmp.json.layout.compose.model.parts.Props
import id.dreamfighter.kmp.json.layout.compose.model.shape.Parallelogram
import id.dreamfighter.kmp.json.layout.compose.model.type.Align
import id.dreamfighter.kmp.json.layout.compose.model.type.ItemColor
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun RowScope.createModifier(
    listItems: ListItems
): Modifier {
    var modifier = if (listItems.weight > 0) {
        Modifier.weight(listItems.weight)
    } else Modifier

    // Apply common props first (like weight from Props)
    modifier = collectRowScopeProps(modifier, listItems.props)

    // Then apply common background
    modifier = commonModifier(modifier, listItems)

    // Then apply alignment
    return when (listItems.alignment) {
        Align.START -> modifier.align(Alignment.Top)
        Align.END -> modifier.align(Alignment.Bottom)
        Align.CENTER -> modifier.align(Alignment.CenterVertically)
        Align.FILL -> modifier.fillMaxHeight()
        else -> modifier
    }
}

@Composable
fun RowScope.collectRowScopeProps(
    modifier: Modifier,
    props: Props?
): Modifier {
    if (props == null) return modifier

    var modifierItem = modifier

    // --- LOGICAL ORDER ---
    // 1. Size
    props.weight?.let {
        modifierItem = modifierItem.weight(it)
    }
    props.fillWeight?.let {
        modifierItem = modifierItem.weight(it, fill = true)
    }

    // 2. Background
    props.background?.let {
        modifierItem = modifierItem.background(it.color)
    }

    // 3. Padding
    props.padding?.let { padding ->
        padding.start?.let {
            modifierItem = modifierItem.padding(start = it.dp)
        }
        padding.end?.let {
            modifierItem = modifierItem.padding(end = it.dp)
        }
        padding.top?.let {
            modifierItem = modifierItem.padding(top = it.dp)
        }
        padding.bottom?.let {
            modifierItem = modifierItem.padding(bottom = it.dp)
        }
    }

    return modifierItem
}

/**
 * This is the primary function for applying props in the correct order.
 * Order: Size > Animation > Background > Clip > Padding > Other
 */
fun Modifier.collectBoxProps(
    props: Props?
): Modifier {
    if (props == null) return this

    var partModifier = this

    // --- 1. SIZE ---
    if (props.fillMaxSize == true) {
        partModifier = partModifier.fillMaxSize()
    } else {
        if (props.fillMaxWidth == true) {
            partModifier = partModifier.fillMaxWidth()
        } else {
            props.width?.let { partModifier = partModifier.width(it.dp) }
        }

        if (props.fillMaxHeight == true) {
            partModifier = partModifier.fillMaxHeight()
        } else {
            props.height?.let { partModifier = partModifier.height(it.dp) }
        }
    }

    if (props.intrinsicSizeMax == true) {
        partModifier = partModifier.height(IntrinsicSize.Max)
    }

    // --- 2. ANIMATION ---
    if (props.animateContentSize == true) {
        partModifier = partModifier.animateContentSize()
    }

    // --- 4. CLIP ---
    props.clip?.let { clip ->
        when(clip.type){
            "ROUND" -> {
                partModifier = partModifier.clip(RoundedCornerShape(
                    topEnd = (clip.topEnd ?: 0.0).dp,
                    topStart = (clip.topStart ?: 0.0).dp,
                    bottomStart = (clip.bottomStart ?: 0.0).dp,
                    bottomEnd = (clip.bottomEnd ?: 0.0).dp
                ))
            }
            "PARALLELOGRAM" -> {
                partModifier = partModifier.clip(Parallelogram(
                    (clip.offset ?: 0.0).toFloat(),
                    (clip.leftOffset ?: 0.0).toFloat(),
                    (clip.rightOffset ?: 0.0).toFloat()
                ))
            }
            // Add CUSTOM clip type logic if needed
        }
    }

    // --- 3. BACKGROUND ---
    props.background?.let {
        partModifier = partModifier.background(it.color)
    }
    props.gradientBackground?.let {
        val angle = it.angle ?: 0.0
        val listColors = it.colors?.map { c -> c.color } ?: listOf(Color.Transparent, Color.Transparent)
        partModifier = partModifier.gradientBackground(listColors, angle = angle.toFloat())
    }

    // --- 5. PADDING (Applied AFTER background/clip) ---
    props.padding?.let { padding ->
        padding.start?.let {
            partModifier = partModifier.padding(start = it.dp)
        }
        padding.end?.let {
            partModifier = partModifier.padding(end = it.dp)
        }
        padding.top?.let {
            partModifier = partModifier.padding(top = it.dp)
        }
        padding.bottom?.let {
            partModifier = partModifier.padding(bottom = it.dp)
        }
    }

    // --- 6. OTHER MODIFIERS ---
    if (props.basicMarquee == true) {
        partModifier = partModifier.basicMarquee(iterations = Int.MAX_VALUE)
    }

    return partModifier
}

@Composable
fun ColumnScope.createModifier(
    listItems: ListItems
): Modifier {
    var modifier = if (listItems.weight > 0) {
        Modifier.weight(listItems.weight)
    } else Modifier

    // Apply common props first
    modifier = collectColumnScopeProps(modifier, listItems.props)

    // Then apply common background
    modifier = commonModifier(modifier, listItems)

    // Then apply alignment
    if(listItems.alignment == null){
        return modifier
    }
    return when (listItems.alignment) {
        Align.START -> modifier.align(Alignment.Start)
        Align.END -> modifier.align(Alignment.End)
        Align.CENTER -> modifier.align(Alignment.CenterHorizontally)
        Align.FILL -> modifier.fillMaxWidth()
        else -> modifier
    }
}

@Composable
private fun ColumnScope.collectColumnScopeProps(
    modifier: Modifier,
    props: Props?
): Modifier {
    if (props == null) return modifier

    var modifierItem = modifier

    // --- LOGICAL ORDER ---
    // 1. Size
    props.weight?.let {
        modifierItem = modifierItem.weight(it)
    }
    props.fillWeight?.let {
        modifierItem = modifierItem.weight(it, fill = true)
    }

    // 2. Alignment
    props.align?.let {
        modifierItem = modifierItem.align(alignment = when(it) {
            "START" -> Alignment.Start
            "END" -> Alignment.End
            "CENTER" -> Alignment.CenterHorizontally
            else -> Alignment.Start
        })
    }

    // 3. Background
    props.background?.let {
        modifierItem = modifierItem.background(it.color)
    }

    return modifierItem
}


@Composable
private fun commonModifier(
    modifier: Modifier,
    listItems: ListItems
): Modifier {
    return when (listItems.backgroundColor) {
        ItemColor.RED -> modifier.background(Color.Red)
        ItemColor.GREEN -> modifier.background(Color.Green)
        ItemColor.BLUE -> modifier.background(Color.Blue)
        else -> modifier
    }
}

fun String.toColorInt(): Int {
    if (this.isEmpty()) return 0
    if (this[0] == '#') {
        var color = substring(1).toLong(16)
        if (length == 7) {
            color = color or -16777216L // 0xFF000000
        } else if (length != 9) {
            throw IllegalArgumentException("Unknown color")
        }
        return color.toInt()
    }
    throw IllegalArgumentException("Unknown color")
}

val String.color
    get() = Color(this.toColorInt())

fun Modifier.gradientBackground(colors: List<Color>, angle: Float) = this.then(
    Modifier.drawBehind {
        val angleRad = angle / 180f * PI
        val x = cos(angleRad).toFloat() //Fractional x
        val y = sin(angleRad).toFloat() //Fractional y

        val radius = sqrt(size.width.pow(2) + size.height.pow(2)) / 2f
        val offset = center + Offset(x * radius, y * radius)

        val exactOffset = Offset(
            x = min(offset.x.coerceAtLeast(0f), size.width),
            y = size.height - min(offset.y.coerceAtLeast(0f), size.height)
        )

        drawRect(
            brush = Brush.linearGradient(
                colors = colors,
                start = Offset(size.width, size.height) - exactOffset,
                end = exactOffset
            ),
            size = size
        )
    }
)

inline fun <reified T> Any?.asOrFail(): T = this as T
//inline fun <reified T,reified T> Any.toMapOrFail(): T = this as T
inline fun <reified T: Any,reified R: Any> Any?.toMapOrEmpty(): Map<T,R> = this as? Map<T,R>?: mapOf()
inline fun <reified T> Any?.toListOrEmpty(): List<T> = this as? List<T> ?: listOf()