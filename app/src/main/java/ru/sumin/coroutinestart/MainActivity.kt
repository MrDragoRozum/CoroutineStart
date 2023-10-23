package ru.sumin.coroutinestart

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.sumin.coroutinestart.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.buttonLoad.setOnClickListener {
//            lifecycleScope.launch {
//                loadData()
//            }
//            loadDataWithoutCoroutine()

            with(binding) {
                progress.isVisible = true
                buttonLoad.isEnabled = false

                val deferredCity: Deferred<String> = lifecycleScope.async { loadCity() }
                val deferredTemp: Deferred<Int> = lifecycleScope.async { loadTemperature() }
                lifecycleScope.launch {
                    val city = deferredCity.await()
                    val temp = deferredTemp.await().toString()
                    tvLocation.text = city
                    tvTemperature.text = temp
                    progress.isVisible = false
                    buttonLoad.isEnabled = true
                }
            }
        }
    }

    private fun loadDataWithoutCoroutine(step: Int = 0, obj: Any? = null) {
        when (step) {
            0 -> {
                Log.d("MainActivity", "Load started: $this")
                binding.progress.isVisible = true
                binding.buttonLoad.isEnabled = false
                loadCityWithoutCoroutine {
                    loadDataWithoutCoroutine(1, it)
                }
            }

            1 -> {
                val city = obj as String
                binding.tvLocation.text = city
                val temp = loadTemperatureWithoutCoroutine(city) {
                    loadDataWithoutCoroutine(2, it)
                }
            }

            2 -> {
                val temp = obj as Int
                binding.tvTemperature.text = temp.toString()
                binding.progress.isVisible = false
                binding.buttonLoad.isEnabled = true
                Log.d("MainActivity", "Load finished: $this")
            }
        }
    }

    private fun loadTemperatureWithoutCoroutine(city: String, callback: (Int) -> Unit) {
        Toast.makeText(
            this,
            getString(R.string.loading_temperature_toast, city),
            Toast.LENGTH_SHORT
        ).show()
        Handler(Looper.getMainLooper()).postDelayed(5000) { callback(17) }
    }

    private fun loadCityWithoutCoroutine(callback: (String) -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed(5000) { callback("Moscow") }
    }

    private suspend fun loadData() {
        Log.d("MainActivity", "Load started: $this")
        binding.progress.isVisible = true
        binding.buttonLoad.isEnabled = false
        val city = loadCity()
        binding.tvLocation.text = city
        val temp = loadTemperature()
        binding.tvTemperature.text = temp.toString()
        binding.progress.isVisible = false
        binding.buttonLoad.isEnabled = true
        Log.d("MainActivity", "Load finished: $this")
    }

    private suspend fun loadCity(): String {
        delay(5000)
        return "Moscow"
    }

    private suspend fun loadTemperature(): Int {
        delay(5000)
        return 17
    }
}
