package com.stiman.dee.bukukas

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE date = :date ORDER BY timestamp DESC")
    fun getTransactionsByDate(date: String): Flow<List<Transaction>>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE date = :date AND type = 'income'")
    fun getTotalIncomeByDate(date: String): Flow<Long>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE date = :date AND type = 'expense'")
    fun getTotalExpenseByDate(date: String): Flow<Long>

    @Query("SELECT * FROM transactions ORDER BY date DESC, timestamp DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'income'")
    fun getTotalIncomeAll(): Flow<Long>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'expense'")
    fun getTotalExpenseAll(): Flow<Long>
}
