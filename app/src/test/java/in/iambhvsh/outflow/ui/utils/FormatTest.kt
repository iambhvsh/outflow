package `in`.iambhvsh.outflow.ui.utils

import `in`.iambhvsh.outflow.data.Currency
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Calendar

class FormatTest {

    @Before
    fun setUp() {
        currency = Currency.INR
    }

    @Test
    fun testMoneyFormatting() {
        currency = Currency.INR
        assertEquals("₹100", money(100.0))
        assertEquals("−₹50", money(-50.0))

        currency = Currency.USD
        assertEquals("$1,000", money(1000.0))
        assertEquals("−$25.5", money(-25.5))
    }

    @Test
    fun testCompactFormatting() {
        currency = Currency.USD
        assertEquals("$500", compact(500.0))
        assertEquals("$10K", compact(10000.0))
        assertEquals("$1.5M", compact(1500000.0))
        assertEquals("$2B", compact(2000000000.0))

        currency = Currency.INR
        assertEquals("₹500", compact(500.0))
        assertEquals("₹10K", compact(10000.0))
        assertEquals("₹1.5L", compact(150000.0))
        assertEquals("₹1Cr", compact(10000000.0))
    }

    @Test
    fun testSignedFormatting() {
        currency = Currency.INR
        assertEquals("+₹100", signed(100.0, positive = true))
        assertEquals("−₹100", signed(100.0, positive = false))
    }

    @Test
    fun testPercentFormatting() {
        assertEquals("0%", percent(0.0, 100.0))
        assertEquals("50%", percent(50.0, 100.0))
        assertEquals("100%", percent(100.0, 100.0))
        assertEquals("0%", percent(50.0, 0.0))
    }

    @Test
    fun testDateUtils() {
        val now = System.currentTimeMillis()
        val startOfToday = startOfDay(now)
        val calendar = Calendar.getInstance().apply { timeInMillis = startOfToday }

        assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, calendar.get(Calendar.MINUTE))
        assertEquals(0, calendar.get(Calendar.SECOND))
        assertEquals(0, calendar.get(Calendar.MILLISECOND))

        val yesterday = plusDays(startOfToday, -1)
        assertEquals(1, daysAgo(yesterday, now))

        val tomorrow = plusDays(startOfToday, 1)
        assertEquals(-1, daysAgo(tomorrow, now))
    }
}
