package id.dreamfighter.kmp.json.layout.compose.model.shape

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import id.dreamfighter.kmp.json.layout.compose.model.utils.PointF

class CustomShape(private val startPoint: PointF,private val points:List<PointF>,private val type:String = "pixel") : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(

            Path().apply {
                //val radian = (180 - angle) * Math.PI / 90
                //val xOnOpposite = (size.width * tan(radian)).toFloat()
                if(type == "pixel"){
                    moveTo(startPoint.x, startPoint.y)
                }else if(type == "percentage"){
                    lineTo(x = startPoint.x * size.width, y = startPoint.y * size.height)
                }
                points.forEach {
                    if(type == "pixel"){
                        lineTo(x = it.x, y = it.y)
                    }else if(type == "percentage"){
                        lineTo(x = it.x * size.width, y = it.y * size.height)
                    }
                }
                //lineTo(x = size.width - angle, y = size.height)
                //lineTo(x = size.width, y = 0f)
                //lineTo(x = angle, y = 0f)
                //lineTo(x = xOnOpposite, y = size.height)
            }
        )
    }
}