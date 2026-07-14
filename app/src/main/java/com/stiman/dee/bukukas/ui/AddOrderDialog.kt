package com.stiman.dee.bukukas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.stiman.dee.bukukas.motorTypes
import com.stiman.dee.bukukas.mobilTypes
import com.stiman.dee.bukukas.serviceOptions
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrderDialog(
    onDismiss: () -> Unit,
    onConfirm: (plate: String, motor: String, service: String, price: Long) -> Unit
) {
    var plateText by remember { mutableStateOf("") }
    var vehicleType by remember { mutableStateOf("Motor") } // "Motor" or "Mobil"
    var selectedMotor by remember { mutableStateOf(motorTypes[0]) }
    var selectedMobil by remember { mutableStateOf(mobilTypes[0]) }
    var selectedServiceIndex by remember { mutableIntStateOf(0) }
    var motorExpanded by remember { mutableStateOf(false) }
    var mobilExpanded by remember { mutableStateOf(false) }

    // Filter services based on vehicle type
    val filteredServices = serviceOptions.filter {
        it.category == vehicleType.lowercase()
    }

    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }

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
                // Header
                Text(
                    text = "Tambah Antrian",
                    color = IncomeGreen,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Vehicle Type Selector (Motor/Mobil)
                Text(
                    text = "Jenis Kendaraan",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Motor", "Mobil").forEach { type ->
                        val isSelected = vehicleType == type
                        OutlinedButton(
                            onClick = {
                                vehicleType = type
                                selectedServiceIndex = 0
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) IncomeGreen.copy(alpha = 0.15f) else Slate800,
                                contentColor = if (isSelected) IncomeGreen else TextSecondary
                            )
                        ) {
                            Text(
                                text = type,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Plate Number Input
                Text(
                    text = "Plat Nomor",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = plateText,
                    onValueChange = { plateText = it.uppercase() },
                    placeholder = {
                        Text("Contoh: B 1234 ABC", color = TextMuted)
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

                Spacer(modifier = Modifier.height(16.dp))

                // Vehicle Type Dropdown
                Text(
                    text = if (vehicleType == "Motor") "Jenis Motor" else "Jenis Mobil",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))

                if (vehicleType == "Motor") {
                    ExposedDropdownMenuBox(
                        expanded = motorExpanded,
                        onExpandedChange = { motorExpanded = !motorExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedMotor,
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
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = motorExpanded)
                            },
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                        )

                        ExposedDropdownMenu(
                            expanded = motorExpanded,
                            onDismissRequest = { motorExpanded = false }
                        ) {
                            motorTypes.forEach { motor ->
                                DropdownMenuItem(
                                    text = {
                                        Text(text = motor, color = TextPrimary, fontSize = 14.sp)
                                    },
                                    onClick = {
                                        selectedMotor = motor
                                        motorExpanded = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    ExposedDropdownMenuBox(
                        expanded = mobilExpanded,
                        onExpandedChange = { mobilExpanded = !mobilExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedMobil,
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
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = mobilExpanded)
                            },
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                        )

                        ExposedDropdownMenu(
                            expanded = mobilExpanded,
                            onDismissRequest = { mobilExpanded = false }
                        ) {
                            mobilTypes.forEach { mobil ->
                                DropdownMenuItem(
                                    text = {
                                        Text(text = mobil, color = TextPrimary, fontSize = 14.sp)
                                    },
                                    onClick = {
                                        selectedMobil = mobil
                                        mobilExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Service Type - compact chips
                Text(
                    text = "Jenis Layanan",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                // 2-column grid for services
                val chunked = filteredServices.chunked(2)
                chunked.forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { service ->
                            val index = filteredServices.indexOf(service)
                            val isSelected = selectedServiceIndex == index
                            OutlinedButton(
                                onClick = { selectedServiceIndex = index },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(64.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSelected) IncomeGreen.copy(alpha = 0.15f) else Slate800,
                                    contentColor = if (isSelected) IncomeGreen else TextSecondary
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = service.name,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = formatter.format(service.price),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        // Fill empty space if odd number
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Buttons - always visible at bottom
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
                            if (plateText.isNotBlank() && filteredServices.isNotEmpty()) {
                                val vehicleName = if (vehicleType == "Motor") selectedMotor else selectedMobil
                                val service = filteredServices[selectedServiceIndex]
                                onConfirm(plateText, "$vehicleType $vehicleName", service.name, service.price)
                            }
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IncomeGreen),
                        enabled = plateText.isNotBlank() && filteredServices.isNotEmpty()
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tambah", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
