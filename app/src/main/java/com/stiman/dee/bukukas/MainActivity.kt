package com.stiman.dee.bukukas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.stiman.dee.bukukas.ui.BukuKasTheme
import com.stiman.dee.bukukas.ui.CustomerHistoryScreen
import com.stiman.dee.bukukas.ui.DashboardScreen
import com.stiman.dee.bukukas.ui.QueueScreen
import com.stiman.dee.bukukas.ui.ReportScreen
import com.stiman.dee.bukukas.ui.SettingsScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: TransactionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        PriceManager.init(this)
        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        setContent {
            BukuKasTheme {
                var currentScreen by remember { mutableStateOf("queue") }
                val activeOrders by viewModel.activeOrders.collectAsState()

                val bottomNavItems = listOf(
                    BottomNavItem("queue", "Antrian", Icons.Default.Home),
                    BottomNavItem("dashboard", "Keuangan", Icons.Default.DateRange),
                    BottomNavItem("report", "Laporan", Icons.Default.List),
                    BottomNavItem("customer_history", "Pelanggan", Icons.Default.Person),
                    BottomNavItem("settings", "Pengaturan", Icons.Default.Settings),
                )

                Scaffold(
                    bottomBar = {
                        NavigationBar(
                            containerColor = com.stiman.dee.bukukas.ui.SurfaceCard
                        ) {
                            bottomNavItems.forEach { item ->
                                NavigationBarItem(
                                    icon = { Icon(item.icon, contentDescription = item.label) },
                                    label = { Text(item.label, fontSize = 11.sp) },
                                    selected = currentScreen == item.route,
                                    onClick = { currentScreen = item.route },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = com.stiman.dee.bukukas.ui.Primary,
                                        selectedTextColor = com.stiman.dee.bukukas.ui.Primary,
                                        unselectedIconColor = com.stiman.dee.bukukas.ui.TextMuted,
                                        unselectedTextColor = com.stiman.dee.bukukas.ui.TextMuted,
                                        indicatorColor = com.stiman.dee.bukukas.ui.Primary.copy(alpha = 0.1f)
                                    )
                                )
                            }
                        }
                    }
                ) { paddingValues ->
                    when (currentScreen) {
                        "queue" -> {
                            QueueScreen(
                                viewModel = viewModel,
                                onShareQueue = {
                                    shareQueueStatus(activeOrders)
                                },
                                modifier = Modifier.padding(paddingValues)
                            )
                        }
                        "dashboard" -> {
                            val allTransactions by viewModel.allTransactions.collectAsState()
                            DashboardScreen(
                                viewModel = viewModel,
                                onNavigateBack = { currentScreen = "queue" },
                                onDownloadCsv = {
                                    if (allTransactions.isEmpty()) {
                                        Toast.makeText(this@MainActivity, "Belum ada data untuk diexport", Toast.LENGTH_SHORT).show()
                                        return@DashboardScreen
                                    }
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        val file = CsvExporter.exportToCsv(this@MainActivity, allTransactions)
                                        withContext(Dispatchers.Main) {
                                            CsvExporter.shareCsv(this@MainActivity, file)
                                        }
                                    }
                                },
                                modifier = Modifier.padding(paddingValues)
                            )
                        }
                        "settings" -> {
                            SettingsScreen(
                                onNavigateBack = { currentScreen = "queue" }
                            )
                        }
                        "report" -> {
                            ReportScreen(
                                viewModel = viewModel,
                                onNavigateBack = { currentScreen = "queue" },
                                modifier = Modifier.padding(paddingValues)
                            )
                        }
                        "customer_history" -> {
                            CustomerHistoryScreen(
                                viewModel = viewModel,
                                onNavigateBack = { currentScreen = "queue" },
                                modifier = Modifier.padding(paddingValues)
                            )
                        }
                    }
                }
            }
        }
    }

    private data class BottomNavItem(
        val route: String,
        val label: String,
        val icon: ImageVector
    )

    private fun shareQueueStatus(orders: List<CustomerOrder>) {
        val waiting = orders.filter { it.status == "waiting" }
        val washing = orders.filter { it.status == "washing" }
        val done = orders.filter { it.status == "done" }

        val queueText = buildString {
            appendLine("🚿 *SiCuci - Status Antrian*")
            appendLine("━━━━━━━━━━━━━━━━━━━━━")
            appendLine()

            if (orders.isEmpty()) {
                appendLine("✅ *Tidak ada antrian!*")
                appendLine("Langsung datang aja ya!")
            } else {
                appendLine("📋 *Total: ${orders.size} kendaraan*")
                appendLine()

                if (waiting.isNotEmpty()) {
                    appendLine("⏳ *Menunggu (${waiting.size})*")
                    waiting.forEach { order ->
                        appendLine("   #${order.queueNumber} ${order.plateNumber} - ${order.motorType}")
                    }
                    appendLine()
                }

                if (washing.isNotEmpty()) {
                    appendLine("🚿 *Sedang Dicuci (${washing.size})*")
                    washing.forEach { order ->
                        appendLine("   #${order.queueNumber} ${order.plateNumber} - ${order.motorType}")
                    }
                    appendLine()
                }

                if (done.isNotEmpty()) {
                    appendLine("✅ *Selesai (${done.size})*")
                    done.forEach { order ->
                        appendLine("   #${order.queueNumber} ${order.plateNumber} - ${order.motorType}")
                    }
                    appendLine()
                }

                appendLine("━━━━━━━━━━━━━━━━━━━━━")
                appendLine("⏱️ Estimasi tunggu: ~${waiting.size * 15} menit")
                appendLine("(asumsi 15 menit/kendaraan)")
            }

            appendLine()
            appendLine("📍 SiCuci - Cuci Motor & Mobil")
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, queueText)
            putExtra(Intent.EXTRA_SUBJECT, "Status Antrian SiCuci")
        }
        startActivity(Intent.createChooser(intent, "Bagikan Status Antrian"))
    }
}
