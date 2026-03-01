package com.example.smsautomatico

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smsautomatico.ui.theme.SMSAutomaticoTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.all { it.value }
        if (!granted) {
            Toast.makeText(this, "Se requieren todos los permisos para funcionar", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestPermissionsLauncher.launch(
            arrayOf(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.SEND_SMS
            )
        )

        setContent {
            SMSAutomaticoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ConfiguracionSMS(
                        modifier = Modifier.padding(innerPadding),
                        onSave = { numero, mensaje -> guardardatos(numero, mensaje) }
                    )
                }
            }
        }
    }

    private fun guardardatos(numero: String, mensaje: String) {
        val shared = getSharedPreferences("config_sms", Context.MODE_PRIVATE)
        with(shared.edit()) {
            putString("numero_objetivo", numero)
            putString("mensaje_respuesta", mensaje)
            apply()
        }
        Toast.makeText(this, "Configuración Guardada", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun ConfiguracionSMS(modifier: Modifier = Modifier, onSave: (String, String) -> Unit) {
    var numero by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Respuesta Automática SMS", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = numero,
            onValueChange = { numero = it },
            label = { Text("Número a responder") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = mensaje,
            onValueChange = { mensaje = it },
            label = { Text("Mensaje de respuesta") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { onSave(numero, mensaje) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Configuración")
        }
    }
}
