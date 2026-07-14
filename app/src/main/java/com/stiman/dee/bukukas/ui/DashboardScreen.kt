package com.stiman.dee.bukukas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stiman.dee.bukukas.Transaction
import com.stiman.dee.bukukas.TransactionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: TransactionViewModel,
    onNavigateBack: () -> Unit = {},
    onDownloadCsv: () -> Unit = {}
) {
    val transactions by viewModel.transactions.collectAsState()
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val totalIncomeAll by viewModel.totalIncomeAll.collectAsState()
    val totalExpenseAll by viewModel.totalExpenseAll.collectAsState()
    val saldo = totalIncome - totalExpense
    val saldoAll = totalIncomeAll - totalExpenseAll

    var showIncomeDialog by remember { mutableStateOf(false) }
    var showExpenseDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Transaction?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            // Header
            HeaderSection(
                selectedDate = selectedDate,
                onNavigateBack = onNavigateBack,
                onDownloadClick = onDownloadCsv,
                onDateClick = { showDatePicker = true }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2-column layout
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Left Column - Summary & Actions
                Column(
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    SaldoCard(saldo = saldo, label = "SALDO HARI INI")

                    // Overall summary card
                    OverallSummaryCard(
                        totalIncomeAll = totalIncomeAll,
                        totalExpenseAll = totalExpenseAll,
                        saldoAll = saldoAll
                    )

                    ActionButtons(
                        onIncomeClick = { showIncomeDialog = true },
                        onExpenseClick = { showExpenseDialog = true }
                    )
                    SummaryCards(
                        totalIncome = totalIncome,
                        totalExpense = totalExpense
                    )
                    WeeklyChart(allTransactions = allTransactions)
                }

                // Right Column - Transaction Log
                Column(
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxHeight()
                ) {
                    TransactionLogHeader()
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        TransactionList(
                            transactions = transactions,
                            onDeleteClick = { showDeleteDialog = it }
                        )
                    }
                }
            }
        }
    }

    if (showIncomeDialog) {
        IncomeDialog(
            onDismiss = { showIncomeDialog = false },
            onConfirm = { amount, category, note ->
                viewModel.addIncome(amount, category, note)
                showIncomeDialog = false
            }
        )
    }

    if (showExpenseDialog) {
        ExpenseDialog(
            onDismiss = { showExpenseDialog = false },
            onConfirm = { amount, category, note ->
                viewModel.addExpense(amount, category, note)
                showExpenseDialog = false
            }
        )
    }

    showDeleteDialog?.let { transaction ->
        DeleteConfirmDialog(
            transaction = transaction,
            onDismiss = { showDeleteDialog = null },
            onConfirm = {
                viewModel.deleteTransaction(transaction)
                showDeleteDialog = null
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "Transaksi dihapus",
                        actionLabel = "Urungkan",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.reinsertTransaction(transaction)
                    }
                }
            }
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        viewModel.setSelectedDate(sdf.format(Date(millis)))
                    }
                    showDatePicker = false
                }) {
                    Text("OK", color = IncomeGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
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
fun HeaderSection(
    selectedDate: String,
    onNavigateBack: () -> Unit = {},
    onDownloadClick: () -> Unit,
    onDateClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
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
            Column {
                Text(
                    text = "SiCuci",
                    color = TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Kasir Digital Cuci Motor & Mobil",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date picker button
            Button(
                onClick = onDateClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Slate700,
                    contentColor = AccentWhite
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatDateDisplay(selectedDate),
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = onDownloadClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Slate700,
                    contentColor = AccentWhite
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Icon(
                    Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Export CSV", fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun SaldoCard(saldo: Long, label: String = "SALDO HARI INI") {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = formatter.format(saldo),
                color = if (saldo >= 0) IncomeGreen else ExpenseRed,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = SimpleDateFormat("dd MMMM yyyy", Locale("id")).format(Date()),
                    color = TextMuted,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun OverallSummaryCard(
    totalIncomeAll: Long,
    totalExpenseAll: Long,
    saldoAll: Long
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "TOTAL KESELURUHAN",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Masuk", color = TextMuted, fontSize = 12.sp)
                    Text(
                        text = formatter.format(totalIncomeAll),
                        color = IncomeGreen,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column {
                    Text("Keluar", color = TextMuted, fontSize = 12.sp)
                    Text(
                        text = formatter.format(totalExpenseAll),
                        color = ExpenseRed,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column {
                    Text("Saldo", color = TextMuted, fontSize = 12.sp)
                    Text(
                        text = formatter.format(saldoAll),
                        color = if (saldoAll >= 0) IncomeGreen else ExpenseRed,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ActionButtons(
    onIncomeClick: () -> Unit,
    onExpenseClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ActionButton(
            text = "+ Income",
            icon = Icons.Default.Add,
            gradientColors = listOf(IncomeGreen, IncomeGreenDark),
            modifier = Modifier.weight(1f),
            onClick = onIncomeClick
        )
        ActionButton(
            text = "- Expense",
            icon = Icons.Default.Remove,
            gradientColors = listOf(ExpenseRed, ExpenseRedDark),
            modifier = Modifier.weight(1f),
            onClick = onExpenseClick
        )
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(72.dp),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(gradientColors),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun SummaryCards(totalIncome: Long, totalExpense: Long) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SummaryCard(
            title = "Total Pemasukan",
            amount = formatter.format(totalIncome),
            color = IncomeGreen,
            icon = Icons.Default.CheckCircle,
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            title = "Total Pengeluaran",
            amount = formatter.format(totalExpense),
            color = ExpenseRed,
            icon = Icons.Default.Warning,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SummaryCard(
    title: String,
    amount: String,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = TextSecondary,
                fontSize = 13.sp
            )
            Text(
                text = amount,
                color = color,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TransactionLogHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Slate800)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Transaksi",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Waktu",
            color = TextMuted,
            fontSize = 13.sp
        )
    }
}

@Composable
fun TransactionList(
    transactions: List<Transaction>,
    onDeleteClick: (Transaction) -> Unit = {}
) {
    if (transactions.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkCard),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Belum ada transaksi",
                    color = TextMuted,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap \"+ Income\" atau \"- Expense\" untuk memulai",
                    color = TextMuted,
                    fontSize = 13.sp
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(transactions) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    onDeleteClick = { onDeleteClick(transaction) }
                )
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    onDeleteClick: () -> Unit = {}
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }
    val isIncome = transaction.type == "income"
    val amountColor = if (isIncome) IncomeGreen else ExpenseRed
    val amountPrefix = if (isIncome) "+" else "-"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Icon + Category + Note
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isIncome) IncomeGreen.copy(alpha = 0.15f)
                            else ExpenseRed.copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isIncome) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = amountColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transaction.category,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (transaction.note.isNotBlank()) {
                        Text(
                            text = transaction.note,
                            color = TextMuted,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Right side: Time + Amount + Delete
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (transaction.time.length >= 5) transaction.time.substring(0, 5) else transaction.time, // HH:mm
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "$amountPrefix${formatter.format(transaction.amount)}",
                        color = amountColor,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmDialog(
    transaction: Transaction,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = {
            Text(
                text = "Hapus Transaksi?",
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "${transaction.category} - ${formatter.format(transaction.amount)}",
                    color = TextSecondary
                )
                if (transaction.note.isNotBlank()) {
                    Text(
                        text = transaction.note,
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Hapus", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = TextSecondary)
            }
        }
    )
}

private fun formatDateDisplay(dateStr: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("id"))
        val date = inputFormat.parse(dateStr) ?: return dateStr
        outputFormat.format(date)
    } catch (e: Exception) {
        dateStr
    }
}

@Composable
fun WeeklyChart(allTransactions: List<Transaction>) {
    val dayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dayNames = listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")

    // Get last 7 days data
    val chartData = remember(allTransactions) {
        val cal = Calendar.getInstance()
        val data = mutableListOf<Triple<String, Long, Long>>() // dayName, income, expense

        for (i in 6 downTo 0) {
            val tempCal = Calendar.getInstance()
            tempCal.add(Calendar.DAY_OF_YEAR, -i)
            val dateStr = dayFormat.format(tempCal.time)
            val dayIndex = tempCal.get(Calendar.DAY_OF_WEEK) - 1
            val dayName = dayNames[dayIndex]

            val dayIncome = allTransactions
                .filter { it.date == dateStr && it.type == "income" }
                .sumOf { it.amount }
            val dayExpense = allTransactions
                .filter { it.date == dateStr && it.type == "expense" }
                .sumOf { it.amount }

            data.add(Triple(dayName, dayIncome, dayExpense))
        }
        data
    }

    val maxValue = chartData.maxOf { maxOf(it.second, it.third) }.coerceAtLeast(1)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = "Grafik 7 Hari",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Chart bars
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                chartData.forEach { (dayName, income, expense) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Bars
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.height(60.dp)
                        ) {
                            // Income bar
                            Box(
                                modifier = Modifier
                                    .width(8.dp)
                                    .height(if (income > 0) (income.toFloat() / maxValue * 50).dp.coerceAtLeast(4.dp) else 0.dp)
                                    .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                                    .background(IncomeGreen)
                            )
                            // Expense bar
                            Box(
                                modifier = Modifier
                                    .width(8.dp)
                                    .height(if (expense > 0) (expense.toFloat() / maxValue * 50).dp.coerceAtLeast(4.dp) else 0.dp)
                                    .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                                    .background(ExpenseRed)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = dayName,
                            color = TextMuted,
                            fontSize = 9.sp
                        )
                    }
                }
            }

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(IncomeGreen)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Masuk", color = TextMuted, fontSize = 9.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(ExpenseRed)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Keluar", color = TextMuted, fontSize = 9.sp)
            }
        }
    }
}
