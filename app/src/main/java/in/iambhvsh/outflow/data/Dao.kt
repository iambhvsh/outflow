package `in`.iambhvsh.outflow.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert
    fun insert(transaction: TransactionEntity): Long

    @Update
    fun update(transaction: TransactionEntity): Int

    @Delete
    fun delete(transaction: TransactionEntity): Int

    @Query("SELECT * FROM outflows ORDER BY timestamp DESC, id DESC LIMIT :limit")
    fun window(limit: Int): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM outflows WHERE timestamp >= :from ORDER BY timestamp DESC, id DESC")
    fun since(from: Long): Flow<List<TransactionEntity>>

    @Query("SELECT COUNT(*) FROM outflows")
    fun count(): Flow<Int>

    @Query(
        """
        SELECT
            COALESCE(SUM(CASE WHEN type = 'INFLOW' THEN amount END), 0.0) AS inflow,
            COALESCE(SUM(CASE WHEN type = 'OUTFLOW' THEN amount END), 0.0) AS outflow
        FROM outflows
        WHERE timestamp >= :from
        """
    )
    fun totals(from: Long): Flow<Totals>

    @Query(
        """
        SELECT category AS category, SUM(amount) AS amount, COUNT(*) AS count
        FROM outflows
        WHERE type = 'OUTFLOW' AND timestamp >= :from
        GROUP BY category
        ORDER BY amount DESC
        """
    )
    fun splits(from: Long): Flow<List<Split>>

    @Query("DELETE FROM outflows")
    fun clear(): Int

    @Query("SELECT * FROM outflows ORDER BY timestamp ASC, id ASC")
    fun all(): List<TransactionEntity>

    @Insert
    fun insertAll(transactions: List<TransactionEntity>): List<Long>

    @Query(
        """
        SELECT COUNT(*) FROM outflows
        WHERE title = :title
          AND ABS(amount - :amount) < 0.005
          AND timestamp BETWEEN :from AND :to
        """
    )
    fun countSimilar(title: String, amount: Double, from: Long, to: Long): Int
}
