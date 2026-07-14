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
    var vehicleType by remember { mutableStateOf("Motor") }
    var selectedMotor by remember { mutableStateOf(motorTypes[0]) }
    var selectedMobil by remember { mutableStateOf(mobilTypes[0]) }
    var selectedServiceIndex by remember { mutableIntStateOf(0) }
    var motorExpanded by remember { mutableStateOf(false) }
    var mobilExpanded by remember { mutableStateOf(false) }

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
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Text(
                    text = "Antrian Baru",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Masukkan data kendaraan pelanggan",
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Vehicle Type Selector
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
                        Surface(
                            onClick = {
                                vehicleType = type
                                selectedServiceIndex = 0
                            },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) Primary else SurfaceCardAlt,
                            border = if (isSelected) null else ButtonDefaults.outlinedButtonBorder
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = type,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                    color = if (isSelected) TextOnPrimary else TextSecondary
                                )
                            }
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
                    placeholder = { Text("B 1234 ABC", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Accent,
                        unfocusedBorderColor = Border,
                        focusedContainerColor = Surface,
                        unfocusedContainerColor = SurfaceCardAlt,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = Accent
                    ),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 15.sp)
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
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Border,
                                unfocusedBorderColor = Border,
                                focusedContainerColor = SurfaceCardAlt,
                                unfocusedContainerColor = SurfaceCardAlt,
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
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Border,
                                unfocusedBorderColor = Border,
                                focusedContainerColor = SurfaceCardAlt,
                                unfocusedContainerColor = SurfaceCardAlt,
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

                // Service Type
                Text(
                    text = "Jenis Layanan",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Service grid
                val chunked = filteredServices.chunked(2)
                chunked.forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { service ->
                            val index = filteredServices.indexOf(service)
                            val isSelected = selectedServiceIndex == index
                            Surface(
                                onClick = { selectedServiceIndex = index },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Primary else SurfaceCardAlt,
                                border = if (isSelected) null else ButtonDefaults.outlinedButtonBorder
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = service.name,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                        color = if (isSelected) TextOnPrimary else TextSecondary,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = formatter.format(service.price),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) TextOnPrimary else Success
                                    )
                                }
                            }
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
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
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                    ) {
                        Text("Batal", fontSize = 14.sp)
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
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Accent),
                        enabled = plateText.isNotBlank() && filteredServices.isNotEmpty()
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tambah", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
