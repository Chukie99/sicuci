package com.stiman.dee.bukukas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // "income" or "expense"
    val amount: Long, // in Rupiah
    val category: String,
    val note: String = "",
    val date: String, // yyyy-MM-dd
    val time: String, // HH:mm:ss
    val timestamp: Long // epoch millis for sorting
)
