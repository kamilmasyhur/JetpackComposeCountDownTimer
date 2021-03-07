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
package com.example.androiddevchallenge.usecase

import android.os.CountDownTimer
import com.example.androiddevchallenge.data.TimeModel

const val TICK_DURATION: Long = 1000
const val MILLIS_TO_SECOND = 1000

class CountDownTimer {
    private lateinit var countDownTimer: CountDownTimer

    fun start(
        time: Long,
        onTick: (TimeModel) -> Unit,
        onFinish: () -> Unit
    ) {
        countDownTimer = object : CountDownTimer(time, TICK_DURATION) {
            override fun onTick(millisUntilFinished: Long) {
                onTick(
                    CountDownUseCase.millisToTime(millis = millisUntilFinished / MILLIS_TO_SECOND)
                )
            }

            override fun onFinish() {
                onTick(TimeModel(hours = 0, minutes = 0, seconds = 0))
                onFinish()
            }
        }.start()
    }

    fun stop() {
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
    }
}
