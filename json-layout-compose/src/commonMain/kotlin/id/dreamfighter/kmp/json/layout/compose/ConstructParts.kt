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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
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
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.github.dreamfighter.multiplatform.json_layout_compose.generated.resources.Res
import com.github.dreamfighter.multiplatform.json_layout_compose.generated.resources.logo_ismart
import id.dreamfighter.kmp.json.layout.compose.model.parts.* // Import all parts
import id.dreamfighter.kmp.json.layout.compose.model.shape.CustomShape
import id.dreamfighter.kmp.json.layout.compose.model.shape.Parallelogram
import id.dreamfighter.kmp.json.layout.compose.model.type.Align
import id.dreamfighter.kmp.json.layout.compose.model.type.FontSize
import id.dreamfighter.kmp.json.layout.compose.model.utils.PointF
import id.dreamfighter.kmp.json.layout.compose.model.utils.collectBoxProps // Import the refactored function
import id.dreamfighter.kmp.json.layout.compose.model.utils.color
import id.dreamfighter.kmp.json.layout.compose.model.utils.createModifier
import id.dreamfighter.kmp.json.layout.compose.model.utils.toListOrEmpty
import id.dreamfighter.kmp.json.layout.compose.model.utils.toMapOrEmpty
import id.dreamfighter.kmp.json.layout.compose.model.view.AutoScrollingLazyRow
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

@Composable
fun ConstructPart(
    listItems: ListItems,
    modifier: Modifier = Modifier,
    data: MutableMap<String,Any?> = mutableMapOf(),
    event:(Map<String,Any>) -> Unit = {_ ->}
) {
    // Use smart casting on the sealed interface
    when (listItems) {
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

            // --- Data override logic (seems fine) ---
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

            var color = textPart.color.color
            var verticalAnimateScroll = false

            // --- REFACTORED PROPS ---
            // Apply all common box props in a logical order
            partModifier = partModifier.collectBoxProps(textPart.props)

            // Handle props specific to Text
            textPart.props?.let { props ->
                if (props.hidden == true && !setHidden) {
                    hidden = true
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
            // --- END REFACTORED PROPS ---

            if(!hidden) {
                if (verticalAnimateScroll && texts.isNotEmpty()) {
                    AutoScrollingLazyRow(list = texts, modifier = partModifier) {
                        Text(
                            maxLines = textPart.maxLines,
                            text = it,
                            color = color,
                            modifier = Modifier, // Modifier is applied to the LazyRow
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
                            ConstructPart(item, modifier = modifier, data, event)
                        }
                    }
                }
            }
        }

        is Button -> {
            val text = listItems.message
            val partModifier = modifier.collectBoxProps(listItems.props)
            Button(onClick = { /*TODO*/ }, modifier = partModifier) {
                Text(text)
            }
        }

        is Image -> {
            val imagePart = listItems
            val imageAlign = when (imagePart.imageAlign) {
                Align.START -> Alignment.TopStart
                Align.END -> Alignment.BottomEnd
                else -> Alignment.Center
            }
            var partModifier = modifier
            val image: Painter = painterResource(Res.drawable.logo_ismart) // Assuming default

            // --- REFACTORED PROPS ---
            partModifier = partModifier.collectBoxProps(imagePart.props)
            val contentScale = when(imagePart.props?.contentScale) {
                "FillWidth" -> ContentScale.FillWidth
                "FillHeight" -> ContentScale.FillHeight
                "Fit" -> ContentScale.Fit
                "Inside" -> ContentScale.Inside
                else -> ContentScale.Fit
            }
            // --- END REFACTORED PROPS ---

            var hidden = imagePart.props?.hidden ?: false
            if(data[imagePart.name]!=null){
                val animateData = data[imagePart.name] as Map<*, *>
                if(animateData["hidden"]!=null) {
                    hidden = animateData["hidden"] as Boolean
                }
            }

            if(!hidden) {
                Image(image, "", modifier = partModifier, contentScale = contentScale, alignment = imageAlign)
            }
        }

        is Video -> {
            val videoPart = listItems
            val uris = remember { mutableStateListOf<String>() }
            val httpHeaders = remember { mutableStateListOf<Map<String, String>>() }

            // --- REFACTORED PROPS ---
            val partModifier = modifier.collectBoxProps(videoPart.props)
            // --- END REFACTORED PROPS ---

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
            //val updatedListener by rememberUpdatedState(listener)
            if(data[videoPart.name]!=null){
                val animateData = data[videoPart.name] as Map<*, *>
                if(animateData["hidden"]!=null) {
                    hidden = animateData["hidden"] as Boolean
                }
            }

            if(!hidden && uris.isNotEmpty()) {
                VideoPlayer(partModifier, uris,httpHeaders){state,track->
                    val map = mapOf(
                        "name" to videoPart.name,
                        "state" to state,
                        "track" to track.toString())
                    event(map)
                }
            }
        }

        is GlideImagePart -> {
            val imagePart = listItems
            var partModifier = modifier
            var imageUrl by remember { mutableStateOf(imagePart.glideUrl ?: "") }
            val headersHttp = NetworkHeaders.Builder()
            val context = LocalPlatformContext.current
            val imageRequest = ImageRequest.Builder(context)

            // --- REFACTORED PROPS ---
            partModifier = partModifier.collectBoxProps(imagePart.props)

            val contentScale = when(imagePart.props?.contentScale){
                "FillWidth" -> ContentScale.FillWidth
                "FillHeight" -> ContentScale.FillHeight
                "Fit" -> ContentScale.Fit
                "Inside" -> ContentScale.Inside
                else -> ContentScale.Fit
            }

            imagePart.props?.url?.let { imageUrl = it }
            imagePart.props?.headers?.let {
                it.forEach { (key, value) ->
                    headersHttp.add(key,value)
                    //headersHttp.add(key,value)
                }
                //headersHttp.putAll(it)
            }

            if(imagePart.props?.swing == true){
                // ... (your swing logic seems fine)
                val angleOffset = 10f
                // ...
                partModifier = partModifier.graphicsLayer(
                    transformOrigin = TransformOrigin(
                        pivotFractionX = 0.5f,
                        pivotFractionY = 0f,
                    ),
                    // rotationZ = angle, // Uncomment this when you add the animateFloat
                )
            }
            // --- END REFACTORED PROPS ---

            var hidden by remember { mutableStateOf(imagePart.props?.hidden ?: false) }
            if(data[imagePart.name]!=null ){
                val animateData = data[imagePart.name] as Map<*, *> // This cast is still risky
                animateData["url"]?.let { imageUrl = it.toString() }

                (animateData["headers"] as? Map<String, String>)?.let {
                    it.forEach { (key, value) ->
                        headersHttp.add(key,value)
                        //headersHttp.add(key,value)
                    }
                }

                if(animateData["hidden"]!=null) {
                    hidden = animateData["hidden"] as Boolean
                }
            }

            if(!hidden) {
                // ... Your GlideImage logic
                // This is platform-specific and needs expect/actual
                if (imagePart.glideUrl != null && data[imagePart.name] != null) {
                    //val builder = LazyHeaders.Builder()

                    val values = data[imagePart.name] as Map<String, Any?>
                    imageUrl = imagePart.glideUrl
                    if (values["headers"] != null) {
                        val headers = values["headers"] as Map<String, String>
                        headers.forEach { (key, value) ->
                            headersHttp.add(key,value)
                            //builder.addHeader(key, value)
                        }
                    }
                } else {

                    //val context = LocalContext.current
                    //Log.d("GLIDE_URL",imageUrl)
                    //Image(image, "", modifier = partModifier, contentScale = contentScale)
                }

                // Build the request

                println(imageUrl)

                if(imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = imageRequest.data(imageUrl) // The URL or resource to load
                            .diskCacheKey(imageUrl)
                            .memoryCacheKey(imageUrl)
                            .crossfade(true) // Enable a fade-in animation
                            .httpHeaders(headersHttp.build())
                            .error {
                                null
                            }
                            .build(),
                        contentScale = contentScale,
                        contentDescription = "User profile picture",
                        modifier = partModifier,
                        onState = { state ->
                            when (state) {
                                is AsyncImagePainter.State.Error -> {
                                    // This will print the *exact* error to your log
                                    println("Coil Error: ${state.result}")
                                }

                                else -> {}
                            }
                        }
                    )
                }
            }
        }

        is Box -> {
            val box = listItems
            val items = box.listItems
            var partModifier = modifier.collectBoxProps(box.props)
            var hidden by remember { mutableStateOf(box.props?.hidden ?: false) }

            // Data override logic
            LaunchedEffect(data[box.name]){
                if(data[box.name]!=null){
                    val datas = data[box.name] as Map<*,*>
                    // This is tricky. You're overriding Props with a Map.
                    // You would need to re-parse this map into a Props object.
                    // (datas["props"] as? Map<String, Any>)?.let { propsMap ->
                    // }
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
                            var modifierItem = Modifier
                            // Apply alignment from props
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
                                ) as Modifier.Companion
                            }
                            ConstructPart(item, modifier = modifierItem, data, event)
                        }
                    }
                }
            }
        }

        is Spacer -> {
            // Apply box props to Spacer
            val partModifier = modifier.collectBoxProps(listItems.props)
            Spacer(modifier = partModifier)
        }

        is ShapePart -> {
            val shape = listItems
            var partModifier = modifier
            val items = shape.listItems
            var hidden by remember { mutableStateOf(shape.props?.hidden ?: false) }

            if(data[shape.name]!=null ){
                val shapeData = data[shape.name] as Map<*, *> // Risky cast
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
                            val type = shape.props?.pointsType?:"pixel"
                            val start = shape.props?.start?.let { PointF(it.x, it.y) } ?: PointF(0f,0f)
                            val points = shape.props?.points?.map { PointF(it.x, it.y) } ?: emptyList()
                            CustomShape(start, points)
                        }
                        else -> RoundedCornerShape(10.dp)
                    }
                )

            // Apply all other box props (background, size, padding)
            partModifier = partModifier.collectBoxProps(shape.props)

            if(!hidden) {
                Box(
                    modifier = partModifier
                ) {
                    items.let {
                        for (item in items) {
                            var modifierItem = Modifier
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
                                ) as Modifier.Companion
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

            // Apply props from the Props object
            partModifier = partModifier.collectBoxProps(column.props)

            Column(
                horizontalAlignment = horizontalAlignment,
                modifier = partModifier
            ) {
                for (item in items) {
                    // createModifier applies column-specific props like weight
                    var modifierItem = createModifier(listItems = item)
                    ConstructPart(item, modifier = modifierItem,data,event)
                }
            }
        }

        is Row -> {
            val row = listItems
            val items = row.listItems
            var partModifier = modifier

            // Apply props from the Props object
            partModifier = partModifier.collectBoxProps(row.props)

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
                    // createModifier applies row-specific props like weight
                    var modifierItem = createModifier(item)
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

            // Apply common props
            partModifier = partModifier.collectBoxProps(card.props)

            var hidden by remember { mutableStateOf(card.props?.hidden ?: false) }

            if(data[card.name]!=null){
                val dt = data[card.name] as Map<*, *> // Risky cast
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
                        var modifierItem = Modifier
                        item.props?.background?.let {
                            modifierItem = modifierItem.background(it.color) as Modifier.Companion
                        }
                        ConstructPart(item, modifierItem, data)
                    }
                }
            }
        }

        is Web -> {
            Text("WEB component not implemented", color = Color.Red)
        }
    }
}

@Composable
expect fun VideoPlayer(
    modifier: Modifier, // <-- ADDED MODIFIER
    uris: List<String>,
    headers:List<Map<String,String>>,
    listener: (Int,String?) -> Unit = { _, _ ->}
)