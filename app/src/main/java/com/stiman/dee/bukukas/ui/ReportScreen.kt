package com.stiman.dee.bukukas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stiman.dee.bukukas.Transaction
import com.stiman.dee.bukukas.TransactionViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    viewModel: TransactionViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allTransactions by viewModel.allTransactions.collectAsState()
    val totalIncomeAll by viewModel.totalIncomeAll.collectAsState()
    val totalExpenseAll by viewModel.totalExpenseAll.collectAsState()

    var selectedPeriod by remember { mutableIntStateOf(0) } // 0=hari ini, 1=minggu ini, 2=bulan ini, 3=pilih bulan, 4=semua
    var searchQuery by remember { mutableStateOf("") }
    var selectedMonth by remember { mutableStateOf("") } // yyyy-MM format
    var showMonthPicker by remember { mutableStateOf(false) }

    val periods = listOf("Hari Ini", "Minggu Ini", "Bulan Ini", "Pilih Bulan", "Semua")
    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale("id"))
    val displayMonth = if (selectedMonth.isNotBlank()) {
        try {
            val date = SimpleDateFormat("yyyy-MM", Locale.getDefault()).parse(selectedMonth)
            date?.let { monthFormat.format(it) } ?: "Pilih Bulan"
        } catch (e: Exception) {
            "Pilih Bulan"
        }
    } else "Pilih Bulan"

    // Filter transactions by period
    val periodFilteredTransactions = remember(allTransactions, selectedPeriod, selectedMonth) {
        when (selectedPeriod) {
            0 -> filterByToday(allTransactions)
            1 -> filterByThisWeek(allTransactions)
            2 -> filterByThisMonth(allTransactions)
            3 -> if (selectedMonth.isNotBlank()) filterByMonth(allTransactions, selectedMonth) else emptyList()
            else -> allTransactions
        }
    }

    // Further filter by search query
    val filteredTransactions = if (searchQuery.isBlank()) {
        periodFilteredTransactions
    } else {
        periodFilteredTransactions.filter {
            it.note.contains(searchQuery, ignoreCase = true) ||
            it.category.contains(searchQuery, ignoreCase = true)
        }
    }

    val totalIncome = filteredTransactions.filter { it.type == "income" }.sumOf { it.amount }
    val totalExpense = filteredTransactions.filter { it.type == "expense" }.sumOf { it.amount }
    val profit = totalIncome - totalExpense

    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Surface)
            .padding(24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Kembali",
                    tint = TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Laporan Keuangan",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Period selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            periods.forEachIndexed { index, period ->
                val isSelected = selectedPeriod == index
                Button(
                    onClick = {
                        if (index == 3) { // Pilih Bulan
                            showMonthPicker = true
                        } else {
                            selectedPeriod = index
                        }
                    },
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) IncomeGreen else DarkCard,
                        contentColor = if (isSelected) Color.White else TextSecondary
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = if (index == 3 && selectedMonth.isNotBlank()) displayMonth else period,
                        fontSize = if (index == 3 && selectedMonth.isNotBlank()) 10.sp else 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cari catatan/kategori...", color = TextMuted) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BlueAccent,
                unfocusedBorderColor = Slate600,
                focusedContainerColor = DarkCard,
                unfocusedContainerColor = DarkCard,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = BlueAccent
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Summary cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ReportSummaryCard(
                title = "Pemasukan",
                amount = formatter.format(totalIncome),
                color = IncomeGreen,
                icon = Icons.Default.CheckCircle,
                modifier = Modifier.weight(1f)
            )
            ReportSummaryCard(
                title = "Pengeluaran",
                amount = formatter.format(totalExpense),
                color = ExpenseRed,
                icon = Icons.Default.Warning,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Profit card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Laba / Rugi", color = TextSecondary, fontSize = 13.sp)
                    Text(
                        text = formatter.format(profit),
                        color = if (profit >= 0) IncomeGreen else ExpenseRed,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("${filteredTransactions.size} transaksi", color = TextMuted, fontSize = 12.sp)
                    Text(periods[selectedPeriod], color = TextSecondary, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Transaction list header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Slate800)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Detail Transaksi", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text("Jumlah", color = TextMuted, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Transaction list
        if (filteredTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkCard),
                contentAlignment = Alignment.Center
            ) {
                Text("Tidak ada transaksi di periode ini", color = TextMuted, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filteredTransactions) { transaction ->
                    ReportTransactionItem(transaction)
                }
            }
        }
    }

    // Month Picker Dialog
    if (showMonthPicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showMonthPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
                        selectedMonth = sdf.format(Date(millis))
                        selectedPeriod = 3
                    }
                    showMonthPicker = false
                }) {
                    Text("Pilih", color = IncomeGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showMonthPicker = false }) {
                    Text("Batal", color = TextSecondary)
                }
            },
            colors = DatePickerDefaults.colors(containerColor = DarkCard)
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = DarkCard,
                    selectedDayContainerColor = IncomeGreen,
                    todayContentColor = IncomeGreen
                )
            )
        }
    }
}

@Composable
fun ReportSummaryCard(
    title: String,
    amount: String,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Column {
                Text(title, color = TextMuted, fontSize = 11.sp)
                Text(amount, color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ReportTransactionItem(transaction: Transaction) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }
    val isIncome = transaction.type == "income"
    val amountColor = if (isIncome) IncomeGreen else ExpenseRed
    val prefix = if (isIncome) "+" else "-"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.category,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Row {
                    Text(
                        text = "${transaction.date} ${if (transaction.time.length >= 5) transaction.time.substring(0, 5) else transaction.time}",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    if (transaction.note.isNotBlank()) {
                        Text(
                            text = " | ${transaction.note}",
                            color = TextMuted,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }
            }
            Text(
                text = "$prefix${formatter.format(transaction.amount)}",
                color = amountColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// Filter functions
private fun filterByToday(transactions: List<Transaction>): List<Transaction> {
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    return transactions.filter { it.date == today }
}

private fun filterByThisWeek(transactions: List<Transaction>): List<Transaction> {
    val cal = Calendar.getInstance()
    cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    val weekStart = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)

    cal.add(Calendar.DAY_OF_WEEK, 6)
    val weekEnd = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)

    return transactions.filter { it.date in weekStart..weekEnd }
}

private fun filterByThisMonth(transactions: List<Transaction>): List<Transaction> {
    val cal = Calendar.getInstance()
    cal.set(Calendar.DAY_OF_MONTH, 1)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    val monthStart = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)

    val monthPrefix = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
    return transactions.filter { it.date >= monthStart && it.date.startsWith(monthPrefix) }
}

private fun filterByMonth(transactions: List<Transaction>, month: String): List<Transaction> {
    // month format: "yyyy-MM"
    return transactions.filter { it.date.startsWith(month) }
}
