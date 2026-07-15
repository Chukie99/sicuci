package com.stiman.dee.bukukas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stiman.dee.bukukas.CustomerOrder
import com.stiman.dee.bukukas.TransactionViewModel
import kotlinx.coroutines.launch

@Composable
fun QueueScreen(
    viewModel: TransactionViewModel,
    onShareQueue: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val activeOrders by viewModel.activeOrders.collectAsState()
    val todayPaidOrders by viewModel.todayPaidOrders.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showPaymentDialog by remember { mutableStateOf<CustomerOrder?>(null) }
    var showDeleteDialog by remember { mutableStateOf<CustomerOrder?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Stats
    val waitingCount = activeOrders.count { it.status == "waiting" }
    val washingCount = activeOrders.count { it.status == "washing" }
    val doneCount = activeOrders.count { it.status == "done" }
    val todayRevenue = todayPaidOrders.sumOf { it.servicePrice }

    val filteredOrders = if (searchQuery.isBlank()) {
        activeOrders
    } else {
        activeOrders.filter {
            it.plateNumber.contains(searchQuery, ignoreCase = true) ||
            it.motorType.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Surface,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Accent,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Antrian Baru", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // ═══════════════ DASHBOARD HEADER ═══════════════
            item {
                Surface(
                    color = Primary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
                        // Title row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "SiCuci",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Dashboard Hari Ini",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 13.sp
                                )
                            }

                            // Share button
                            FilledTonalButton(
                                onClick = onShareQueue,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color.White.copy(alpha = 0.15f),
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Share", fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Cards - 2x2 Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            DashboardStatCard(
                                label = "Antre",
                                count = waitingCount,
                                color = StatusWaiting,
                                modifier = Modifier.weight(1f)
                            )
                            DashboardStatCard(
                                label = "Dicuci",
                                count = washingCount,
                                color = StatusWashing,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            DashboardStatCard(
                                label = "Selesai",
                                count = doneCount,
                                color = StatusDone,
                                modifier = Modifier.weight(1f)
                            )
                            DashboardStatCard(
                                label = "Pendapatan",
                                count = todayPaidOrders.size,
                                color = Accent,
                                modifier = Modifier.weight(1f),
                                amount = formatPrice(todayRevenue)
                            )
                        }
                    }
                }
            }

            // ═══════════════ SEARCH BAR ═══════════════
            item {
                Surface(
                    color = SurfaceCard,
                    shadowElevation = 1.dp
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text("Cari plat nomor...", color = TextMuted, fontSize = 14.sp)
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Accent,
                            unfocusedBorderColor = Border,
                            focusedContainerColor = Surface,
                            unfocusedContainerColor = Surface,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = Accent
                        ),
                        singleLine = true
                    )
                }
            }

            // ═══════════════ SECTION TITLE ═══════════════
            if (filteredOrders.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daftar Antrean",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${filteredOrders.size} kendaraan",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // ═══════════════ QUEUE LIST ═══════════════
            if (filteredOrders.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(
                                shape = CircleShape,
                                color = SurfaceCardAlt,
                                modifier = Modifier.size(72.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (searchQuery.isNotBlank()) "Tidak ditemukan" else "Belum ada antrian",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (searchQuery.isNotBlank()) "Plat \"$searchQuery\" tidak ada" else "Tap + untuk registrasi kendaraan baru",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            } else {
                items(filteredOrders) { order ->
                    QueueItemCard(
                        order = order,
                        onStartWashing = { viewModel.startWashing(order) },
                        onFinishWashing = { viewModel.finishWashing(order) },
                        onPay = { showPaymentDialog = order },
                        onDelete = { showDeleteDialog = order },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
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

    // Delete confirmation dialog
    showDeleteDialog?.let { order ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            containerColor = SurfaceCard,
            title = {
                Text("Hapus Antrian?", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Hapus antrian #${order.queueNumber} (${order.plateNumber})?", color = TextSecondary)
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteOrder(order)
                        showDeleteDialog = null
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
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Danger),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Hapus", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Batal", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun DashboardStatCard(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier,
    amount: String? = null
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.12f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            if (amount != null) {
                Text(
                    text = amount,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                Text(
                    text = count.toString(),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun QuickStat(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = count.toString(),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun QueueItemCard(
    order: CustomerOrder,
    onStartWashing: () -> Unit,
    onFinishWashing: () -> Unit,
    onPay: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusConfig = when (order.status) {
        "waiting" -> Triple(StatusWaiting, "Menunggu", "Mulai Cuci")
        "washing" -> Triple(StatusWashing, "Dicuci", "Selesai")
        "done" -> Triple(StatusDone, "Selesai", "Bayar Sekarang")
        else -> Triple(TextMuted, "Dibayar", "")
    }
    val (statusColor, statusText, actionText) = statusConfig

    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = { onDelete() }
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top: Plate + Status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Status color dot
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = order.plateNumber,
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${order.motorType} • ${order.serviceType}",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }

                // Status badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Divider
            Divider(color = Divider, thickness = 1.dp)

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom: Price + Action button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatPrice(order.servicePrice),
                    color = Success,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                when (order.status) {
                    "waiting" -> {
                        Button(
                            onClick = onStartWashing,
                            colors = ButtonDefaults.buttonColors(containerColor = StatusWashing),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(actionText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    "washing" -> {
                        Button(
                            onClick = onFinishWashing,
                            colors = ButtonDefaults.buttonColors(containerColor = StatusDone),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(actionText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    "done" -> {
                        Button(
                            onClick = onPay,
                            colors = ButtonDefaults.buttonColors(containerColor = Accent),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(actionText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Hint text
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Long press untuk hapus",
                color = TextMuted,
                fontSize = 11.sp
            )
        }
    }
}

private fun formatPrice(amount: Long): String {
    return "Rp ${String.format("%,d", amount).replace(",", ".")}"
}
