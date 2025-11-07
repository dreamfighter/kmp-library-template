package id.dreamfighter.kmp.json.layout.compose.model.shape

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class Parallelogram(private val offset: Float=0f,private val leftOffset: Float=0f,private val rightOffset: Float=0f) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(

            Path().apply {
                //val radian = (180 - angle) * Math.PI / 90
                //val xOnOpposite = (size.width * tan(radian)).toFloat()
                moveTo(0f, size.height)
                lineTo(x = size.width - offset + rightOffset, y = size.height)
                lineTo(x = size.width, y = 0f)
                lineTo(x = offset + leftOffset, y = 0f)
                //lineTo(x = xOnOpposite, y = size.height)
            }
        )
    }
}