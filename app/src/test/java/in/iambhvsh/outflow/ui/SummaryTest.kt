package `in`.iambhvsh.outflow.ui

import `in`.iambhvsh.outflow.data.TransactionEntity
import `in`.iambhvsh.outflow.data.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SummaryTest {

    @Test
    fun testSummaryNetAndTotal() {
        val summary = Summary(inflow = 500.0, outflow = 200.0)
        assertEquals(300.0, summary.net, 0.001)
        assertEquals(700.0, summary.total, 0.001)
        assertFalse(summary.isEmpty)

        val emptySummary = Summary(0.0, 0.0)
        assertTrue(emptySummary.isEmpty)
    }
}
