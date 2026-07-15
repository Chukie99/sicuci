package com.stiman.dee.bukukas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val transactionDao = db.transactionDao()
    private val orderDao = db.customerOrderDao()

    // Transaction flows
    private val _selectedDate = MutableStateFlow(getToday())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    val transactions: StateFlow<List<Transaction>> = _selectedDate.flatMapLatest { date ->
        transactionDao.getTransactionsByDate(date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalIncome: StateFlow<Long> = _selectedDate.flatMapLatest { date ->
        transactionDao.getTotalIncomeByDate(date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val totalExpense: StateFlow<Long> = _selectedDate.flatMapLatest { date ->
        transactionDao.getTotalExpenseByDate(date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val allTransactions: StateFlow<List<Transaction>> = transactionDao.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalIncomeAll: StateFlow<Long> = transactionDao.getTotalIncomeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val totalExpenseAll: StateFlow<Long> = transactionDao.getTotalExpenseAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    // Order flows
    val activeOrders: StateFlow<List<CustomerOrder>> = orderDao.getActiveOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<CustomerOrder>> = orderDao.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayPaidOrders: StateFlow<List<CustomerOrder>> = orderDao.getTodayPaidOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Customer history search
    private val _searchPlateNumber = MutableStateFlow("")
    val searchPlateNumber: StateFlow<String> = _searchPlateNumber.asStateFlow()

    val customerHistory: StateFlow<List<CustomerOrder>> = _searchPlateNumber.flatMapLatest { plate ->
        if (plate.isBlank()) {
            orderDao.getAllOrders()
        } else {
            orderDao.getOrdersByPlateNumber(plate.uppercase())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchPlateNumber(plate: String) {
        _searchPlateNumber.value = plate
    }

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    // Order operations
    fun addOrder(plateNumber: String, motorType: String, serviceType: String, servicePrice: Long) {
        viewModelScope.launch {
            val maxQueue = orderDao.getTodayMaxQueue() ?: 0
            val now = Date()
            orderDao.insert(
                CustomerOrder(
                    plateNumber = plateNumber.uppercase(),
                    motorType = motorType,
                    serviceType = serviceType,
                    servicePrice = servicePrice,
                    status = "waiting",
                    queueNumber = maxQueue + 1,
                    createdAt = now.time
                )
            )
        }
    }

    fun startWashing(order: CustomerOrder) {
        viewModelScope.launch {
            orderDao.update(
                order.copy(
                    status = "washing",
                    startedAt = Date().time
                )
            )
        }
    }

    fun finishWashing(order: CustomerOrder) {
        viewModelScope.launch {
            orderDao.update(
                order.copy(
                    status = "done",
                    completedAt = Date().time
                )
            )
        }
    }

    fun processPayment(order: CustomerOrder, paymentMethod: String, paymentAmount: Long) {
        viewModelScope.launch {
            val change = if (paymentMethod == "cash") {
                paymentAmount - order.servicePrice
            } else 0L

            orderDao.update(
                order.copy(
                    status = "paid",
                    paymentMethod = paymentMethod,
                    paymentAmount = paymentAmount,
                    change = change,
                    paidAt = Date().time
                )
            )

            // Add income transaction
            val now = Date()
            transactionDao.insert(
                Transaction(
                    type = "income",
                    amount = order.servicePrice,
                    category = order.serviceType,
                    note = "${order.motorType} - ${order.plateNumber}",
                    date = getToday(),
                    time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(now),
                    timestamp = now.time
                )
            )
        }
    }

    fun deleteOrder(order: CustomerOrder) {
        viewModelScope.launch {
            orderDao.delete(order)
        }
    }

    // Transaction operations
    fun addIncome(amount: Long, category: String, note: String) {
        viewModelScope.launch {
            val now = Date()
            transactionDao.insert(
                Transaction(
                    type = "income",
                    amount = amount,
                    category = category,
                    note = note,
                    date = _selectedDate.value,
                    time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(now),
                    timestamp = now.time
                )
            )
        }
    }

    fun addExpense(amount: Long, category: String, note: String) {
        viewModelScope.launch {
            val now = Date()
            transactionDao.insert(
                Transaction(
                    type = "expense",
                    amount = amount,
                    category = category,
                    note = note,
                    date = _selectedDate.value,
                    time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(now),
                    timestamp = now.time
                )
            )
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionDao.delete(transaction)
        }
    }

    fun reinsertTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionDao.insert(transaction)
        }
    }

    fun reinsertOrder(order: CustomerOrder) {
        viewModelScope.launch {
            orderDao.insert(order)
        }
    }

    private fun getToday(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}
