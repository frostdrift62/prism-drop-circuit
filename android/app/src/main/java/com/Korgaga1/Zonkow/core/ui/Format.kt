package com.Korgaga1.Zonkow.core.ui

fun formatCharge(value: Int): String {
    val digits = value.toString()
    if (digits.length <= 3) {
        return digits
    }
    val builder = StringBuilder()
    val firstGroup = digits.length % 3
    if (firstGroup > 0) {
        builder.append(digits, 0, firstGroup)
    }
    var index = firstGroup
    while (index < digits.length) {
        if (builder.isNotEmpty()) {
            builder.append(' ')
        }
        builder.append(digits, index, index + 3)
        index += 3
    }
    return builder.toString()
}
