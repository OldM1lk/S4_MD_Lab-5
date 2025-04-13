package com.example.lab_5.data

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class DrawingState(
    val selectedColor: Color = Color.Black,
    val currentPath: PathData? = null,
    val paths: List<PathData> = emptyList(),
    val selectedThickness: Float = 10f
)

val colors = listOf(
    Color.Black,
    Color.Red,
    Color.Blue,
    Color.Green,
    Color.Yellow,
    Color.Magenta,
    Color.Cyan
)

data class PathData(
    val id: Long,
    val color: Color,
    val path: List<Offset>,
    val thickness: Float
)

sealed interface DrawingAction {
    data object OnNewPathStart : DrawingAction
    data class OnDraw(val offset: Offset) : DrawingAction
    data object OnPathEnd : DrawingAction
    data class OnSelectColor(val color: Color) : DrawingAction
    data object OnClearCanvasClick : DrawingAction
    data class OnSelectThickness(val thickness: Float) : DrawingAction
}