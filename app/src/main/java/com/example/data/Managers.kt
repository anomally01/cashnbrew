package com.example.data

import com.example.model.CartItem
import com.example.model.Product
import com.example.model.Transaction
import com.example.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object AuthManager {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // Valid PIN codes
    private val validPins = setOf("1234", "123456", "0000")

    fun loginWithPin(pin: String): Boolean {
        if (validPins.contains(pin)) {
            _currentUser.value = User(username = "admin", role = "Cashier", name = "Staff Member")
            _isLoggedIn.value = true
            return true
        }
        return false
    }

    fun login(username: String, password: String): Boolean {
        if (username.trim() == "admin" && password == "123456") {
            _currentUser.value = User(username = username, role = "Store Manager", name = "Manager Admin")
            _isLoggedIn.value = true
            return true
        }
        return false
    }

    fun logout() {
        _currentUser.value = null
        _isLoggedIn.value = false
    }
}

object ProductRepository {
    private val products = listOf(
        Product(
            id = "prod_1",
            name = "Cappuccino",
            category = "Coffee",
            price = 20000.0,
            image = "https://lh3.googleusercontent.com/aida-public/AB6AXuAEyhlE1033BJDJAT2cg3bW5tPC4Nfz5Xy4g8qgeqjpq1QoTOTBHdIc2lfXlWpy1in68HobGK2eMbdLXzkUU9aXfCVty5_NDyA6p-NS0NJ1Cc6jwZSNqLvZpYene3l4H9e87El2UUD-Lv8DZsntt7FjNw9j32y16PMA79O1RYsoFbUtyVIMEJl3-0fQdnCwL5gT4bgfIobztZOLf6TR6JFFOvGu_C_b-_MkwmdLSr3NcwwnlTnR-ojykA",
            description = "A classic Italian espresso-based coffee drink that is traditionally prepared with equal parts double espresso, steamed milk, and steamed milk foam. Offers a rich, bold flavor with a velvety texture.",
            rating = 4.8,
            reviewCount = 120,
            volume = "160ml",
            subtitle = "Signature Espresso",
            availableSizes = listOf("Small", "Medium", "Large")
        ),
        Product(
            id = "prod_2",
            name = "Caffe Latte",
            category = "Coffee",
            price = 22000.0,
            image = "https://lh3.googleusercontent.com/aida-public/AB6AXuAbNcY7Fs7zAMvhWcaTwU9LTn5N3a3Q8OGm9qQPr9oNQGS0xVnZiNFxURDtEx__BVYUxXKR95I7_WPrI5_Q6wU6ZkzktGIF4aSeNIFTu7OH42inUT9p0PYZjRexca6vgyP0QoPI8RMQS_m_d5CDUjccmLdCsoP_zlQFsflP1vDtnKLjt0F0g84iwcLMv0sqYEey_Zj7L0Z2MWJI1fF7Xf0StHA_BHvoP2TDfOY4kkzb1QpFcRumbjUFtw",
            description = "A smooth and creamy blend of freshly pulled espresso shots and steamed whole milk, topped with a delicate layer of silky microfoam art.",
            rating = 4.6,
            reviewCount = 95,
            volume = "240ml",
            subtitle = "Creamy & Smooth",
            availableSizes = listOf("Small", "Medium", "Large")
        ),
        Product(
            id = "prod_3",
            name = "Double Espresso",
            category = "Coffee",
            price = 18500.0,
            image = "https://lh3.googleusercontent.com/aida-public/AB6AXuBRX5wRQzlhyKr-Or-QN6Atss8As0UXq8uvN4pRBfpvWsEttjXeP-HBBPN4CsrezcJNdcj0lVa2GUlijS45WE5pzlZCJUcgN9l-c4lanIXzXBLpb77INGhhrNkrHVos8pyb2X0zQ3ZawGBPV488rU6ZkJFOAX3rblrHmLMPBXSKLoE27aMPBIBoKU2Xz2sY4gTnPb9B3ocKnr8SmgsqcJP-BiyUbepOhKq0CGnhZyIuWYh8h-zIX4fOjw",
            description = "Two concentrated shots of rich espresso extracted under high pressure with a thick, golden-hazelnut crema. Intense, aromatic, and invigorating.",
            rating = 4.9,
            reviewCount = 210,
            volume = "60ml",
            subtitle = "Pure Extract",
            availableSizes = listOf("Single", "Double")
        ),
        Product(
            id = "prod_4",
            name = "Americano",
            category = "Coffee",
            price = 15000.0,
            image = "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=600&auto=format&fit=crop&q=80",
            description = "Rich espresso diluted with hot filtered water, delivering the depth of espresso with the lightness of drip coffee.",
            rating = 4.7,
            reviewCount = 88,
            volume = "200ml",
            subtitle = "Bold & Clean",
            availableSizes = listOf("Small", "Medium", "Large")
        ),
        Product(
            id = "prod_5",
            name = "Cold Brew Caramel",
            category = "Coffee",
            price = 25000.0,
            image = "https://images.unsplash.com/photo-1517701550927-30cf4ba1dba5?w=600&auto=format&fit=crop&q=80",
            description = "Slow-steeped for 18 hours in cold purified water, infused with handcrafted artisanal salted caramel syrup over crystal clear ice.",
            rating = 4.9,
            reviewCount = 145,
            volume = "350ml",
            subtitle = "Slow Brewed",
            availableSizes = listOf("Medium", "Large")
        ),
        Product(
            id = "prod_6",
            name = "Matcha Latte",
            category = "Non Coffee",
            price = 20000.0,
            image = "https://images.unsplash.com/photo-1536256263959-770b48d82b0a?w=600&auto=format&fit=crop&q=80",
            description = "Ceremonial grade Uji Japanese matcha whisked to perfection with silky steamed milk and a touch of organic cane sugar.",
            rating = 4.8,
            reviewCount = 92,
            volume = "240ml",
            subtitle = "Kyoto Ceremonial",
            availableSizes = listOf("Small", "Medium", "Large")
        ),
        Product(
            id = "prod_7",
            name = "Chocolate",
            category = "Non Coffee",
            price = 18000.0,
            image = "https://images.unsplash.com/photo-1542990253-0d0f5be5f0ed?w=600&auto=format&fit=crop&q=80",
            description = "Single-origin dark cocoa melted into steamed creamy milk, topped with cocoa powder dusting. Rich, comforting, and decadent.",
            rating = 4.7,
            reviewCount = 74,
            volume = "240ml",
            subtitle = "Artisanal Cocoa",
            availableSizes = listOf("Small", "Medium", "Large")
        ),
        Product(
            id = "prod_8",
            name = "Croissant",
            category = "Food",
            price = 18000.0,
            image = "https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=600&auto=format&fit=crop&q=80",
            description = "Freshly baked French butter pastry with flaky, golden layers and a tender, buttery honeycomb interior. Served warmed.",
            rating = 4.9,
            reviewCount = 160,
            volume = "1 pc",
            subtitle = "French Butter Pastry",
            availableSizes = listOf("Standard")
        ),
        Product(
            id = "prod_9",
            name = "French Fries",
            category = "Food",
            price = 15000.0,
            image = "https://images.unsplash.com/photo-1576107232684-1279f3908594?w=600&auto=format&fit=crop&q=80",
            description = "Crispy golden potato strips seasoned with aromatic sea salt and rosemary herbs, served with homemade garlic aioli.",
            rating = 4.6,
            reviewCount = 80,
            volume = "1 portion",
            subtitle = "Crispy & Herbed",
            availableSizes = listOf("Regular", "Large")
        ),
        Product(
            id = "prod_10",
            name = "Chicken Burger",
            category = "Food",
            price = 25000.0,
            image = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=600&auto=format&fit=crop&q=80",
            description = "Crispy buttermilk fried chicken patty with fresh lettuce, melted cheddar cheese, and signature café sauce on a toasted brioche bun.",
            rating = 4.8,
            reviewCount = 115,
            volume = "1 burger",
            subtitle = "Brioche Special",
            availableSizes = listOf("Standard")
        )
    )

    fun getAllProducts(): List<Product> = products

    fun getProductById(id: String): Product? = products.find { it.id == id }

    fun getCategories(): List<String> = listOf("All", "Coffee", "Non Coffee", "Food")

    fun getProductsByCategory(category: String): List<Product> {
        if (category == "All" || category.isBlank()) return products
        return products.filter { it.category.equals(category, ignoreCase = true) }
    }
}

object CartManager {
    private val _cartItems = MutableStateFlow<List<CartItem>>(
        // Pre-populate with sample items so Cart is visually ready if opened immediately
        listOf(
            CartItem(
                product = ProductRepository.getAllProducts()[0], // Cappuccino
                quantity = 2,
                size = "Medium",
                notes = "Whole Milk"
            ),
            CartItem(
                product = ProductRepository.getAllProducts()[2], // Double Espresso
                quantity = 1,
                size = "Single",
                notes = "Dark Roast"
            )
        )
    )
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    fun addToCart(product: Product, quantity: Int = 1, size: String = "Medium", notes: String = "") {
        val currentList = _cartItems.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.product.id == product.id && it.size == size }
        
        if (existingIndex >= 0) {
            val existing = currentList[existingIndex]
            currentList[existingIndex] = existing.copy(quantity = existing.quantity + quantity)
        } else {
            currentList.add(CartItem(product = product, quantity = quantity, size = size, notes = notes))
        }
        _cartItems.value = currentList
    }

    fun increaseQuantity(productId: String, size: String = "") {
        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == productId && (size.isEmpty() || it.size == size) }
        if (index >= 0) {
            val item = currentList[index]
            currentList[index] = item.copy(quantity = item.quantity + 1)
            _cartItems.value = currentList
        }
    }

    fun decreaseQuantity(productId: String, size: String = "") {
        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == productId && (size.isEmpty() || it.size == size) }
        if (index >= 0) {
            val item = currentList[index]
            if (item.quantity > 1) {
                currentList[index] = item.copy(quantity = item.quantity - 1)
                _cartItems.value = currentList
            } else {
                currentList.removeAt(index)
                _cartItems.value = currentList
            }
        }
    }

    fun removeItem(productId: String, size: String = "") {
        val currentList = _cartItems.value.toMutableList()
        currentList.removeAll { it.product.id == productId && (size.isEmpty() || it.size == size) }
        _cartItems.value = currentList
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun getSubtotal(): Double {
        return _cartItems.value.sumOf { it.itemTotal }
    }

    fun getTax(): Double {
        // 8% tax
        return getSubtotal() * 0.08
    }

    fun getTotal(): Double {
        return getSubtotal() + getTax()
    }

    fun getItemCount(): Int {
        return _cartItems.value.sumOf { it.quantity }
    }
}

object TransactionManager {
    private val _transactions = MutableStateFlow<List<Transaction>>(
        listOf(
            Transaction(
                id = "#ORD-8924",
                items = listOf(
                    CartItem(ProductRepository.getAllProducts()[0], 2, "Medium"),
                    CartItem(ProductRepository.getAllProducts()[7], 1, "Standard")
                ),
                total = 58000.0,
                payment = 60000.0,
                change = 2000.0,
                date = "Today, 10:42 AM",
                status = "Completed",
                paymentMethod = "Cash",
                orderSummaryText = "2x Cappuccino, 1x Croissant"
            ),
            Transaction(
                id = "#ORD-8923",
                items = listOf(
                    CartItem(ProductRepository.getAllProducts()[2], 1, "Double")
                ),
                total = 18500.0,
                payment = 20000.0,
                change = 1500.0,
                date = "Today, 10:38 AM",
                status = "Completed",
                paymentMethod = "NFC / Tap",
                orderSummaryText = "1x Double Espresso"
            ),
            Transaction(
                id = "#ORD-8890",
                items = listOf(
                    CartItem(ProductRepository.getAllProducts()[1], 4, "Large"),
                    CartItem(ProductRepository.getAllProducts()[7], 2, "Standard")
                ),
                total = 124000.0,
                payment = 150000.0,
                change = 26000.0,
                date = "Yesterday, 04:30 PM",
                status = "Completed",
                paymentMethod = "Cash",
                orderSummaryText = "4x Caffe Latte, 2x Croissant"
            ),
            Transaction(
                id = "#ORD-8885",
                items = listOf(
                    CartItem(ProductRepository.getAllProducts()[5], 2, "Medium"),
                    CartItem(ProductRepository.getAllProducts()[8], 1, "Regular")
                ),
                total = 55000.0,
                payment = 55000.0,
                change = 0.0,
                date = "Yesterday, 02:15 PM",
                status = "Completed",
                paymentMethod = "Card",
                orderSummaryText = "2x Matcha Latte, 1x French Fries"
            )
        )
    )
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private var orderSequence = 8925

    fun createTransaction(
        items: List<CartItem>,
        total: Double,
        payment: Double,
        change: Double,
        paymentMethod: String = "Cash"
    ): Transaction {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val currentTime = timeFormat.format(Date())
        val newId = "#ORD-$orderSequence"
        orderSequence++

        val transaction = Transaction(
            id = newId,
            items = items.map { it.copy() },
            total = total,
            payment = payment,
            change = change,
            date = "Today, $currentTime",
            status = "Completed",
            paymentMethod = paymentMethod,
            orderSummaryText = items.joinToString(", ") { "${it.quantity}x ${it.product.name}" }
        )

        val updated = _transactions.value.toMutableList()
        updated.add(0, transaction)
        _transactions.value = updated
        return transaction
    }

    fun getTodaySales(): Double {
        return _transactions.value
            .filter { it.date.startsWith("Today") && it.status == "Completed" }
            .sumOf { it.total }
    }

    fun getTotalOrders(): Int {
        return _transactions.value.size
    }

    fun getAvgTicket(): Double {
        val total = _transactions.value.filter { it.status == "Completed" }.sumOf { it.total }
        val count = _transactions.value.count { it.status == "Completed" }
        return if (count > 0) total / count else 0.0
    }
}
