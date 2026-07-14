package com.stiman.dee.bukukas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customer_orders")
data class CustomerOrder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plateNumber: String,
    val motorType: String,
    val serviceType: String,       // Cuci Biasa, Cuci + Semir, dll
    val servicePrice: Long,        // Harga layanan
    val status: String,            // "waiting", "washing", "done", "paid"
    val paymentMethod: String? = null,  // "qris" or "cash"
    val paymentAmount: Long = 0,   // Jumlah bayar
    val change: Long = 0,          // Kembalian (kalau cash)
    val queueNumber: Int,          // Nomor antrian
    val createdAt: Long,           // Waktu masuk antrian
    val startedAt: Long? = null,   // Waktu mulai dicuci
    val completedAt: Long? = null, // Waktu selesai dicuci
    val paidAt: Long? = null       // Waktu bayar
)
