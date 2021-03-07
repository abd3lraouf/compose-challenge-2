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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun ScrollInputPreview() {
    var minutes by remember { mutableStateOf(0) }
    var seconds by remember { mutableStateOf(0) }

    Row(
        Modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        VerticalInput(
            min = 0,
            max = 10,
            onValueChanged = { minutes = it },
            value = minutes
        )
        Text(text = ":", style = MaterialTheme.typography.h3, fontSize = 36.sp)
        VerticalInput(
            min = 0,
            max = 59,
            onValueChanged = { seconds = it },
            value = seconds
        )
    }
}

@Composable
fun VerticalInput(
    min: Int,
    max: Int,
    value: Int,
    modifier: Modifier = Modifier,
    onValueChanged: (Int) -> Unit
) {
    Column(
        modifier = modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(8.dp))
            .padding(0.dp, 8.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val enableTop = value != max
        IconButton(
            enabled = enableTop,
            onClick = { onValueChanged((value + 1).coerceIn(min, max)) }
        ) {
            CompositionLocalProvider(LocalContentAlpha provides if (enableTop) ContentAlpha.high else ContentAlpha.disabled) {
                Icon(
                    Icons.Filled.ChevronLeft,
                    contentDescription = "Up button",
                    modifier = Modifier
                        .rotate(90f)
                        .size(36.dp)
                )
            }
        }
        Text(text = "$value", style = MaterialTheme.typography.h3)
        val enableBottom = value != min
        IconButton(
            enabled = enableBottom,
            onClick = { onValueChanged((value - 1).coerceIn(min, max)) }
        ) {
            CompositionLocalProvider(LocalContentAlpha provides if (enableBottom) ContentAlpha.high else ContentAlpha.disabled) {
                Icon(
                    Icons.Filled.ChevronLeft,
                    contentDescription = "Down button",
                    modifier = Modifier
                        .rotate(-90f)
                        .size(36.dp)
                )
            }
        }
    }
}
