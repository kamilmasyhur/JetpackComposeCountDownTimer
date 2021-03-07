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
package com.example.androiddevchallenge.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.extension.exhaustive
import com.example.androiddevchallenge.extension.getOrDefault
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.viewmodel.CountDownTimerState
import com.example.androiddevchallenge.viewmodel.CountDownTimerViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    val viewModel = CountDownTimerViewModel()
    Surface(color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            val isStartButtonEnabled = remember { mutableStateOf(true) }
            val hours = remember { mutableStateOf("") }
            val minutes = remember { mutableStateOf("") }
            val seconds = remember { mutableStateOf("") }
            val timer by viewModel.timeModel.observeAsState(CountDownTimerState.OnFinish)

            Headers()
            Body(hours, minutes, seconds, isStartButtonEnabled, viewModel, timer)
        }
    }
}

@Composable
private fun Body(
    hours: MutableState<String>,
    minutes: MutableState<String>,
    seconds: MutableState<String>,
    isStartButtonEnabled: MutableState<Boolean>,
    viewModel: CountDownTimerViewModel,
    timer: CountDownTimerState
) {
    TimerHeader()
    UserInput(hours, minutes, seconds)
    StartButton(isStartButtonEnabled, viewModel, hours, minutes, seconds)
    Timer(timer, isStartButtonEnabled)
}

@Composable
private fun Timer(
    timer: CountDownTimerState,
    isStartButtonEnabled: MutableState<Boolean>,
) {
    when (timer) {
        is CountDownTimerState.OnTick -> {
            with(timer.time) {
                CountDownTimerView(
                    hour = this.hours,
                    minute = this.minutes,
                    second = this.seconds,
                )
            }
        }
        is CountDownTimerState.OnFinish -> {
            CountDownTimerView(hour = 0, minute = 0, second = 0)
            isStartButtonEnabled.value = true
        }
    }.exhaustive
}

@Composable
private fun StartButton(
    isStartButtonEnabled: MutableState<Boolean>,
    viewModel: CountDownTimerViewModel,
    hours: MutableState<String>,
    minutes: MutableState<String>,
    seconds: MutableState<String>,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Dp(16f)),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            enabled = isStartButtonEnabled.value,
            onClick = {
                isStartButtonEnabled.value = false
                viewModel.startTimer(
                    hours = hours.value.getOrDefault("0").toInt(),
                    minutes = minutes.value.getOrDefault("0").toInt(),
                    seconds = seconds.value.getOrDefault("0").toInt()
                )
            }
        ) {
            Text(text = LocalContext.current.getString(R.string.start))
        }
        Button(
            enabled = !isStartButtonEnabled.value,
            onClick = {
                isStartButtonEnabled.value = true
                viewModel.stopTimer()
            }
        ) {
            Text(text = LocalContext.current.getString(R.string.stop))
        }
        Button(
            onClick = {
                isStartButtonEnabled.value = true
                viewModel.resetTimer()
            }
        ) {
            Text(text = LocalContext.current.getString(R.string.reset))
        }
    }
}

@Composable
private fun UserInput(
    hours: MutableState<String>,
    minutes: MutableState<String>,
    seconds: MutableState<String>,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Dp(16f))
    ) {
        OutlinedTextField(
            value = hours.value,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            placeholder = { Placeholder(resource = R.string.put_hour) },
            singleLine = true,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(1f / 3f),
            onValueChange = {
                hours.value = it
            }
        )
        OutlinedTextField(
            value = minutes.value,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            placeholder = { Placeholder(resource = R.string.put_minute) },
            singleLine = true,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(0.5f),
            onValueChange = {
                minutes.value = it
            }
        )
        OutlinedTextField(
            value = seconds.value,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            placeholder = { Placeholder(resource = R.string.put_second) },
            singleLine = true,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {
                seconds.value = it
            }
        )
    }
}

@Composable
fun Placeholder(@StringRes resource: Int) {
    Text(text = LocalContext.current.getString(resource))
}

@Composable
private fun Headers() {
    Text(
        text = LocalContext.current.getString(R.string.welcome_message),
        modifier = Modifier.padding(bottom = Dp(16f))
    )
}

@Composable
private fun TimerHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = LocalContext.current.getString(R.string.hour),
            modifier = Modifier.fillMaxWidth(1f / 3f),
            textAlign = TextAlign.Center
        )
        Text(
            text = LocalContext.current.getString(R.string.minute),
            modifier = Modifier.fillMaxWidth(0.5f),
            textAlign = TextAlign.Center
        )
        Text(
            text = LocalContext.current.getString(R.string.second),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CountDownTimerView(hour: Int, minute: Int, second: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(text = "$hour : $minute : $second")
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
