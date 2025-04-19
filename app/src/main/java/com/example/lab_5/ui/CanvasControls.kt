package com.example.lab_5.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach

@Composable
fun CanvasControls(
    selectedColor: Color,
    selectedThickness: Float,
    colors: List<Color>,
    onSelectColor: (Color) -> Unit,
    onSelectThickness: (Float) -> Unit,
    onClearCanvas: () -> Unit,
    onLoadClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Slider(
        modifier = modifier
            .padding(8.dp),
        value = selectedThickness,
        onValueChange = { onSelectThickness(it) },
        valueRange = 1f..50f
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
    ) {
        colors.fastForEach { color ->
            val isSelected = selectedColor == color

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        val scale = if (isSelected) 1.25f else 1f
                        scaleX = scale
                        scaleY = scale
                    }
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = 2.dp,
                        color = if (isSelected) {
                            Color.Black
                        } else {
                            Color.Transparent
                        },
                        shape = CircleShape
                    )
                    .clickable {
                        onSelectColor(color)
                    }
            )
        }
    }
    Row {
        Button(
            onClick = onLoadClick,
            modifier = modifier.padding(4.dp)
        ) {
            Text("Загрузить")
        }
        Button(
            onClick = onClearCanvas,
            modifier = modifier.padding(4.dp)
        ) {
            Text("Очистить")
        }
        Button(
            onClick = onSaveClick,
            modifier = modifier.padding(4.dp)
        ) {
            Text("Сохранить")
        }
    }
}