package com.abika.expense.home

// Emoji stand in for icons so the app does not pull in the large material-icons artifact.
enum class HomeTab(val label: String, val icon: String) {
    Dashboard("Dashboard", "📊"),
    Transactions("Transaksi", "🧾"),
    Categories("Kategori", "🏷️"),
}
