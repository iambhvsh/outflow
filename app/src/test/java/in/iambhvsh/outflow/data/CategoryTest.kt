package `in`.iambhvsh.outflow.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CategoryTest {

    @Test
    fun testCategoryOfTransactionType() {
        val outflowCategories = Category.of(TransactionType.OUTFLOW)
        assertTrue(outflowCategories.isNotEmpty())
        assertEquals(Category.OTHER, outflowCategories.last())
        outflowCategories.dropLast(1).forEach {
            assertEquals(TransactionType.OUTFLOW, it.type)
        }

        val inflowCategories = Category.of(TransactionType.INFLOW)
        assertTrue(inflowCategories.isNotEmpty())
        assertEquals(Category.OTHER, inflowCategories.last())
        inflowCategories.dropLast(1).forEach {
            assertEquals(TransactionType.INFLOW, it.type)
        }
    }

    @Test
    fun testCategoryOfRawString() {
        assertEquals(Category.FOOD, Category.of("FOOD"))
        assertEquals(Category.FOOD, Category.of("food"))
        assertEquals(Category.FOOD, Category.of("Food"))
        assertEquals(Category.SALARY, Category.of("Salary"))
        assertEquals(Category.OTHER, Category.of(null))
        assertEquals(Category.OTHER, Category.of(""))
        assertEquals(Category.OTHER, Category.of("NonExistentCategory"))
    }
}
