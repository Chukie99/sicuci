package com.stiman.dee.bukukas

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerOrderDao {
    @Insert
    suspend fun insert(order: CustomerOrder): Long

    @Update
    suspend fun update(order: CustomerOrder)

    @Delete
    suspend fun delete(order: CustomerOrder)

    @Query("SELECT * FROM customer_orders WHERE status != 'paid' ORDER BY queueNumber ASC")
    fun getActiveOrders(): Flow<List<CustomerOrder>>

    @Query("SELECT * FROM customer_orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<CustomerOrder>>

    @Query("SELECT * FROM customer_orders WHERE status = 'paid' ORDER BY paidAt DESC")
    fun getPaidOrders(): Flow<List<CustomerOrder>>

    @Query("SELECT * FROM customer_orders WHERE plateNumber = :plateNumber ORDER BY createdAt DESC")
    fun getOrdersByPlateNumber(plateNumber: String): Flow<List<CustomerOrder>>

    @Query("SELECT MAX(queueNumber) FROM customer_orders WHERE date(createdAt/1000, 'unixepoch', 'localtime') = date('now', 'localtime')")
    suspend fun getTodayMaxQueue(): Int?

    @Query("SELECT * FROM customer_orders WHERE status = 'paid' AND date(paidAt/1000, 'unixepoch', 'localtime') = date('now', 'localtime') ORDER BY paidAt DESC")
    fun getTodayPaidOrders(): Flow<List<CustomerOrder>>

    @Query("DELETE FROM customer_orders WHERE id = :id")
    suspend fun deleteById(id: Long)
}
