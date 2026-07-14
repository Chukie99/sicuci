package com.stiman.dee.bukukas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
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

                when (currentScreen) {
                    "queue" -> {
                        QueueScreen(
                            viewModel = viewModel,
                            onNavigateToDashboard = { currentScreen = "dashboard" },
                            onNavigateToSettings = { currentScreen = "settings" },
                            onNavigateToReport = { currentScreen = "report" },
                            onNavigateToCustomerHistory = { currentScreen = "customer_history" },
                            onShareQueue = {
                                shareQueueStatus(activeOrders)
                            }
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
                            }
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
                            onNavigateBack = { currentScreen = "queue" }
                        )
                    }
                    "customer_history" -> {
                        CustomerHistoryScreen(
                            viewModel = viewModel,
                            onNavigateBack = { currentScreen = "queue" }
                        )
                    }
                }
            }
        }
    }

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
