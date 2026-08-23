package `in`.iambhvsh.outflow.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    fun insert(transaction: TransactionEntity): Long

    @Delete
    fun delete(transaction: TransactionEntity): Int

    @Query("SELECT * FROM outflows ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>
}
