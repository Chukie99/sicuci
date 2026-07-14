package com.stiman.dee.bukukas.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.stiman.dee.bukukas.CustomerOrder
import com.stiman.dee.bukukas.PriceManager
import com.stiman.dee.bukukas.QrCodeGenerator
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PaymentDialog(
    order: CustomerOrder,
    onDismiss: () -> Unit,
    onPay: (method: String, amount: Long) -> Unit
) {
    var selectedMethod by remember { mutableStateOf("") }
    var cashAmountText by remember { mutableStateOf("") }

    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }
    val change = if (selectedMethod == "cash") {
        val cashAmount = cashAmountText.toLongOrNull() ?: 0L
        cashAmount - order.servicePrice
    } else 0L

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
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Text(
                    text = "Pembayaran",
                    color = Gold,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${order.plateNumber} - ${order.motorType}",
                    color = TextSecondary,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Total
                Text("Total", color = TextSecondary, fontSize = 13.sp)
                Text(
                    text = formatter.format(order.servicePrice),
                    color = IncomeGreen,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Payment Method
                Text("Metode Bayar", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PaymentMethodButton(
                        text = "QRIS",
                        isSelected = selectedMethod == "qris",
                        color = BlueAccent,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedMethod = "qris" }
                    )
                    PaymentMethodButton(
                        text = "Tunai",
                        isSelected = selectedMethod == "cash",
                        color = IncomeGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedMethod = "cash" }
                    )
                }

                // QRIS section - real QR code
                if (selectedMethod == "qris") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Slate800),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Scan QR Code ini", color = TextSecondary, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Real QR code
                            val qrisString = remember { PriceManager.getQrisString() }

                            if (qrisString.isNotBlank()) {
                                val qrBitmap = remember(qrisString) {
                                    QrCodeGenerator.generate(qrisString, 400)
                                }
                                Image(
                                    bitmap = qrBitmap.asImageBitmap(),
                                    contentDescription = "QR Code QRIS",
                                    modifier = Modifier
                                        .size(200.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                            } else {
                                // Fallback - no QRIS set
                                Box(
                                    modifier = Modifier
                                        .size(200.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("QRIS", color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            "Belum diatur",
                                            color = Color.Gray,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            "Set di menu Harga",
                                            color = Color.Gray,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("SiCuci", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Cash section
                if (selectedMethod == "cash") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Slate800),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text("Jumlah Diterima", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = cashAmountText,
                                onValueChange = { cashAmountText = it.filter { c -> c.isDigit() } },
                                placeholder = { Text("Masukkan jumlah", color = TextMuted) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = IncomeGreen,
                                    unfocusedBorderColor = Slate600,
                                    focusedContainerColor = DarkCard,
                                    unfocusedContainerColor = DarkCard,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    cursorColor = IncomeGreen
                                ),
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            // Quick amount buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(order.servicePrice, order.servicePrice + 5000, order.servicePrice + 10000).forEach { amount ->
                                    OutlinedButton(
                                        onClick = { cashAmountText = amount.toString() },
                                        modifier = Modifier.weight(1f).height(36.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(0.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = if (cashAmountText == amount.toString()) IncomeGreen else TextMuted
                                        )
                                    ) {
                                        Text(formatter.format(amount), fontSize = 11.sp)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Kembalian:", color = TextSecondary, fontSize = 14.sp)
                                Text(
                                    text = if (change >= 0) formatter.format(change) else "Kurang ${formatter.format(-change)}",
                                    color = if (change >= 0) IncomeGreen else ExpenseRed,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                    ) {
                        Text("Batal", fontSize = 15.sp)
                    }

                    Button(
                        onClick = {
                            when (selectedMethod) {
                                "qris" -> onPay("qris", order.servicePrice)
                                "cash" -> {
                                    val amount = cashAmountText.toLongOrNull() ?: 0L
                                    if (amount >= order.servicePrice) {
                                        onPay("cash", amount)
                                    }
                                }
                            }
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Gold),
                        enabled = when (selectedMethod) {
                            "qris" -> true
                            "cash" -> {
                                val amount = cashAmountText.toLongOrNull() ?: 0L
                                amount >= order.servicePrice
                            }
                            else -> false
                        }
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Bayar", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodButton(
    text: String,
    isSelected: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.height(60.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.2f) else Slate800
        ),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (isSelected) color else TextSecondary,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
