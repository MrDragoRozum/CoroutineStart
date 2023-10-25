package ru.sumin.coroutinestart

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val parentJob = Job()
    private val exception = CoroutineExceptionHandler { _, throwable ->
        Log.d(
            LOG_TAG,
            "Исключение поймано: $throwable"
        )
    }
    private val coroutineScope = CoroutineScope(Dispatchers.IO + parentJob + exception)
    fun method() {
        val childJob1 = coroutineScope.launch {
            delay(1000)
            Log.d(LOG_TAG, "Первая корутина завершила работу")
            launch {
                launch {
                    delay(3000)
                    error()
                }
                Log.d(LOG_TAG, "Четвертая корутина завершила работу")
            }
        }
        val childJob2 = coroutineScope.launch {
            delay(5000)
            Log.d(LOG_TAG, "Вторая корутина завершила работу")
            launch {
                Log.d(LOG_TAG, "Пятая корутина завершила работу")
            }
        }
        val childJob3 = coroutineScope.launch {
            delay(3000)
            Log.d(LOG_TAG, "Третья корутина завершила работу")
        }
    }

    private fun error() {
        throw RuntimeException()
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
    }

    companion object {
        private const val LOG_TAG = "MainViewModel"
    }
}