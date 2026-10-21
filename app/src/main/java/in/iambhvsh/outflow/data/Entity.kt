package `in`.iambhvsh.outflow.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

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
