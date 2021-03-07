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

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieAnimationSpec
import com.airbnb.lottie.compose.rememberLottieAnimationState
import com.example.androiddevchallenge.R
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun TimerScreen() {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        val timerTextScale = remember { Animatable(1f) }

        var minutes by remember { mutableStateOf(0) }
        var seconds by remember { mutableStateOf(20) }

        var running by remember { mutableStateOf(false) }

        var playPauseToggle by remember { mutableStateOf(false) }

        val totalTime = minutes * 60L + seconds
        var elapsedTime by remember { mutableStateOf(0L) }
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(elapsedTime) {
            if (elapsedTime > 0 && elapsedTime < totalTime - 5) {
                timerTextScale.animateTo(1.3F, animationSpec = tween(150))
                timerTextScale.animateTo(1.0F, animationSpec = tween(100))
            } else if ((totalTime - elapsedTime) in 0..5) {
                repeat(3) {
                    timerTextScale.animateTo(1.3F, animationSpec = tween(150))
                    timerTextScale.animateTo(1.0F, animationSpec = tween(100))
                }
            }
        }

        Box(Modifier) {

            Text(
                text = abs(totalTime - elapsedTime).formatDuration(),
                style = MaterialTheme.typography.h2,
                modifier = Modifier
                    .align(Alignment.Center)
                    .scale(timerTextScale.value)
            )
            TimerCircle(
                Modifier
                    .width(300.dp)
                    .height(300.dp),
                elapsedTime = elapsedTime,
                totalTime = totalTime,
            )
        }

        Column(
            Modifier
                .padding(vertical = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                VerticalInput(
                    min = 0,
                    max = 9,
                    onValueChanged = { minutes = it },
                    value = minutes
                )
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = ":",
                    style = MaterialTheme.typography.h3
                )
                VerticalInput(
                    min = 0,
                    max = 59,
                    onValueChanged = { seconds = it },
                    value = seconds
                )
            }
            Box(Modifier.size(56.dp)) {
                PlayPauseToggleButton(
                    onClick = {
                        if (playPauseToggle) {
                            running = false
                            coroutineScope.coroutineContext.cancelChildren()
                        } else {
                            running = true
                            coroutineScope.launch {
                                while (elapsedTime < totalTime) {
                                    ensureActive()
                                    elapsedTime += 1
                                    delay(1000)
                                }
                                while (elapsedTime > 0 ) {
                                    ensureActive()
                                    elapsedTime -= 1
                                    delay(100)
                                }
                                elapsedTime = 0
                                running = false
                            }
                        }
                        playPauseToggle = !playPauseToggle
                    },
                    isTimerRunning = running
                )
            }
        }
    }
}

@Composable
private fun PlayPauseToggleButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    isTimerRunning: Boolean = true,
) {
    val state = rememberLottieAnimationState()
    val interactionSource = remember { MutableInteractionSource() }

    LottieAnimation(
        spec = LottieAnimationSpec.RawRes(if (isTimerRunning) R.raw.play_pause else R.raw.pause_play),
        animationState = state.apply { this.isPlaying = !isTimerRunning },
        modifier = modifier.clickable(
            indication = null,
            interactionSource = interactionSource,
            onClick = onClick
        )
    )
}

@Composable
@Preview
fun TimerScreenPreview() {
    TimerScreen()
}
