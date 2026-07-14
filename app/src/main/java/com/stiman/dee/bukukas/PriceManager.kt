package com.stiman.dee.bukukas

import android.content.Context
import android.content.SharedPreferences

object PriceManager {
    private const val PREFS_NAME = "buku_kas_prices"
    private const val KEY_PREFIX = "price_"
    private const val KEY_QRIS = "qris_string"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getPrice(serviceName: String): Long {
        val saved = prefs?.getLong(KEY_PREFIX + serviceName, -1L)
        return if (saved != null && saved >= 0) {
            saved
        } else {
            defaultServiceOptions.find { it.name == serviceName }?.price ?: 0L
        }
    }

    fun setPrice(serviceName: String, price: Long) {
        prefs?.edit()?.putLong(KEY_PREFIX + serviceName, price)?.apply()
    }

    fun getServicesWithPrices(): List<ServiceOption> {
        return defaultServiceOptions.map { service ->
            service.copy(price = getPrice(service.name))
        }
    }

    fun getQrisString(): String {
        return prefs?.getString(KEY_QRIS, "") ?: ""
    }

    fun setQrisString(qris: String) {
        prefs?.edit()?.putString(KEY_QRIS, qris)?.apply()
    }
}
