package id.dreamfighter.kmp.json.layout.compose.model.utils

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import id.dreamfighter.kmp.json.layout.compose.model.parts.ListItems
import id.dreamfighter.kmp.json.layout.compose.model.parts.Props
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

    modifier = commonModifier(modifier, listItems)
    modifier = collectRowScopeProps(modifier = modifier, props = listItems.props)

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

    props.weight?.let {
        modifierItem = modifierItem.weight(it)
    }
    props.fillWeight?.let {
        modifierItem = modifierItem.weight(it, fill = true)
    }
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
    props.background?.let {
        modifierItem = modifierItem.background(it.color)
    }

    return modifierItem
}

fun Modifier.collectBoxProps(
    props: Props?
): Modifier {
    if (props == null) return this

    var partModifier = this

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

    if (props.animateContentSize == true) {
        partModifier = partModifier.animateContentSize()
    }
    props.height?.let {
        partModifier = partModifier.height(it.dp)
    }
    if (props.fillMaxWidth == true) {
        partModifier = partModifier.fillMaxWidth()
    }
    if (props.fillMaxHeight == true) {
        partModifier = partModifier.fillMaxHeight()
    }
    props.background?.let {
        partModifier = partModifier.background(it.color)
    }
    props.gradientBackground?.let {
        val angle = it.angle ?: 0.0
        val listColors = it.colors?.map { c -> c.color } ?: listOf(Color.Transparent, Color.Transparent)
        partModifier = partModifier.gradientBackground(listColors, angle = angle.toFloat())
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

    modifier = commonModifier(modifier, listItems)
    modifier = collectColumnScopeProps(modifier, listItems.props)

    if (listItems.alignment == null) {
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
    props.align?.let {
        modifierItem = modifierItem.align(alignment = when(it) {
            "START" -> Alignment.Start
            "END" -> Alignment.End
            else -> Alignment.Start
        })
    }
    props.background?.let {
        modifierItem = modifierItem.background(it.color)
    }
    props.weight?.let {
        modifierItem = modifierItem.weight(it)
    }
    props.fillWeight?.let {
        modifierItem = modifierItem.weight(it, fill = true)
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
inline fun <reified T: Any,reified R: Any> Any?.toMapOrEmpty(): Map<T,R> = this as? Map<T,R>?: mapOf()
inline fun <reified T> Any?.toListOrEmpty(): List<T> = this as? List<T> ?: listOf()