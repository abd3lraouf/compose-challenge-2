/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge.ui.timer

import android.text.format.DateUtils
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private const val TAG = "TimerCircleComponent"

@Preview
@Composable
fun TimerCircleComponentPreview() {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        var time by remember { mutableStateOf(0L) }
        val totalTime = 360L
        val coroutineScope = rememberCoroutineScope()

        TimerCircleComponent(
            screenWidthDp = 300,
            screenHeightDp = 300,
            elapsedTime = time,
            totalTime = totalTime
        )

        Button(
            onClick = {
                coroutineScope.launch {
                    while (time < totalTime) {
                        time += 1
                        delay(100)
                    }
                    time = 0
                }
            }
        ) {
            Text(text = "Start")
        }
    }
}

@Composable
fun TimerCircleComponent(
    modifier: Modifier = Modifier,
    screenWidthDp: Int,
    screenHeightDp: Int,
    elapsedTime: Long,
    totalTime: Long
) {
    val maxRadius by remember { mutableStateOf(min(screenHeightDp, screenWidthDp)) }

    Box(
        modifier = modifier
            .size(maxRadius.dp)
            .padding(8.dp)
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center),
            text = (elapsedTime / 10).formatDuration(),
            style = typography.h2,
        )

        TimerCircle(
            modifier = modifier,
            elapsedTime = elapsedTime,
            totalTime = totalTime
        )
    }
}

@Composable
fun TimerCircle(
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 6.dp,
    elapsedTime: Long,
    totalTime: Long,
) {
    Canvas(
        modifier = modifier.fillMaxSize(),
        onDraw = {
            rotate(-90F) {

                val cX = size.width / 2f
                val cY = size.height / 2f
                val r = min(cX, cY)

                val brush = Brush.sweepGradient(
                    0f to Color.Magenta.copy(alpha = 0.5f),
                    0.3f to Color.Blue.copy(alpha = 0.5f),
                    0.6f to Color.Red.copy(alpha = 0.5f),
                    1f to Color.Red.copy(alpha = 0.5f),
                    center = size.center
                )

                val arcAngleScale360 = 360F * elapsedTime / totalTime
                val deciArcAngle = arcAngleScale360.toInt() / 10 * 10F

                val cap = StrokeCap.Round

                drawArc(
                    brush = brush,
                    startAngle = 0F,
                    sweepAngle = deciArcAngle,
                    useCenter = false,
                    style = Stroke(
                        width = strokeWidth.toPx(),
                        cap = cap
                    )
                )

                val x1 = cX + r * cos(deciArcAngle.radians)
                val y1 = cY + r * sin(deciArcAngle.radians)

                val theta = (arcAngleScale360 - deciArcAngle) / 10F * 90 - deciArcAngle

                translate(x1, y1) {
                    rotate(-theta, Offset.Zero) {

                        drawLine(
                            brush = brush,
                            end = Offset.Zero,
                            start = Offset(-r * .1F, 0F),
                            strokeWidth = strokeWidth.toPx(),
                            cap = cap
                        )
                    }
                }

                // Remaining Bars
                val firstBar = deciArcAngle.toInt()
                (firstBar + 10 until 360 step 10).forEach { angleDegrees ->
                    val angle = angleDegrees.radians

                    val x1 = cX + r * cos(angle)
                    val y1 = cY + r * sin(angle)

                    translate(x1, y1) {
                        rotate((angleDegrees).toFloat(), Offset.Zero) {
                            drawLine(
                                brush = brush,
                                start = Offset.Zero,
                                end = Offset(-r * .1F, 0F),
                                strokeWidth = strokeWidth.toPx(),
                                cap = cap
                            )
                        }
                    }
                }
            }
        }
    )
}

val Float.radians get() = (this * PI / 180).toFloat()
val Int.radians get() = (this * PI / 180).toFloat()
fun Long.formatDuration(): String =
    DateUtils.formatElapsedTime(this)
