package com.example.lab_5.ui.screens

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import com.example.lab_5.data.DrawingAction
import com.example.lab_5.data.DrawingState
import com.example.lab_5.data.ImageAction
import com.example.lab_5.data.PathData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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
            is DrawingAction.OnSetBackgroundImage -> onSetBackgroundImage(action.image)
        }
    }

    private fun onSetBackgroundImage(image: ImageBitmap) {
        _state.update {
            it.copy(
                backgroundImage = image
            )
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

    fun onImageAction(action: ImageAction) {
        when(action) {
            is ImageAction.OnLoadImage -> onLoadImage(action.image)
            is ImageAction.OnSaveImage -> onSaveImage(action.bitmap, action.context)
        }
    }

    private fun onSaveImage(bitmap: Bitmap, context: Context) {

    }

    private fun onLoadImage(image: ImageBitmap) {

    }
}