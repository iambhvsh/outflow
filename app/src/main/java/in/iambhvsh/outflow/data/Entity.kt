package `in`.iambhvsh.outflow.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * One flow of money, in or out. Both indices serve the reads in [TransactionDao]: every window is
 * ordered by [timestamp], and the totals sum one [type] within a window.
 */
@Entity(
    tableName = "outflows",
    indices = [Index("timestamp"), Index("type", "timestamp")]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val timestamp: Long,
    val type: TransactionType = TransactionType.OUTFLOW
)
