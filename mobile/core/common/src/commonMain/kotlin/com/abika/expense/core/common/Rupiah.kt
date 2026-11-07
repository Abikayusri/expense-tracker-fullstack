package com.abika.expense.core.common

/** 1250000 -> "Rp1.250.000", -150000 -> "-Rp150.000". */
fun formatRupiah(amount: Long): String {
    val digits = amount.toString().removePrefix("-")
    val grouped = digits.reversed().chunked(3).joinToString(".").reversed()
    return (if (amount < 0) "-" else "") + "Rp" + grouped
}
