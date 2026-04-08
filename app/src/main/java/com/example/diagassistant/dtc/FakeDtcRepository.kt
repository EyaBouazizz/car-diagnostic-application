package com.example.diagassistant.dtc

import kotlinx.coroutines.delay

enum class DtcSeverity { INFO, ADVISORY, CRITICAL }

data class DtcCode(
    val code: String,
    val title: String,
    val severity: DtcSeverity,
    val system: String,
    val frequency: String,
    val description: String,

    // details
    val possibleCauses: List<String>,
    val symptoms: List<String>,
    val recommendedActions: List<String>,
    val freezeFrame: Map<String, String>,
    val notes: String,
)

object FakeDtcRepository {

    // Simulate a scan call
    suspend fun runScan(): List<DtcCode> {
        delay(1200) // fake "diagnostic time"
        return activeDtcs()
    }

    private fun activeDtcs(): List<DtcCode> = listOf(
        DtcCode(
            code = "P0300",
            title = "Random or Multiple Cylinder Misfire Detected",
            severity = DtcSeverity.CRITICAL,
            system = "Powertrain",
            frequency = "Intermittent",
            description =
                "The powertrain control module (PCM) detected that one or more cylinders are not firing properly. " +
                        "Continued driving may cause catalytic converter damage and increased emissions.",
            possibleCauses = listOf(
                "Worn spark plugs or ignition coils",
                "Vacuum leak causing lean condition",
                "Low fuel pressure or injector fault",
                "Engine mechanical issue (compression/valve)"
            ),
            symptoms = listOf(
                "Rough idle or shaking under load",
                "Loss of power / hesitation",
                "Flashing MIL (check engine light) under misfire events",
                "Fuel smell in exhaust"
            ),
            recommendedActions = listOf(
                "Reduce load and avoid high RPM until inspected",
                "Check spark plugs and ignition coils (swap test if needed)",
                "Inspect intake hoses for vacuum leaks",
                "Verify fuel pressure and injector balance"
            ),
            freezeFrame = mapOf(
                "RPM" to "3,850",
                "Vehicle Speed" to "48 km/h",
                "Coolant Temp" to "92°C",
                "Short Term Fuel Trim" to "+18%",
                "Long Term Fuel Trim" to "+9%",
                "Throttle" to "22%"
            ),
            notes = "Critical severity because misfires can overheat the catalytic converter."
        ),
        DtcCode(
            code = "P0171",
            title = "System Too Lean (Bank 1)",
            severity = DtcSeverity.ADVISORY,
            system = "Fuel & Air Metering",
            frequency = "Continuous",
            description =
                "The ECU indicates Bank 1 is operating with too much air and not enough fuel. " +
                        "This is often caused by unmetered air (vacuum leak) or fuel delivery issues.",
            possibleCauses = listOf(
                "Vacuum leak after MAF sensor",
                "Dirty/failed MAF sensor",
                "Weak fuel pump / clogged fuel filter",
                "Exhaust leak upstream of O2 sensor"
            ),
            symptoms = listOf(
                "Rough idle",
                "Hesitation on acceleration",
                "Higher than normal fuel trims",
                "Possible ping/knock under load"
            ),
            recommendedActions = listOf(
                "Inspect for vacuum leaks and loose intake clamps",
                "Clean MAF sensor (MAF-safe cleaner)",
                "Check fuel pressure under load",
                "Inspect upstream exhaust leaks"
            ),
            freezeFrame = mapOf(
                "RPM" to "2,150",
                "Vehicle Speed" to "0 km/h",
                "Coolant Temp" to "88°C",
                "Short Term Fuel Trim" to "+14%",
                "Long Term Fuel Trim" to "+12%",
                "MAF" to "4.2 g/s"
            ),
            notes = "Advisory severity: vehicle often remains drivable but should be inspected soon."
        ),
        DtcCode(
            code = "P0562",
            title = "System Voltage Low",
            severity = DtcSeverity.INFO,
            system = "Electrical / Charging",
            frequency = "Intermittent",
            description =
                "The ECU detected supply voltage below the expected threshold during operation. " +
                        "Low voltage can cause unstable sensor readings and module resets.",
            possibleCauses = listOf(
                "Weak battery",
                "Loose battery terminals / ground strap",
                "Alternator undercharging",
                "High electrical load with low idle speed"
            ),
            symptoms = listOf(
                "Slow crank or hard start",
                "Flickering lights",
                "Random warning messages / infotainment reboot"
            ),
            recommendedActions = listOf(
                "Measure battery voltage at rest and while running",
                "Check ground straps and terminal tightness",
                "Verify alternator charge voltage (typically ~13.8–14.6V)"
            ),
            freezeFrame = mapOf(
                "Voltage" to "11.6V",
                "RPM" to "900",
                "Coolant Temp" to "90°C",
                "Electrical Load" to "High"
            ),
            notes = "Info severity: may be transient, but repeated events indicate a charging/battery issue."
        )
    )
}