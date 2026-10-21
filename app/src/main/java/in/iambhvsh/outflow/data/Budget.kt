package `in`.iambhvsh.outflow.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * A monthly spending target for a single outflow category.
 */
@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey
    val category: String,
    val limit: Double
)

/**
 * Combines a configured budget with month-to-date spending.
 */
data class BudgetProgress(
    val category: Category,
    val limit: Double,
    val spent: Double
) {
    val remaining: Double get() = (limit - spent).coerceAtLeast(0.0)
    val ratio: Float get() = if (limit > 0.0) (spent / limit).toFloat() else 0f
    val isExceeded: Boolean get() = spent > limit
    val isNearLimit: Boolean get() = ratio >= 0.8f && !isExceeded
}

@Dao
interface BudgetDao {

    @Query("SELECT * FROM budgets ORDER BY category ASC")
    fun all(): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets")
    fun allSync(): List<BudgetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setBudget(budget: BudgetEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(budgets: List<BudgetEntity>): List<Long>

    @Delete
    fun delete(budget: BudgetEntity): Int

    @Query("DELETE FROM budgets WHERE category = :category")
    fun deleteByCategory(category: String): Int

    @Query("DELETE FROM budgets")
    fun clear(): Int
}
