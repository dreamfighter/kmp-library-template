package id.dreamfighter.kmp.json.layout.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontListFontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.dreamfighter.multiplatform.json_layout_compose.generated.resources.Res
import com.github.dreamfighter.multiplatform.json_layout_compose.generated.resources.logo_ismart
import id.dreamfighter.kmp.json.layout.compose.model.parts.* // Import all parts
import id.dreamfighter.kmp.json.layout.compose.model.shape.CustomShape
import id.dreamfighter.kmp.json.layout.compose.model.shape.Parallelogram
import id.dreamfighter.kmp.json.layout.compose.model.type.Align
import id.dreamfighter.kmp.json.layout.compose.model.type.FontSize
import id.dreamfighter.kmp.json.layout.compose.model.type.Type
import id.dreamfighter.kmp.json.layout.compose.model.utils.PointF
import id.dreamfighter.kmp.json.layout.compose.model.utils.collectBoxProps
import id.dreamfighter.kmp.json.layout.compose.model.utils.collectRowScopeProps
import id.dreamfighter.kmp.json.layout.compose.model.utils.color
import id.dreamfighter.kmp.json.layout.compose.model.utils.createModifier
import id.dreamfighter.kmp.json.layout.compose.model.utils.gradientBackground
import id.dreamfighter.kmp.json.layout.compose.model.utils.toListOrEmpty
import id.dreamfighter.kmp.json.layout.compose.model.utils.toMapOrEmpty
import id.dreamfighter.kmp.json.layout.compose.model.view.AutoScrollingLazyRow
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.concurrent.Volatile

@Composable
fun ConstructPart(
    listItems: ListItems,
    modifier: Modifier = Modifier,
    data: MutableMap<String,Any?> = mutableMapOf(),
    event:(Map<String,Any>) -> Unit = {_ ->}
) {
    when (listItems) { // Use 'when (listItems)' for smart casting
        is Text -> {
            val textPart = listItems
            var partModifier = modifier
            var fontfamily: FontFamily = FontFamily.Default
            var texts by remember { mutableStateOf(listOf<String>()) }
            var hidden by remember { mutableStateOf(false) }
            var setHidden by remember { mutableStateOf(false) }

            var fontWeight = when(textPart.fontWeight){
                "BOLD" -> FontWeight.Bold
                "THIN" -> FontWeight.Thin
                "LIGHT" -> FontWeight.Light
                else -> FontWeight.Normal
            }
            if(data["fonts"]!=null && textPart.fontFamily!=null){
                val fontFamilies = data["fonts"] as Map<*,*>
                fontfamily = fontFamilies[textPart.fontFamily] as FontListFontFamily
            }
            val text = if(data[textPart.name]!=null){
                val datas = data[textPart.name] as Map<*,*>
                if(datas["fontFamily"]!=null){
                    fontfamily = datas["fontFamily"] as FontListFontFamily
                }
                if(datas["fontWeight"]!=null){
                    fontWeight = when(datas["fontWeight"]){
                        "BOLD" -> FontWeight.Bold
                        "THIN" -> FontWeight.Thin
                        "LIGHT" -> FontWeight.Light
                        else -> FontWeight.Normal
                    }
                }
                if(datas["hidden"]!=null) {
                    hidden = datas["hidden"] as Boolean
                    setHidden = true
                }
                if(datas["texts"]!=null && datas["texts"] is MutableList<*>) {
                    texts = (datas["texts"] as MutableList<String>).map { it }
                }
                if(datas["text"]!=null) {
                    datas["text"] as String
                }else{
                    ""
                }
            }else{
                textPart.message
            }
            val textAlign = when (textPart.textAlign) {
                Align.START -> TextAlign.Left
                Align.END -> TextAlign.End
                Align.CENTER -> TextAlign.Center
                else -> null
            }
            val fontSize = when (textPart.textFont) {
                FontSize.TINY.name -> 8.sp
                FontSize.SMALL.name -> 12.sp
                FontSize.BIG.name -> 20.sp
                FontSize.HUGE.name -> 24.sp
                FontSize.DEFAULT.name -> 12.sp
                else -> textPart.textFont.toInt().sp
            }

            var color = Color.Black
            var verticalAnimateScroll = false

            if(textPart.color!=null){
                color = textPart.color.color
            }

            // Refactored Props handling
            val props = textPart.props
            if (props != null) {
                if (props.fillMaxWidth == true) {
                    partModifier = partModifier.fillMaxWidth()
                }
                props.fillMaxHeight?.let {
                    partModifier = if(it) partModifier.fillMaxHeight() else partModifier.fillMaxHeight(it.toString().toFloat())
                }
                props.padding?.let { padding ->
                    padding.start?.let { partModifier = partModifier.padding(start = it.dp) }
                    padding.end?.let { partModifier = partModifier.padding(end = it.dp) }
                    padding.top?.let { partModifier = partModifier.padding(top = it.dp) }
                    padding.bottom?.let { partModifier = partModifier.padding(bottom = it.dp) }
                }
                if (props.hidden == true && !setHidden) {
                    hidden = true
                }
                props.background?.let {
                    partModifier = partModifier.background(it.color)
                }
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
                    }
                }
                if (props.basicMarquee == true) {
                    partModifier = partModifier.basicMarquee(iterations = Int.MAX_VALUE)
                }
                props.animated?.let { animated ->
                    when(animated.type){
                        "scroll" -> {
                            val scrollState = rememberScrollState()
                            var shouldAnimated by remember { mutableStateOf(true) }
                            LaunchedEffect(key1 = shouldAnimated){
                                scrollState.animateScrollTo(
                                    scrollState.maxValue,
                                    animationSpec = tween(20000, 200, easing = CubicBezierEasing(0f,0f,0f,0f))
                                )
                                scrollState.scrollTo(0)
                                shouldAnimated = !shouldAnimated
                            }
                            partModifier = partModifier.horizontalScroll(scrollState, false)
                        }
                        "vertical_scroll" -> {
                            verticalAnimateScroll = true
                        }
                    }
                }
            }


            if(!hidden) {
                if (verticalAnimateScroll && texts.isNotEmpty()) {
                    AutoScrollingLazyRow(list = texts, modifier = partModifier) {
                        Text(
                            maxLines = textPart.maxLines,
                            text = it,
                            color = color,
                            modifier = partModifier,
                            textAlign = textAlign,
                            fontSize = fontSize,
                            fontWeight = fontWeight,
                            fontFamily = fontfamily
                        )
                    }

                } else {
                    Text(
                        maxLines = textPart.maxLines,
                        text = text,
                        color = color,
                        modifier = partModifier,
                        textAlign = textAlign,
                        fontSize = fontSize,
                        fontWeight = fontWeight,
                        fontFamily = fontfamily
                    )
                }
            }
        }

        is AnimatedVisibility -> {
            var visible by remember { mutableStateOf(false) }
            var prevState by remember { mutableStateOf(EnterExitState.PostExit) }
            val animated = listItems
            val items = animated.listItems
            var hidden by remember { mutableStateOf(animated.props?.hidden ?: false) }

            LaunchedEffect(Unit) {
                while(true) {
                    visible = !visible
                    val map = mapOf<String,Any>(
                        "name" to animated.name,
                        "visible" to visible)
                    if(visible) {
                        delay(animated.enterDelay)
                    }else{
                        delay(animated.exitDelay)
                    }
                    event(map)
                }
            }
            var enterAnimation = EnterTransition.None
            var exitAnimation = ExitTransition.None
            if(animated.animationType.isNotEmpty()){
                animated.animationType.forEach {
                    enterAnimation += when(it){
                        "SLIDE_RIGHT" -> slideInHorizontally(animationSpec = tween(durationMillis = 200)) { -it }
                        "SLIDE_LEFT" -> slideInHorizontally(animationSpec = tween(durationMillis = 200)) { +it }
                        "SLIDE_UP" -> slideInVertically(animationSpec = tween(durationMillis = 200)) { +it }
                        "FADE" -> fadeIn(animationSpec = tween(durationMillis = 200))
                        else -> slideInHorizontally(animationSpec = tween(durationMillis = 200)) { -it }
                    }

                    exitAnimation += when(it){
                        "SLIDE_RIGHT" -> slideOutHorizontally(animationSpec = spring(stiffness = Spring.StiffnessHigh)) { -it }
                        "SLIDE_LEFT" -> slideOutHorizontally(animationSpec = spring(stiffness = Spring.StiffnessHigh)) { +it }
                        "SLIDE_UP" -> slideOutVertically(animationSpec = spring(stiffness = Spring.StiffnessHigh)) { +it }
                        "FADE" -> androidx.compose.animation.fadeOut()
                        else -> slideOutHorizontally(animationSpec = spring(stiffness = Spring.StiffnessHigh)) { -it }
                    }
                }
            }

            if(data[animated.name]!=null){
                val animateData = data[animated.name] as Map<*, *>
                if(animateData["hidden"]!=null) {
                    hidden = animateData["hidden"] as Boolean
                }
            }

            if(!hidden) {
                AnimatedVisibility(
                    modifier = modifier,
                    visible = visible,
                    enter = enterAnimation,
                    exit = exitAnimation
                ) {
                    val map = mapOf<String, Any>(
                        "name" to animated.name,
                        "currentState" to transition.currentState.name,
                        "targetState" to transition.targetState.name
                    )

                    if (prevState != transition.targetState) {
                        prevState = transition.targetState
                    }

                    items?.let {
                        for (item in items) {
                            ConstructPart(item, modifier = modifier, data)
                        }
                    }
                }
            }
        }

        is Button -> {
            val text = listItems.message
            Button(onClick = { /*TODO*/ }, modifier = modifier) {
                Text(text)
            }
        }

        is Image -> {
            var contentScale = ContentScale.Fit
            val imagePart = listItems
            val imageAlign = when (imagePart.imageAlign) {
                Align.START -> Alignment.TopStart
                Align.END -> Alignment.BottomEnd
                else -> Alignment.Center
            }
            var partModifier = modifier
            val image: Painter = painterResource(Res.drawable.logo_ismart) // Assuming this is a default

            val props = imagePart.props
            if (props != null) {
                contentScale = when(props.contentScale) {
                    "FillWidth" -> ContentScale.FillWidth
                    else -> ContentScale.Fit
                }
                if(props.fillMaxWidth == true){
                    partModifier = partModifier.fillMaxWidth()
                }
            }

            var hidden = props?.hidden ?: false
            if(data[imagePart.name]!=null){
                val animateData = data[imagePart.name] as Map<*, *>
                if(animateData["hidden"]!=null) {
                    hidden = animateData["hidden"] as Boolean
                }
            }

            if(!hidden) {
                Image(image, "", modifier = partModifier, contentScale = contentScale)
            }
        }

        is Video -> {
            val videoPart = listItems
            val uris = remember { mutableStateListOf<String>() }
            val httpHeaders = remember { mutableStateListOf<Map<String, String>>() }

            var start = 0
            videoPart.url?.let{
                if(uris.isEmpty()){
                    uris.add(it)
                }else{
                    uris[0] = it
                }
                start = 1
                (videoPart.headers?:mapOf()).run {
                    if (httpHeaders.isEmpty()) {
                        httpHeaders.add(this)
                    } else {
                        httpHeaders[0] = this
                    }
                }
            }

            if(data[videoPart.name]!=null){
                val map = data[videoPart.name] as Map<*, *>
                var index = start
                for(url in map["url"].toListOrEmpty<String>()){
                    val mapHeaders = map["headers"].toMapOrEmpty<String,String>()
                    with(mapHeaders){
                        if(index < uris.size){
                            httpHeaders[index] = this
                            uris[index] = url
                        }else{
                            uris.add(url)
                            httpHeaders.add(this)
                        }
                    }
                    index = index.inc()
                }
            }

            var hidden:Boolean by remember { mutableStateOf(videoPart.props?.hidden ?: false) }
            if(data[videoPart.name]!=null){
                val animateData = data[videoPart.name] as Map<*, *>
                if(animateData["hidden"]!=null) {
                    hidden = animateData["hidden"] as Boolean
                }
            }

            if(!hidden && uris.isNotEmpty()) {
                VideoPlayer(uris,httpHeaders){state,track->
                    val map = mapOf(
                        "name" to videoPart.name,
                        "state" to state,
                        "track" to track.toString())
                    event(map)
                }
            }
        }

        is GlideImagePart -> {
            // ... Your GlideImage logic seems platform-specific (e.g., GlideUrl)
            // This part needs to be implemented with an expect/actual
            // For now, leaving it as a placeholder.
            // You'll need to refactor this to use the new `props` object.
            Text("GLIDE_IMAGE not implemented in this refactor", color = Color.Red)
        }

        is Box -> {
            val box = listItems
            val items = box.listItems
            var partModifier = modifier.collectBoxProps(box.props)
            var hidden by remember { mutableStateOf(box.props?.hidden ?: false) }

            LaunchedEffect(data[box.name]){
                if(data[box.name]!=null){
                    val datas = data[box.name] as Map<*,*>
                    datas["props"]?.let { props ->
                        // This cast is unsafe, ideally data[box.name] is also type-safe
                        partModifier = modifier.collectBoxProps(props as Props?)
                    }
                    if(datas["hidden"]!=null) {
                        hidden = datas["hidden"] as Boolean
                    }
                }
            }

            val contentAlignment: Alignment = when (box.contentAlignment) {
                "CENTER" -> Alignment.Center
                "TOP_START" -> Alignment.TopStart
                "TOP_END" -> Alignment.TopEnd
                "BOTTOM_START" -> Alignment.BottomStart
                "BOTTOM_END" -> Alignment.BottomEnd
                else -> Alignment.TopStart
            }

            if(!hidden) {
                Box(
                    modifier = partModifier, contentAlignment = contentAlignment
                ) {
                    items?.let {
                        for (item in items) {
                            var modifierItem = Modifier.padding(0.dp)
                            item.props?.align?.let { align ->
                                modifierItem = modifierItem.align(
                                    alignment = when (align) {
                                        "TOP_START" -> Alignment.TopStart
                                        "TOP_CENTER" -> Alignment.TopCenter
                                        "TOP_END" -> Alignment.TopEnd
                                        "CENTER" -> Alignment.Center
                                        "CENTER_START" -> Alignment.CenterStart
                                        "CENTER_END" -> Alignment.CenterEnd
                                        "BOTTOM_START" -> Alignment.BottomStart
                                        "BOTTOM_CENTER" -> Alignment.BottomCenter
                                        "BOTTOM_END" -> Alignment.BottomEnd
                                        else -> Alignment.TopStart
                                    }
                                )
                            }
                            ConstructPart(item, modifier = modifierItem, data, event)
                        }
                    }
                }
            }
        }

        is Spacer -> {
            Spacer(modifier = modifier.collectBoxProps(listItems.props))
        }

        is ShapePart -> {
            val shape = listItems
            var partModifier = modifier
            val items = shape.listItems
            var hidden by remember { mutableStateOf(shape.props?.hidden ?: false) }

            if(data[shape.name]!=null ){
                val shapeData = data[shape.name] as SnapshotStateMap<*, *>
                if(shapeData["hidden"]!=null) {
                    hidden = shapeData["hidden"] as Boolean
                }
            }

            partModifier = partModifier
                .clip(
                    when (shape.shapeType) {
                        "ROUND" -> {
                            RoundedCornerShape(shape.props?.cornerSize?.dp ?: 10.dp)
                        }
                        "PARALLELOGRAM" -> {
                            Parallelogram(
                                shape.props?.offset?.toFloat() ?: 0f,
                                shape.props?.leftOffset?.toFloat() ?: 0f,
                                shape.props?.rightOffset?.toFloat() ?: 0f
                            )
                        }
                        "CUSTOM" -> {
                            val start = shape.props?.start?.let { PointF(it.x, it.y) } ?: PointF(0f,0f)
                            val points = shape.props?.points?.map { PointF(it.x, it.y) } ?: emptyList()
                            CustomShape(start, points)
                        }
                        else -> RoundedCornerShape(10.dp)
                    }
                )

            partModifier = partModifier.collectBoxProps(shape.props)

            if(!hidden) {
                Box(
                    modifier = partModifier
                ) {
                    items.let {
                        for (item in items) {
                            var modifierItem = Modifier.padding(0.dp)
                            item.props?.align?.let { align ->
                                modifierItem = modifierItem.align(
                                    alignment = when (align) {
                                        "TOP_START" -> Alignment.TopStart
                                        "TOP_CENTER" -> Alignment.TopCenter
                                        "TOP_END" -> Alignment.TopEnd
                                        "CENTER" -> Alignment.Center
                                        "CENTER_START" -> Alignment.CenterStart
                                        "CENTER_END" -> Alignment.CenterEnd
                                        "BOTTOM_START" -> Alignment.BottomStart
                                        "BOTTOM_CENTER" -> Alignment.BottomCenter
                                        "BOTTOM_END" -> Alignment.BottomEnd
                                        else -> Alignment.TopStart
                                    }
                                )
                            }
                            ConstructPart(item, modifier = modifierItem, data, event)
                        }
                    }
                }
            }
        }

        is Column -> {
            val column = listItems
            var partModifier = modifier
            val items = column.listItems
            val horizontalAlignment = when(column.horizontalAlignment){
                "START" -> Alignment.Start
                "END" -> Alignment.End
                else -> Alignment.CenterHorizontally
            }

            column.props?.let { props ->
                props.height?.let { partModifier = partModifier.height(it.dp) }
                props.background?.let { partModifier = partModifier.background(it.color) }
                props.gradientBackground?.let {
                    val angle = it.angle ?: 0.0
                    val listColors = it.colors?.map { c -> c.color } ?: emptyList()
                    partModifier = partModifier.gradientBackground(listColors, angle = angle.toFloat())
                }
                if (props.fillMaxWidth == true) {
                    partModifier = partModifier.fillMaxWidth()
                }
            }

            Column(
                horizontalAlignment = horizontalAlignment,
                modifier = partModifier
            ) {
                for (item in items) {
                    var modifierItem = createModifier(listItems = item) // This function is in ModifierUtil
                    ConstructPart(item, modifier = modifierItem,data,event)
                }
            }
        }

        is Row -> {
            val row = listItems
            val items = row.listItems
            var partModifier = modifier

            row.props?.let { props ->
                props.height?.let { partModifier = partModifier.height(it.dp) }
                props.background?.let { partModifier = partModifier.background(it.color) }
                props.gradientBackground?.let {
                    val angle = it.angle ?: 0.0
                    val listColors = it.colors?.map { c -> c.color } ?: emptyList()
                    partModifier = partModifier.gradientBackground(listColors, angle = angle.toFloat())
                }
                if (props.fillMaxWidth == true) {
                    partModifier = partModifier.fillMaxWidth()
                }
                if (props.intrinsicSizeMax == true) {
                    partModifier = partModifier.height(IntrinsicSize.Max)
                }
                props.padding?.let { padding ->
                    padding.start?.let { partModifier = partModifier.padding(start = it.dp) }
                    padding.end?.let { partModifier = partModifier.padding(end = it.dp) }
                    padding.top?.let { partModifier = partModifier.padding(top = it.dp) }
                    padding.bottom?.let { partModifier = partModifier.padding(bottom = it.dp) }
                }
            }

            val verticalAlignment = when(row.verticalAlignment){
                "TOP" -> Alignment.Top
                "BOTTOM" -> Alignment.Bottom
                else -> Alignment.CenterVertically
            }

            Row(
                verticalAlignment = verticalAlignment,
                modifier = partModifier
            ) {
                for (item in items) {
                    var modifierItem = createModifier(item) // This function is in ModifierUtil
                    ConstructPart(item, modifierItem, data, event)
                }
            }
        }

        is CardPart -> {
            val card = listItems
            val items = card.listItems
            var partModifier = modifier
            var color = card.cardBackgroundColor?.color ?: Color.White
            var elevation = (card.elevation ?: 10).dp

            card.props?.let { props ->
                props.height?.let { partModifier = partModifier.height(it.dp) }
                props.background?.let { partModifier = partModifier.background(it.color) }
                if (props.fillMaxWidth == true) {
                    partModifier = partModifier.fillMaxWidth()
                }
            }

            var hidden by remember { mutableStateOf(card.props?.hidden ?: false) }

            if(data[card.name]!=null){
                val dt = data[card.name] as Map<*, *>
                if(dt["hidden"]!=null) {
                    hidden = dt["hidden"] as Boolean
                }
            }

            if(!hidden) {
                Card(
                    modifier = partModifier,
                    elevation = CardDefaults.cardElevation(elevation),
                    colors = CardDefaults.cardColors(containerColor = color)
                ) {
                    for (item in items) {
                        var modifierItem = Modifier.background("#00FFFFFF".color)
                        item.props?.background?.let {
                            modifierItem = modifierItem.background(it.color)
                        }
                        ConstructPart(item, modifierItem, data)
                    }
                }
            }
        }

        is Web -> {
            // Implement your Composable for 'Web' here
        }
    }
}

@Composable
expect fun VideoPlayer(uris: List<String>, headers:List<Map<String,String>>, listener: (Int,String?) -> Unit = { _, _ ->})