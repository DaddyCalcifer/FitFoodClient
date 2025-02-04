package com.fitfood.clientapp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp

@Composable
fun BarcodeScanOverlay(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val scanAreaWidth = canvasWidth * 0.8f // Ширина области сканирования (80% экрана)
        val scanAreaHeight = 200.dp.toPx() // Высота области сканирования

        // Координаты центра экрана
        val centerX = canvasWidth / 2
        val centerY = canvasHeight / 2

        // Рисуем затемнение вокруг области сканирования
        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                color = Color.Black.copy(alpha = 0.6f)
            }

            // Затемнение сверху
            canvas.drawRect(
                Rect(Offset(0f, 0f),
                Size(canvasWidth, centerY - scanAreaHeight / 2)),
                paint
            )

            // Затемнение снизу
            canvas.drawRect(
                Rect(Offset(0f, centerY + scanAreaHeight / 2),
                Size(canvasWidth, canvasHeight - (centerY + scanAreaHeight / 2))),
                paint
            )

            // Затемнение слева
            canvas.drawRect(
                Rect(Offset(0f, centerY - scanAreaHeight / 2),
                Size(centerX - scanAreaWidth / 2, scanAreaHeight)),
                paint
            )

            // Затемнение справа
            canvas.drawRect(
                        Rect(Offset(centerX + scanAreaWidth / 2, centerY - scanAreaHeight / 2),
                Size(centerX - scanAreaWidth / 2, scanAreaHeight)),
                paint
            )
        }

        // Рисуем рамку области сканирования
        withTransform({
            translate(
                left = centerX - scanAreaWidth / 2,
                top = centerY - scanAreaHeight / 2
            )
        }) {
            drawRect(
                color = Color.Green,
                size = Size(scanAreaWidth, scanAreaHeight),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx())
            )
        }
    }
}