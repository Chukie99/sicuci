package com.stiman.dee.bukukas

data class ServiceOption(
    val name: String,
    val price: Long,
    val description: String
)

val motorTypes = listOf(
    "Matic",
    "Manual",
    "Bebek",
    "Sport",
    "Cub"
)

val defaultServiceOptions = listOf(
    ServiceOption("Cuci Biasa", 10000, "Cuci motor standar"),
    ServiceOption("Cuci + Semir", 15000, "Cuci dan semir ban/body"),
    ServiceOption("Cuci Detail", 20000, "Cuci detail termasuk mesin"),
    ServiceOption("Cuci + Busa", 12000, "Cuci dengan busa salju"),
    ServiceOption("Semir Saja", 5000, "Semir ban dan body saja")
)

// Dynamic service options with saved prices
val serviceOptions: List<ServiceOption>
    get() = PriceManager.getServicesWithPrices()
