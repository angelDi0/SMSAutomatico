package com.example.smsautomatico.ui.rec

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.util.Log

class Call : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            if (state == TelephonyManager.EXTRA_STATE_RINGING && incomingNumber != null) {
                val shared = context.getSharedPreferences("config_sms", Context.MODE_PRIVATE)
                val targetNumber = shared.getString("numero_objetivo", "") ?: ""
                val message = shared.getString("mensaje_respuesta", "") ?: ""

                if (incomingNumber.contains(targetNumber) && targetNumber.isNotBlank()) {
                    try {
                        val smsManager = context.getSystemService(SmsManager::class.java)
                        smsManager.sendTextMessage(incomingNumber, null, message, null, null)
                        Log.d("Llamada", "SMS Enviado a $incomingNumber")
                    } catch (e: Exception) {
                        Log.e("Llamada", "Error al enviar SMS: ${e.message}")
                    }
                }
            }
        }
    }
}