package io.github.jmoralese.djimini2cv

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import io.github.jmoralese.djimini2cv.ui.ConnectionScreen
import io.github.jmoralese.djimini2cv.ui.DroneViewModel
import io.github.jmoralese.djimini2cv.ui.theme.Dji2Theme

class MainActivity : ComponentActivity() {

    private val viewModel: DroneViewModel by viewModels {
        DroneViewModel.factory((application as DroneApp).droneGateway)
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            // Sin bloqueo: el registro DJI informa de su propio error si algo falta.
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestMissingRuntimePermissions()
        setContent {
            Dji2Theme {
                ConnectionScreen(viewModel)
            }
        }
    }

    private fun requestMissingRuntimePermissions() {
        val missing = DroneRuntime.runtimePermissions.filter {
            checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isNotEmpty()) {
            permissionLauncher.launch(missing.toTypedArray())
        }
    }
}
