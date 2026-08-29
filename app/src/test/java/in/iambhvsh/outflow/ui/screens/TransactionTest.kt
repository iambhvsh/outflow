package `in`.iambhvsh.outflow.ui.screens

import `in`.iambhvsh.outflow.data.Category
import `in`.iambhvsh.outflow.data.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Test

class TransactionTest {

    @Test
    fun testRetypeSameType() {
        assertEquals(Category.FOOD, retype(Category.FOOD, TransactionType.OUTFLOW))
        assertEquals(Category.SALARY, retype(Category.SALARY, TransactionType.INFLOW))
    }

    @Test
    fun testRetypeDifferentType() {
        // FOOD is OUTFLOW, retyping to INFLOW should return first INFLOW category (SALARY)
        assertEquals(Category.SALARY, retype(Category.FOOD, TransactionType.INFLOW))
        // SALARY is INFLOW, retyping to OUTFLOW should return first OUTFLOW category (FOOD)
        assertEquals(Category.FOOD, retype(Category.SALARY, TransactionType.OUTFLOW))
    }

    @Test
    fun testRetypeOtherCategoryPreserved() {
        // OTHER category should remain OTHER regardless of flow direction
        assertEquals(Category.OTHER, retype(Category.OTHER, TransactionType.INFLOW))
        assertEquals(Category.OTHER, retype(Category.OTHER, TransactionType.OUTFLOW))
    }
}
