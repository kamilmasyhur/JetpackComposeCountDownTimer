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
package com.example.androiddevchallenge.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androiddevchallenge.data.TimeModel
import com.example.androiddevchallenge.usecase.CountDownTimer
import com.example.androiddevchallenge.usecase.CountDownUseCase

class CountDownTimerViewModel : ViewModel() {

    private val _timeModel = MutableLiveData<CountDownTimerState>()
    val timeModel: LiveData<CountDownTimerState> = _timeModel
    private val countDownTimer by lazy {
        CountDownTimer()
    }

    fun startTimer(hours: Int, minutes: Int, seconds: Int) {
        val time: Long = CountDownUseCase.timeToMillis(hours, minutes, seconds)
        startTimer(time)
    }

    fun stopTimer() = countDownTimer.stop()

    fun resetTimer() {
        countDownTimer.stop()
        _timeModel.value = CountDownTimerState.OnTick(TimeModel(hours = 0, minutes = 0, seconds = 0))
    }

    private fun startTimer(time: Long) {
        countDownTimer.start(
            time = time,
            onTick = { tickTime ->
                _timeModel.value = CountDownTimerState.OnTick(tickTime)
            },
            onFinish = {
                _timeModel.value = CountDownTimerState.OnFinish
            }
        )
    }
}
