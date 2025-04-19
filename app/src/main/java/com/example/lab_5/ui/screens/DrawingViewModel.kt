package com.example.lab_5.ui.screens

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab_5.data.DrawingAction
import com.example.lab_5.data.DrawingState
import com.example.lab_5.data.ImageAction
import com.example.lab_5.data.PathData
import com.example.lab_5.data.colors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DrawingViewModel : ViewModel() {
    private val _state = MutableStateFlow(DrawingState())
    val state = _state.asStateFlow()

    fun onDrawingAction(action: DrawingAction) {
        when (action) {
            DrawingAction.OnClearCanvasClick -> onClearCanvasClick()
            is DrawingAction.OnDraw -> onDraw(action.offset)
            DrawingAction.OnNewPathStart -> onNewPathStart()
            DrawingAction.OnPathEnd -> onPathEnd()
            is DrawingAction.OnSelectColor -> onSelectColor(action.color)
            is DrawingAction.OnSelectThickness -> onSelectThickness(action.thickness)
        }
    }

    fun onImageAction(action: ImageAction) {
        when (action) {
            is ImageAction.OnLoadImage -> onLoadImage(action.image)
            is ImageAction.OnSaveImage -> onSaveImage(action.bitmap, action.context)
        }
    }

    private fun onSelectThickness(thickness: Float) {
        _state.update {
            it.copy(
                selectedThickness = thickness
            )
        }
    }

    private fun onSelectColor(color: Color) {
        _state.update {
            it.copy(
                selectedColor = color
            )
        }
    }

    private fun onPathEnd() {
        val currentPathData = state.value.currentPath ?: return
        _state.update {
            it.copy(
                currentPath = null,
                paths = it.paths + currentPathData
            )
        }
    }

    private fun onNewPathStart() {
        _state.update {
            it.copy(
                currentPath = PathData(
                    id = System.currentTimeMillis(),
                    color = it.selectedColor,
                    path = emptyList(),
                    thickness = it.selectedThickness
                )
            )
        }
    }

    private fun onDraw(offset: Offset) {
        val currentPathData = state.value.currentPath ?: return
        _state.update {
            it.copy(
                currentPath = currentPathData.copy(
                    path = currentPathData.path + offset
                )
            )
        }
    }

    private fun onClearCanvasClick() {
        _state.update {
            it.copy(
                currentPath = null,
                paths = emptyList()
            )
        }
    }

    private fun onSaveImage(bitmap: Bitmap, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val filename = "drawing_${System.currentTimeMillis()}.png"
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
            val imageUri: Uri? =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            imageUri?.let { uri ->
                resolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.close()
                }
            }
        }
        Toast
            .makeText(
                context, "Изображение сохранено в галерею",
                Toast.LENGTH_SHORT
            )
            .show()
    }

    private fun onLoadImage(image: ImageBitmap) {
        _state.update {
            it.copy(
                backgroundImage = image
            )
        }
    }

    fun createBitmapFromCanvas(state: DrawingState): Bitmap {
        val bitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)

        state.backgroundImage?.let {
            val bg = it.asAndroidBitmap()
            canvas.drawBitmap(bg, 0f, 0f, null)
        } ?: run {
            canvas.drawColor(Color.White.toArgb())
        }

        val paint = android.graphics.Paint().apply {
            isAntiAlias = true
            style = android.graphics.Paint.Style.STROKE
            strokeCap = android.graphics.Paint.Cap.ROUND
            strokeJoin = android.graphics.Paint.Join.ROUND
        }

        for (pathData in state.paths) {
            paint.color = pathData.color.toArgb()
            paint.strokeWidth = pathData.thickness

            val path = android.graphics.Path().apply {
                pathData.path.firstOrNull()?.let { moveTo(it.x, it.y) }
                pathData.path.drop(1).forEach { lineTo(it.x, it.y) }
            }

            canvas.drawPath(path, paint)
        }

        return bitmap
    }
}