package com.stiman.dee.bukukas.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stiman.dee.bukukas.DatabaseBackup
import com.stiman.dee.bukukas.PriceManager
import com.stiman.dee.bukukas.serviceOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        PriceManager.init(context)
    }

    var prices by remember {
        mutableStateOf(
            serviceOptions.associate { it.name to PriceManager.getPrice(it.name) }
        )
    }
    var qrisText by remember { mutableStateOf(PriceManager.getQrisString()) }
    var saved by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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
            Column {
                Text(
                    text = "Pengaturan",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Harga layanan & QRIS",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // QRIS Section
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
                        text = "Kode QRIS",
                        color = BlueAccent,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Masukkan kode/link QRIS untuk pembayaran",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = qrisText,
                        onValueChange = { qrisText = it },
                        placeholder = {
                            Text("Contoh: https://qrisk.com/xxx atau kode QRIS", color = TextMuted)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BlueAccent,
                            unfocusedBorderColor = Slate600,
                            focusedContainerColor = Slate800,
                            unfocusedContainerColor = Slate800,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = BlueAccent
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                        minLines = 2,
                        maxLines = 4
                    )
                }
            }

            // Price Section Header
            Text(
                text = "Harga Layanan",
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Service price list
            serviceOptions.forEach { service ->
                var priceText by remember {
                    mutableStateOf(prices[service.name]?.toString() ?: service.price.toString())
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = service.name,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = service.description,
                            color = TextMuted,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Rp", color = TextSecondary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            OutlinedTextField(
                                value = priceText,
                                onValueChange = { newValue ->
                                    priceText = newValue.filter { it.isDigit() }
                                    val newPrice = priceText.toLongOrNull() ?: 0L
                                    prices = prices.toMutableMap().apply {
                                        put(service.name, newPrice)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = IncomeGreen,
                                    unfocusedBorderColor = Slate600,
                                    focusedContainerColor = Slate800,
                                    unfocusedContainerColor = Slate800,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    cursorColor = IncomeGreen
                                ),
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }

        // Backup/Restore Section
        Spacer(modifier = Modifier.height(16.dp))

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
                    text = "Backup & Restore",
                    color = BlueAccent,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Cadangkan atau pulihkan data transaksi",
                    color = TextMuted,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Backup button
                    Button(
                        onClick = {
                            scope.launch {
                                val result = DatabaseBackup.backupDatabase(context)
                                result.onSuccess { file ->
                                    DatabaseBackup.shareBackup(context, file)
                                    Toast.makeText(context, "Backup berhasil!", Toast.LENGTH_SHORT).show()
                                }.onFailure { e ->
                                    Toast.makeText(context, "Backup gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BlueAccent)
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Backup", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }

                    // Restore button
                    val restoreLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.OpenDocument()
                    ) { uri ->
                        uri?.let {
                            scope.launch {
                                val result = DatabaseBackup.restoreDatabase(context, it)
                                result.onSuccess {
                                    Toast.makeText(context, "Restore berhasil! Silakan restart app.", Toast.LENGTH_LONG).show()
                                }.onFailure { e ->
                                    Toast.makeText(context, "Restore gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            restoreLauncher.launch(arrayOf("application/zip", "application/octet-stream"))
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Gold)
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Restore", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save button
        Button(
            onClick = {
                prices.forEach { (name, price) ->
                    PriceManager.setPrice(name, price)
                }
                PriceManager.setQrisString(qrisText)
                saved = true
                scope.launch {
                    delay(2000)
                    saved = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = IncomeGreen)
        ) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (saved) "Tersimpan!" else "Simpan Semua",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
