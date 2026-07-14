package com.stiman.dee.bukukas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.stiman.dee.bukukas.CustomerOrder
import com.stiman.dee.bukukas.TransactionViewModel
import com.stiman.dee.bukukas.motorTypes
import com.stiman.dee.bukukas.serviceOptions
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun QueueScreen(
    viewModel: TransactionViewModel,
    onNavigateToDashboard: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToReport: () -> Unit = {},
    onNavigateToCustomerHistory: () -> Unit = {}
) {
    val activeOrders by viewModel.activeOrders.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showPaymentDialog by remember { mutableStateOf<CustomerOrder?>(null) }
    var showExpenseDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val filteredOrders = if (searchQuery.isBlank()) {
        activeOrders
    } else {
        activeOrders.filter {
            it.plateNumber.contains(searchQuery, ignoreCase = true) ||
            it.motorType.contains(searchQuery, ignoreCase = true)
        }
    }

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
        QueueHeader(
            orderCount = activeOrders.size,
            onAddClick = { showAddDialog = true },
            onExpenseClick = { showExpenseDialog = true },
            onSettingsClick = onNavigateToSettings,
            onReportClick = onNavigateToReport,
            onDashboardClick = onNavigateToDashboard,
            onCustomerHistoryClick = onNavigateToCustomerHistory
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Queue Stats
        QueueStats(orders = activeOrders)

        Spacer(modifier = Modifier.height(20.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cari plat nomor...", color = TextMuted) },
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

        // Queue List
        if (filteredOrders.isEmpty()) {
            Box(modifier = Modifier.weight(1f)) {
                if (searchQuery.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkCard),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Tidak ditemukan",
                                color = TextMuted,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Plat \"$searchQuery\" tidak ada di antrian",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    EmptyQueue()
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredOrders) { order ->
                    QueueItem(
                        order = order,
                        onStartWashing = { viewModel.startWashing(order) },
                        onFinishWashing = { viewModel.finishWashing(order) },
                        onPay = { showPaymentDialog = order },
                        onDelete = {
                            viewModel.deleteOrder(order)
                            scope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = "Antrian #${order.queueNumber} dihapus",
                                    actionLabel = "Urungkan",
                                    duration = SnackbarDuration.Short
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    viewModel.reinsertOrder(order)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
    }

    if (showAddDialog) {
        AddOrderDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { plate, motor, service, price ->
                viewModel.addOrder(plate, motor, service, price)
                showAddDialog = false
            }
        )
    }

    showPaymentDialog?.let { order ->
        PaymentDialog(
            order = order,
            onDismiss = { showPaymentDialog = null },
            onPay = { method, amount ->
                viewModel.processPayment(order, method, amount)
                showPaymentDialog = null
            }
        )
    }

    if (showExpenseDialog) {
        QuickExpenseDialog(
            onDismiss = { showExpenseDialog = false },
            onConfirm = { amount, category, note ->
                viewModel.addExpense(amount, category, note)
                showExpenseDialog = false
            }
        )
    }
}

@Composable
fun QueueHeader(
    orderCount: Int,
    onAddClick: () -> Unit,
    onExpenseClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onReportClick: () -> Unit = {},
    onDashboardClick: () -> Unit,
    onCustomerHistoryClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Antrian Cuci",
                color = TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$orderCount kendaraan dalam antrian",
                color = TextSecondary,
                fontSize = 14.sp
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = onExpenseClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ExpenseRed)
            ) {
                Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Beli Alat", fontSize = 13.sp)
            }

            OutlinedButton(
                onClick = onSettingsClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
            ) {
                Text("Harga", fontSize = 13.sp)
            }

            OutlinedButton(
                onClick = onReportClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BlueAccent)
            ) {
                Text("Laporan", fontSize = 13.sp)
            }

            OutlinedButton(
                onClick = onCustomerHistoryClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Gold)
            ) {
                Text("Pelanggan", fontSize = 13.sp)
            }

            OutlinedButton(
                onClick = onDashboardClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
            ) {
                Text("Keuangan", fontSize = 13.sp)
            }

            OutlinedButton(
                onClick = {
                    // Open Ko-fi link
                    val intent = android.content.Intent(
                        android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse("https://ko-fi.com/chuckie999")
                    )
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5E5B))
            ) {
                Text("☕", fontSize = 14.sp)
            }

            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(containerColor = IncomeGreen),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tambah Antrian", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun QueueStats(orders: List<CustomerOrder>) {
    val waiting = orders.count { it.status == "waiting" }
    val washing = orders.count { it.status == "washing" }
    val done = orders.count { it.status == "done" }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            title = "Menunggu",
            count = waiting,
            color = Gold,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Sedang Dicuci",
            count = washing,
            color = BlueAccent,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Selesai",
            count = done,
            color = IncomeGreen,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(
    title: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                color = color,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                color = TextSecondary,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun EmptyQueue() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkCard),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Belum ada antrian",
                color = TextMuted,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap \"Tambah Antrian\" untuk mulai",
                color = TextMuted,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun QueueItem(
    order: CustomerOrder,
    onStartWashing: () -> Unit,
    onFinishWashing: () -> Unit,
    onPay: () -> Unit,
    onDelete: () -> Unit
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }
    val statusColor = when (order.status) {
        "waiting" -> Gold
        "washing" -> BlueAccent
        "done" -> IncomeGreen
        else -> TextMuted
    }
    val statusText = when (order.status) {
        "waiting" -> "Menunggu"
        "washing" -> "Dicuci"
        "done" -> "Selesai"
        else -> "Dibayar"
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
            // Top row: Queue number + Status + Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Queue number badge
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(statusColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "#${order.queueNumber}",
                            color = statusColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = order.plateNumber,
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${order.motorType} - ${order.serviceType}",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Status chip
                    Surface(
                        color = statusColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = statusText,
                            color = statusColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Price + Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatter.format(order.servicePrice),
                    color = IncomeGreen,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    when (order.status) {
                        "waiting" -> {
                            ActionChip(
                                text = "Mulai Cuci",
                                icon = Icons.Default.PlayArrow,
                                color = BlueAccent,
                                onClick = onStartWashing
                            )
                        }
                        "washing" -> {
                            ActionChip(
                                text = "Selesai",
                                icon = Icons.Default.CheckCircle,
                                color = IncomeGreen,
                                onClick = onFinishWashing
                            )
                        }
                        "done" -> {
                            ActionChip(
                                text = "Bayar",
                                icon = Icons.Default.CheckCircle,
                                color = Gold,
                                onClick = onPay
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActionChip(
    text: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickExpenseDialog(
    onDismiss: () -> Unit,
    onConfirm: (amount: Long, category: String, note: String) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Beli Sabun/Semir") }
    var noteText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val quickExpenses = listOf(
        "Beli Sabun Cuci",
        "Beli Semir Ban",
        "Beli Busa Salju",
        "Beli Sponge/Kain",
        "Bayar Listrik",
        "Bayar Air PDAM",
        "Lain-lain"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkCard)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Catat Pengeluaran",
                    color = ExpenseRed,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Beli kebutuhan / bayar tagihan",
                    color = TextSecondary,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Amount Input
                Text(
                    text = "Jumlah (Rp)",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { c -> c.isDigit() } },
                    placeholder = { Text("Masukkan jumlah", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ExpenseRed,
                        unfocusedBorderColor = Slate600,
                        focusedContainerColor = Slate800,
                        unfocusedContainerColor = Slate800,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = ExpenseRed
                    ),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 18.sp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Quick amount buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(5000L, 10000L, 15000L, 20000L).forEach { amount ->
                        OutlinedButton(
                            onClick = { amountText = amount.toString() },
                            modifier = Modifier.weight(1f).height(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (amountText == amount.toString()) ExpenseRed else TextMuted
                            )
                        ) {
                            Text("${amount/1000}rb", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Category Dropdown
                Text(
                    text = "Kategori",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ExpenseRed,
                            unfocusedBorderColor = Slate600,
                            focusedContainerColor = Slate800,
                            unfocusedContainerColor = Slate800,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        quickExpenses.forEach { category ->
                            DropdownMenuItem(
                                text = {
                                    Text(text = category, color = TextPrimary, fontSize = 15.sp)
                                },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Note Input
                Text(
                    text = "Catatan (Opsional)",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    placeholder = { Text("Keterangan tambahan", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ExpenseRed,
                        unfocusedBorderColor = Slate600,
                        focusedContainerColor = Slate800,
                        unfocusedContainerColor = Slate800,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = ExpenseRed
                    ),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                    ) {
                        Text("Batal", fontSize = 16.sp)
                    }

                    Button(
                        onClick = {
                            val amount = amountText.toLongOrNull() ?: 0L
                            if (amount > 0) {
                                onConfirm(amount, selectedCategory, noteText)
                            }
                        },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed),
                        enabled = amountText.isNotBlank() && (amountText.toLongOrNull() ?: 0L) > 0
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simpan", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
