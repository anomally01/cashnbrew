package com.example.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object CurrencyFormatter {
    private val rupiahFormat: DecimalFormat by lazy {
        val symbols = DecimalFormatSymbols(Locale("id", "ID")).apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        DecimalFormat("#,###", symbols)
    }

    /**
     * Formats a number to Indonesian Rupiah (e.g. Rp 25.000)
     */
    fun formatRupiah(amount: Double): String {
        return "Rp " + rupiahFormat.format(amount)
    }

    /**
     * Formats with standard prefix
     */
    fun format(amount: Double): String {
        return formatRupiah(amount)
    }
}

fun Double.toRupiah(): String = CurrencyFormatter.formatRupiah(this)
