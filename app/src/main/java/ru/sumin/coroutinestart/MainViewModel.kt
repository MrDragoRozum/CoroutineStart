package ru.sumin.coroutinestart

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class MainViewModel: ViewModel() {

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + parentJob)
    fun method() {
        val childJob1 = coroutineScope.launch {
            delay(3000)
            Log.d(LOG_TAG, "Первая корутина завершила работу")
        }
        val childJob2 = coroutineScope.launch {
            delay(2000)
            childJob1.cancel()
            Log.d(LOG_TAG, "Вторая корутина завершила работу")
            Log.d(LOG_TAG, "Родительская корутина завершила работу: ${parentJob.isCancelled}")
        }
//        thread {
//            Thread.sleep(1000)
//            parentJob.cancel() // Завершил свою работу, все его наследники (корутины) были отменены
//            Log.d(LOG_TAG, "Родительский Job активный: ${parentJob.isActive}") // Активный
//        }
        Log.d(LOG_TAG, parentJob.children.contains(childJob1).toString()) // true
        Log.d(LOG_TAG, parentJob.children.contains(childJob2).toString()) // true

    }

    override fun onCleared() {
        super.onCleared()
        thread {
            Log.d(LOG_TAG, "Родительский Job активный: ${parentJob.isActive}")
        }
        coroutineScope.cancel()
    }

    companion object {
        private const val LOG_TAG = "MainViewModel"
    }
}