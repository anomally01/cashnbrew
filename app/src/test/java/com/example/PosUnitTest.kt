package com.example

import com.example.data.AuthManager
import com.example.data.CartManager
import com.example.data.ProductRepository
import com.example.data.TransactionManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PosUnitTest {

    @Before
    fun setup() {
        AuthManager.logout()
        CartManager.clearCart()
    }

    @Test
    fun testAuthManager() {
        // Test invalid PIN
        assertFalse(AuthManager.loginWithPin("9999"))
        assertFalse(AuthManager.isLoggedIn.value)

        // Test valid PIN
        assertTrue(AuthManager.loginWithPin("1234"))
        assertTrue(AuthManager.isLoggedIn.value)
        assertNotNull(AuthManager.currentUser.value)

        // Test logout
        AuthManager.logout()
        assertFalse(AuthManager.isLoggedIn.value)

        // Test manager login
        assertTrue(AuthManager.login("admin", "123456"))
        assertTrue(AuthManager.isLoggedIn.value)
    }

    @Test
    fun testProductRepository() {
        val products = ProductRepository.getAllProducts()
        assertTrue(products.isNotEmpty())

        val coffeeList = ProductRepository.getProductsByCategory("Coffee")
        assertTrue(coffeeList.isNotEmpty())

        val cappuccino = ProductRepository.getProductById("prod_1")
        assertNotNull(cappuccino)
        assertEquals("Cappuccino", cappuccino?.name)
    }

    @Test
    fun testCartManagerAndTransactions() {
        val product = ProductRepository.getAllProducts().first()
        CartManager.addToCart(product, quantity = 2, size = "Large")

        assertEquals(1, CartManager.cartItems.value.size)
        assertEquals(2, CartManager.getItemCount())
        assertEquals(product.price * 2, CartManager.getSubtotal(), 0.01)

        val totalWithTax = CartManager.getTotal()
        assertTrue(totalWithTax > CartManager.getSubtotal())

        // Test Transaction Creation
        val tx = TransactionManager.createTransaction(
            items = CartManager.cartItems.value,
            total = totalWithTax,
            payment = 50000.0,
            change = 50000.0 - totalWithTax,
            paymentMethod = "Cash"
        )

        assertNotNull(tx)
        assertEquals("Completed", tx.status)
        assertEquals("Cash", tx.paymentMethod)
        assertTrue(TransactionManager.transactions.value.contains(tx))
    }
}
