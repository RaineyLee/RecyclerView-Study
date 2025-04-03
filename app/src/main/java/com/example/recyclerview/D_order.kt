package com.example.recyclerview

data class D_order(
    val order_id: String? = null,
    val item_id: String? = null,
    val item_name: String? = null,
    val quantity: String? = null,
    var isChecked: Boolean = false
)
