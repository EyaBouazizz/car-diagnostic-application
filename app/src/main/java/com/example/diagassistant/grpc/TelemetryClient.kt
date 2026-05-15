package com.example.diagassistant.grpc

import android.util.Log
import com.example.diagassistant.telemetry.Empty
import com.example.diagassistant.telemetry.TelemetrySample
import com.example.diagassistant.telemetry.TelemetryServiceGrpc
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver

class TelemetryClient(
    private val host: String = "127.0.0.1",
    private val port: Int = 50051,
) {
    private val channel: ManagedChannel =
        ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext()
            .build()

    private val stub: TelemetryServiceGrpc.TelemetryServiceStub =
        TelemetryServiceGrpc.newStub(channel)

    fun startTelemetryStream(
        onSample: (TelemetrySample) -> Unit,
        onError: (Throwable) -> Unit,
    ) {
        Log.d("Grpc", "Starting StreamTelemetry to $host:$port")

        stub.streamTelemetry(
            Empty.getDefaultInstance(),
            object : StreamObserver<TelemetrySample> {

                override fun onNext(value: TelemetrySample) {
                    Log.d(
                        "TelemetryClient",
                        "onNext rpm=${value.rpm} tempC=${value.temperatureC} adc=${value.adcRaw} ts=${value.tsMs}"
                    )
                    onSample(value)
                }

                override fun onError(t: Throwable) {
                    Log.e("TelemetryClient", "onError", t)
                    onError(t)
                }

                override fun onCompleted() {
                    Log.d("TelemetryClient", "onCompleted")
                }
            }
        )
    }

    fun close() {
        channel.shutdownNow()
    }
}