package com.stiman.dee.bukukas

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

object PriceManager {
    private const val PREFS_NAME = "buku_kas_prices"
    private const val KEY_PREFIX = "price_"
    private const val KEY_QRIS = "qris_string"
    private const val KEY_CUSTOM_SERVICES = "custom_services"
    private const val KEY_DELETED_SERVICES = "deleted_services"

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
        val customServices = getCustomServices()
        val deletedServices = getDeletedServices()

        // Start with default services (excluding deleted ones)
        val defaultServices = defaultServiceOptions
            .filter { it.name !in deletedServices }
            .map { service ->
                service.copy(price = getPrice(service.name))
            }

        // Add custom services with their prices
        val customWithPrices = customServices.map { service ->
            service.copy(price = getPrice(service.name))
        }

        return defaultServices + customWithPrices
    }

    fun addCustomService(service: ServiceOption) {
        val customServices = getCustomServices().toMutableList()
        customServices.add(service)
        saveCustomServices(customServices)
        // Save price separately
        setPrice(service.name, service.price)
    }

    fun updateService(oldName: String, updatedService: ServiceOption) {
        // If name changed, we need to handle the old price key
        if (oldName != updatedService.name) {
            prefs?.edit()?.remove(KEY_PREFIX + oldName)?.apply()
        }
        setPrice(updatedService.name, updatedService.price)

        // Update in custom services if it's a custom service
        val customServices = getCustomServices().toMutableList()
        val index = customServices.indexOfFirst { it.name == oldName }
        if (index != -1) {
            customServices[index] = updatedService
            saveCustomServices(customServices)
        }
        // If it's a default service, just update the price (already done above)
    }

    fun deleteService(serviceName: String) {
        // Check if it's a custom service
        val customServices = getCustomServices().toMutableList()
        val isCustom = customServices.any { it.name == serviceName }

        if (isCustom) {
            customServices.removeAll { it.name == serviceName }
            saveCustomServices(customServices)
        } else {
            // It's a default service, add to deleted list
            val deletedServices = getDeletedServices().toMutableSet()
            deletedServices.add(serviceName)
            saveDeletedServices(deletedServices)
        }

        // Remove price
        prefs?.edit()?.remove(KEY_PREFIX + serviceName)?.apply()
    }

    private fun getCustomServices(): List<ServiceOption> {
        val json = prefs?.getString(KEY_CUSTOM_SERVICES, "[]") ?: "[]"
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                ServiceOption(
                    name = obj.getString("name"),
                    price = obj.getLong("price"),
                    description = obj.getString("description"),
                    category = obj.getString("category")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveCustomServices(services: List<ServiceOption>) {
        val array = JSONArray()
        services.forEach { service ->
            val obj = JSONObject().apply {
                put("name", service.name)
                put("price", service.price)
                put("description", service.description)
                put("category", service.category)
            }
            array.put(obj)
        }
        prefs?.edit()?.putString(KEY_CUSTOM_SERVICES, array.toString())?.apply()
    }

    private fun getDeletedServices(): Set<String> {
        val json = prefs?.getString(KEY_DELETED_SERVICES, "[]") ?: "[]"
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { array.getString(it) }.toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    private fun saveDeletedServices(services: Set<String>) {
        val array = JSONArray()
        services.forEach { array.put(it) }
        prefs?.edit()?.putString(KEY_DELETED_SERVICES, array.toString())?.apply()
    }

    fun getQrisString(): String {
        return prefs?.getString(KEY_QRIS, "") ?: ""
    }

    fun setQrisString(qris: String) {
        prefs?.edit()?.putString(KEY_QRIS, qris)?.apply()
    }
}
