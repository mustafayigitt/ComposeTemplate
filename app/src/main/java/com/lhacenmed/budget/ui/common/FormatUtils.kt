package com.lhacenmed.budget.ui.common

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun formatDate(date: String): String = runCatching {
    val d     = LocalDate.parse(date)
    val today = LocalDate.now()
    when (d) {
        today              -> "Today — ${d.format(DateTimeFormatter.ofPattern("MMM d"))}"
        today.minusDays(1) -> "Yesterday — ${d.format(DateTimeFormatter.ofPattern("MMM d"))}"
        else               -> d.format(DateTimeFormatter.ofPattern("EEE, MMM d"))
    }
}.getOrDefault(date)

@RequiresApi(Build.VERSION_CODES.O)
fun formatTimestamp(raw: String): String = runCatching {
    ZonedDateTime.parse(raw).format(DateTimeFormatter.ofPattern("MMM d, HH:mm"))
}.getOrDefault(raw)

fun Float.format()  = if (this == kotlin.math.floor(this)) this.toInt().toString() else "%.2f".format(this)
fun Double.format() = if (this == kotlin.math.floor(this)) this.toInt().toString() else "%.2f".format(this)
