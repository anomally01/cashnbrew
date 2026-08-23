package com.example.model

data class Product(
    val id: String,
    val name: String,
    val category: String,
    val price: Double,
    val image: String,
    val description: String,
    val rating: Double = 4.8,
    val reviewCount: Int = 120,
    val volume: String = "160ml",
    val subtitle: String = "Signature Espresso",
    val availableSizes: List<String> = listOf("Small", "Medium", "Large"),
)

data class CartItem(
    val product: Product,
    var quantity: Int,
    val size: String = "Medium",
    val notes: String = ""
) {
    val itemTotal: Double
        get() = product.price * quantity
}

data class Transaction(
    val id: String,
    val items: List<CartItem>,
    val total: Double,
    val payment: Double,
    val change: Double,
    val date: String,
    val status: String = "Completed", // "Completed", "Refunded"
    val paymentMethod: String = "Cash", // "Cash", "Card", "NFC / Tap"
    val orderSummaryText: String = items.joinToString(", ") { "${it.quantity}x ${it.product.name}" }
)

data class User(
    val username: String,
    val role: String = "Staff / Cashier",
    val name: String = "Barista Team"
)
