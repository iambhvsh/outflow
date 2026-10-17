package `in`.iambhvsh.outflow.ui.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import `in`.iambhvsh.outflow.data.Currency
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs
import kotlin.math.round

var currency: Currency by mutableStateOf(Currency.INR)

private val Decimal = DecimalFormat("0.##", DecimalFormatSymbols(Locale.US))
private val Thousands = DecimalFormat("#,##0.##", DecimalFormatSymbols(Locale.US))
private val Lakhs = DecimalFormat("#,##,##0.##", DecimalFormatSymbols(Locale.US))

private val Grouped: DecimalFormat get() = if (currency == Currency.INR) Lakhs else Thousands

fun money(value: Double): String {
    val magnitude = Grouped.format(abs(value))
    return if (value <= -0.005) "−${currency.symbol}$magnitude" else "${currency.symbol}$magnitude"
}

fun signed(magnitude: Double, positive: Boolean): String =
    (if (positive) "+" else "−") + money(abs(magnitude))

fun compact(value: Double): String {
    val magnitude = abs(value)
    val sign = if (value <= -0.005) "−" else ""
    val scaled = if (currency == Currency.INR) lakhs(magnitude) else thousands(magnitude)
    return sign + currency.symbol + scaled
}

private fun thousands(magnitude: Double): String = when {
    magnitude >= 1_000_000_000 -> Decimal.format(magnitude / 1_000_000_000) + "B"
    magnitude >= 1_000_000 -> Decimal.format(magnitude / 1_000_000) + "M"
    magnitude >= 10_000 -> Decimal.format(magnitude / 1_000) + "K"
    else -> Grouped.format(magnitude)
}

private fun lakhs(magnitude: Double): String = when {
    magnitude >= 10_000_000 -> Decimal.format(magnitude / 10_000_000) + "Cr"
    magnitude >= 100_000 -> Decimal.format(magnitude / 100_000) + "L"
    magnitude >= 10_000 -> Decimal.format(magnitude / 1_000) + "K"
    else -> Grouped.format(magnitude)
}

fun tally(value: Int): String = Grouped.format(value)

fun axis(value: Double): String = compact(round(value))

fun percent(part: Double, total: Double): String =
    if (total <= 0.0) "0%" else "${((part / total) * 100).toInt()}%"

private fun formatter(pattern: String) = SimpleDateFormat(pattern, Locale.getDefault())

fun dayLabel(timestamp: Long): String {
    val days = daysAgo(timestamp)
    return when {
        days == 0 -> "Today"
        days == 1 -> "Yesterday"
        isThisYear(timestamp) -> formatter("EEE, d MMM").format(Date(timestamp))
        else -> formatter("d MMM yyyy").format(Date(timestamp))
    }
}

fun dateLabel(timestamp: Long): String =
    formatter(if (isThisYear(timestamp)) "d MMM" else "d MMM yyyy").format(Date(timestamp))

fun timeLabel(timestamp: Long): String = formatter("h:mm a").format(Date(timestamp)).lowercase(Locale.getDefault())

fun weekdayLabel(timestamp: Long): String = formatter("EEE").format(Date(timestamp))

fun monthLabel(timestamp: Long): String =
    formatter(if (isThisYear(timestamp)) "MMMM" else "MMMM yyyy").format(Date(timestamp))

fun startOfDay(timestamp: Long): Long = calendar(timestamp).apply {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis

fun startOfWeek(timestamp: Long): Long = calendar(startOfDay(timestamp)).apply {
    add(Calendar.DAY_OF_YEAR, -((get(Calendar.DAY_OF_WEEK) - firstDayOfWeek + 7) % 7))
}.timeInMillis

fun startOfMonth(timestamp: Long): Long = calendar(startOfDay(timestamp)).apply {
    set(Calendar.DAY_OF_MONTH, 1)
}.timeInMillis

fun plusDays(timestamp: Long, offset: Int): Long = calendar(startOfDay(timestamp)).apply {
    add(Calendar.DAY_OF_YEAR, offset)
}.timeInMillis

fun daysAgo(timestamp: Long, now: Long = System.currentTimeMillis()): Int {
    val diff = startOfDay(now) - startOfDay(timestamp)
    return round(diff / 86_400_000.0).toInt()
}

private fun isThisYear(timestamp: Long): Boolean =
    calendar(timestamp).get(Calendar.YEAR) == calendar(System.currentTimeMillis()).get(Calendar.YEAR)

private fun calendar(timestamp: Long): Calendar =
    Calendar.getInstance().apply { timeInMillis = timestamp }

/** Shifts [timestamp] to [targetDaysAgo] days ago, keeping the same hour, minute, second, and millisecond. */
fun shiftDay(timestamp: Long, targetDaysAgo: Int): Long {
    val targetDayStart = plusDays(startOfDay(System.currentTimeMillis()), -targetDaysAgo)
    val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
    return Calendar.getInstance().apply {
        timeInMillis = targetDayStart
        set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY))
        set(Calendar.MINUTE, cal.get(Calendar.MINUTE))
        set(Calendar.SECOND, cal.get(Calendar.SECOND))
        set(Calendar.MILLISECOND, cal.get(Calendar.MILLISECOND))
    }.timeInMillis
}

/** Updates the year, month, and day of [originalTimestamp] to match [utcDateMillis], keeping the local time of day. */
fun atDate(originalTimestamp: Long, utcDateMillis: Long): Long {
    val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        timeInMillis = utcDateMillis
    }
    val cal = Calendar.getInstance().apply { timeInMillis = originalTimestamp }
    return Calendar.getInstance().apply {
        timeInMillis = originalTimestamp
        set(Calendar.YEAR, utcCal.get(Calendar.YEAR))
        set(Calendar.MONTH, utcCal.get(Calendar.MONTH))
        set(Calendar.DAY_OF_MONTH, utcCal.get(Calendar.DAY_OF_MONTH))
        set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY))
        set(Calendar.MINUTE, cal.get(Calendar.MINUTE))
        set(Calendar.SECOND, cal.get(Calendar.SECOND))
        set(Calendar.MILLISECOND, cal.get(Calendar.MILLISECOND))
    }.timeInMillis
}

/** Converts a local timestamp's date (year, month, day) to UTC midnight millis for DatePickerState. */
fun localToUtcDateMillis(localTimestamp: Long): Long {
    val cal = Calendar.getInstance().apply { timeInMillis = localTimestamp }
    return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        clear()
        set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
    }.timeInMillis
}
