package com.example.diagassistant.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.diagassistant.grpc.TelemetryClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class LiveUiState(
    val rpm: Int = 0,
    val temperatureC: Float = 0f,     // NEW
    val adcRaw: Int = 0,
    val voltage: Double = 0.0,        // keep (set to 0.0 if server doesn't send it)
    val connected: Boolean = false,
    val error: String? = null
)

class LiveViewModel : ViewModel() {

    private val client = TelemetryClient(host = "127.0.0.1", port = 50051)

    private val _ui = MutableStateFlow(LiveUiState())
    val ui: StateFlow<LiveUiState> = _ui

    init {
        Log.d("LiveVM", "init called - starting telemetry stream")
        client.startTelemetryStream(
            onSample = { s ->
                Log.d(
                    "LiveVM",
                    "onSample rpm=${s.rpm} tempC=${s.temperatureC} adc=${s.adcRaw} ts=${s.tsMs}"
                )

                _ui.update {
                    it.copy(
                        rpm = s.rpm,
                        temperatureC = s.temperatureC,
                        adcRaw = s.adcRaw.toInt(),
                        voltage = 0.0,              // not provided by new server (keep for now)
                        connected = true,
                        error = null
                    )
                }
            },
            onError = { t ->
                Log.e("LiveVM", "stream error", t)
                _ui.update { it.copy(connected = false, error = t.toString()) }
            }
        )
    }

    override fun onCleared() {
        client.close()
        super.onCleared()
    }
}