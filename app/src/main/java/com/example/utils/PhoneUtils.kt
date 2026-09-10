package com.example.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

object PhoneUtils {
    private const val DEFAULT_EMERGENCY_NUMBER = "08009999999"

    /**
     * Utiliza a intenção 'tel:' para disparar uma chamada telefônica automática 
     * para a central de emergência 24h.
     * Abre o discador do sistema preenchido com o número.
     */
    fun callEmergencyCenter(context: Context, phoneNumber: String = DEFAULT_EMERGENCY_NUMBER) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("PhoneUtils", "Erro ao abrir o discador: ${e.message}")
        }
    }
}
