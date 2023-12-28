package com.mahmoudibrahem.arplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.mahmoudibrahem.arplayground.ui.theme.ARPlaygroundTheme
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var arSceneView: ArSceneView
    private lateinit var modelNode: ArModelNode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        setContent {
            ARPlaygroundTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    MainScreenContent(
                        models = viewModel.getModelsList()
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            DeviceResults.isSupportsAR.collectLatest { result ->
                if (result) {
                    viewModel.requestARServiceInstall(this@MainActivity)
                }
            }
        }
    }


    @Composable
    fun MainScreenContent(
        models: List<ModelItem>
    ) {
        var selectedModelIndex by remember { mutableIntStateOf(0) }
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            ARScreen(
                model = models[selectedModelIndex]
            )
            ModelsSection(
                modifier = Modifier.align(Alignment.BottomCenter),
                modelsList = viewModel.getModelsList(),
                selectedModel = selectedModelIndex,
                onModelSelected = {
                    selectedModelIndex = it.id
                },
                onPlaceButtonClicked = {
                    if (modelNode != null) {
                        modelNode.anchor()
                    }
                },
                onUnPlaceButtonClicked = {
                    if (modelNode != null) {
                        modelNode.detachAnchor()
                    }
                }
            )
        }
    }

    @Composable
    fun ARScreen(
        model: ModelItem
    ) {

        ARScene(
            modifier = Modifier.fillMaxSize(),
            planeRenderer = true,
            onCreate = {
                arSceneView = it
                modelNode = ArModelNode(
                    arSceneView.engine,
                    placementMode = PlacementMode.INSTANT
                ).apply {
                    loadModelGlbAsync(
                        glbFileLocation = "models/${model.name}.glb",
                        scaleToUnits = model.scaleFactor
                    )
                }
                arSceneView.addChild(modelNode)
            },
            onSessionCreate = {
                planeRenderer.isVisible = false
            }
        )

        LaunchedEffect(key1 = model) {
            modelNode.loadModelGlbAsync(
                glbFileLocation = "models/${model.name}.glb",
                scaleToUnits = model.scaleFactor
            )
        }
    }

    @Composable
    fun ModelsSection(
        modifier: Modifier = Modifier,
        modelsList: List<ModelItem>,
        onModelSelected: (ModelItem) -> Unit,
        onPlaceButtonClicked: () -> Unit,
        onUnPlaceButtonClicked: () -> Unit,
        selectedModel: Int
    ) {
        var isPlaceButtonVisible by remember { mutableStateOf(true) }
        val placeButtonAlpha = animateFloatAsState(
            targetValue = if (isPlaceButtonVisible) 1f else 0f,
            label = ""
        )
        val unPlaceButtonAlpha = animateFloatAsState(
            targetValue = if (isPlaceButtonVisible) 0f else 1f,
            label = ""
        )
        Column(
            modifier = modifier
                .shadow(elevation = 8.dp)
                .fillMaxWidth()
        ) {
            Box {
                if (isPlaceButtonVisible) {
                    Button(
                        modifier = Modifier
                            .padding(vertical = 16.dp, horizontal = 8.dp)
                            .height(48.dp)
                            .fillMaxWidth()
                            .graphicsLayer {
                                alpha = placeButtonAlpha.value
                            },
                        onClick = {
                            onPlaceButtonClicked()
                            isPlaceButtonVisible = !isPlaceButtonVisible
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xffcc3333),
                        ),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text(
                            text = "Place",
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                } else {
                    Button(
                        modifier = Modifier
                            .padding(vertical = 16.dp, horizontal = 8.dp)
                            .height(48.dp)
                            .fillMaxWidth()
                            .graphicsLayer {
                                alpha = unPlaceButtonAlpha.value
                            },
                        onClick = {
                            onUnPlaceButtonClicked()
                            isPlaceButtonVisible = !isPlaceButtonVisible
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xff2F4858),
                        ),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text(
                            text = "UnPlace",
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }

            }
            LazyRow(
                modifier = modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                itemsIndexed(items = modelsList) { index, model ->
                    val imageSize = animateDpAsState(
                        targetValue = if (index == selectedModel) 75.dp else 50.dp,
                        label = ""
                    )
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(imageSize.value)
                            .aspectRatio(1f)
                            .border(
                                width = if (selectedModel == index) 2.dp else 0.dp,
                                color = Color(0xffcc3333),
                                shape = CircleShape
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onModelSelected(model)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier.size(32.dp),
                            painter = painterResource(id = model.image),
                            contentDescription = stringResource(R.string.model_image),
                            contentScale = ContentScale.FillBounds
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        arSceneView.removeChild(modelNode)
    }

}




