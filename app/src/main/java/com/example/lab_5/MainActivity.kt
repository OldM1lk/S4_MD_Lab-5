package com.example.lab_5

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab_5.data.DrawingAction
import com.example.lab_5.data.ImageAction
import com.example.lab_5.data.colors
import com.example.lab_5.ui.CanvasControls
import com.example.lab_5.ui.screens.DrawingScreen
import com.example.lab_5.ui.screens.DrawingViewModel
import com.example.lab_5.ui.theme.Lab_5Theme
import java.io.InputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab_5Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel = viewModel<DrawingViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    val context = LocalContext.current
                    val pickImageLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent()
                    ) {
                        it?.let {
                            val inputStream: InputStream? =
                                context.contentResolver.openInputStream(it)
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            inputStream?.close()
                            viewModel.onImageAction(
                                ImageAction.OnLoadImage(bitmap.asImageBitmap())
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        DrawingScreen(
                            paths = state.paths,
                            currentPath = state.currentPath,
                            onAction = viewModel::onDrawingAction,
                            backgroundImage = state.backgroundImage,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                        CanvasControls(
                            selectedColor = state.selectedColor,
                            selectedThickness = state.selectedThickness,
                            colors = colors,
                            onSelectColor = {
                                viewModel.onDrawingAction(DrawingAction.OnSelectColor(it))
                            },
                            onSelectThickness = {
                                viewModel.onDrawingAction(DrawingAction.OnSelectThickness(it))
                            },
                            onClearCanvas = {
                                viewModel.onDrawingAction(DrawingAction.OnClearCanvasClick)
                            },
                            onLoadClick = {
                                pickImageLauncher.launch("image/*")
                            },
                            onSaveClick = {
                                val bitmap = viewModel.createBitmapFromCanvas(state, )
                                viewModel.onImageAction(ImageAction.OnSaveImage(bitmap, context))
                            }
                        )
                    }
                }
            }
        }
    }
}