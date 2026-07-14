package com.stiman.dee.bukukas

data class ServiceOption(
    val name: String,
    val price: Long,
    val description: String,
    val category: String = "motor" // "motor" or "mobil"
)

// Vehicle types
val motorTypes = listOf(
    "Matic",
    "Manual",
    "Bebek",
    "Sport",
    "Cub"
)

val mobilTypes = listOf(
    "Sedan",
    "SUV",
    "MPV",
    "Hatchback",
    "Pickup",
    "Box"
)

// Default services for motor
val defaultMotorServices = listOf(
    ServiceOption("Cuci Biasa", 10000, "Cuci motor standar", "motor"),
    ServiceOption("Cuci + Semir", 15000, "Cuci dan semir ban/body", "motor"),
    ServiceOption("Cuci Detail", 20000, "Cuci detail termasuk mesin", "motor"),
    ServiceOption("Cuci + Busa", 12000, "Cuci dengan busa salju", "motor"),
    ServiceOption("Semir Saja", 5000, "Semir ban dan body saja", "motor")
)

// Default services for mobil
val defaultMobilServices = listOf(
    ServiceOption("Cuci Biasa Mobil", 35000, "Cuci mobil standar luar", "mobil"),
    ServiceOption("Cuci + Wax", 50000, "Cuci dan wax body mobil", "mobil"),
    ServiceOption("Cuci Detail Mobil", 75000, "Cuci detail dalam & luar", "mobil"),
    ServiceOption("Cuci Engine", 40000, "Cuci bagian mesin mobil", "mobil"),
    ServiceOption("Salon Mobil", 150000, "Cuci + salon + semir ban", "mobil")
)

// Combined default services
val defaultServiceOptions = defaultMotorServices + defaultMobilServices

// Dynamic service options with saved prices
val serviceOptions: List<ServiceOption>
    get() = PriceManager.getServicesWithPrices()
