package com.stiman.dee.bukukas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

val incomeCategories = listOf(
    "Jasa Cuci",
    "Jasa Semir",
    "Jasa Service",
    "Jasa Tambal Ban",
    "Lain-lain"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeDialog(
    onDismiss: () -> Unit,
    onConfirm: (amount: Long, category: String, note: String) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(incomeCategories[0]) }
    var noteText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

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
                modifier = Modifier.padding(32.dp)
            ) {
                // Header
                Text(
                    text = "Tambah Pemasukan",
                    color = IncomeGreen,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(28.dp))

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
                    placeholder = {
                        Text("Masukkan jumlah", color = TextMuted)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
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
                    textStyle = LocalTextStyle.current.copy(fontSize = 18.sp)
                )

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
                            focusedBorderColor = IncomeGreen,
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
                        incomeCategories.forEach { category ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = category,
                                        color = TextPrimary,
                                        fontSize = 15.sp
                                    )
                                },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

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
                    placeholder = {
                        Text("Contoh: Vario, Nmax", color = TextMuted)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
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
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextSecondary
                        )
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
                        colors = ButtonDefaults.buttonColors(
                            containerColor = IncomeGreen
                        ),
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
