package com.stiman.dee.bukukas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stiman.dee.bukukas.CustomerOrder
import com.stiman.dee.bukukas.TransactionViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CustomerHistoryScreen(
    viewModel: TransactionViewModel,
    onNavigateBack: () -> Unit
) {
    val customerHistory by viewModel.customerHistory.collectAsState()
    val searchPlate by viewModel.searchPlateNumber.collectAsState()
    var searchQuery by remember { mutableStateOf(searchPlate) }

    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }

    // Calculate stats
    val totalSpent = customerHistory.filter { it.status == "paid" }.sumOf { it.servicePrice }
    val totalVisits = customerHistory.filter { it.status == "paid" }.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
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
                text = "Riwayat Pelanggan",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.setSearchPlateNumber(it)
            },
            placeholder = { Text("Ketik plat nomor...", color = TextMuted) },
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

        // Stats Card
        if (searchQuery.isNotBlank() && customerHistory.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = BlueAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = searchQuery.uppercase(),
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Total Kunjungan", color = TextMuted, fontSize = 11.sp)
                        Text(
                            text = "$totalVisits kali",
                            color = IncomeGreen,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Total Belanja", color = TextMuted, fontSize = 11.sp)
                        Text(
                            text = formatter.format(totalSpent),
                            color = IncomeGreen,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Transaction List Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Slate800)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Riwayat", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text("Status", color = TextMuted, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Transaction List
        if (customerHistory.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkCard),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchQuery.isBlank()) "Ketik plat nomor untuk melihat riwayat" else "Tidak ada riwayat untuk plat ini",
                    color = TextMuted,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(customerHistory) { order ->
                    CustomerHistoryItem(order = order, formatter = formatter)
                }
            }
        }
    }
}

@Composable
fun CustomerHistoryItem(order: CustomerOrder, formatter: NumberFormat) {
    val statusColor = when (order.status) {
        "paid" -> IncomeGreen
        "done" -> Gold
        "washing" -> BlueAccent
        else -> TextMuted
    }
    val statusText = when (order.status) {
        "paid" -> "Lunas"
        "done" -> "Selesai"
        "washing" -> "Dicuci"
        "waiting" -> "Antri"
        else -> order.status
    }

    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id"))
    val dateStr = try {
        dateFormat.format(Date(order.createdAt))
    } catch (e: Exception) {
        "-"
    }

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
                    text = "${order.motorType} - ${order.serviceType}",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$dateStr | #${order.queueNumber}",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatter.format(order.servicePrice),
                    color = IncomeGreen,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = statusText,
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}
