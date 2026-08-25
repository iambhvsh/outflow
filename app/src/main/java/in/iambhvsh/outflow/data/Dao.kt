package `in`.iambhvsh.outflow.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/** Every read the app makes. The list is windowed and everything else is added up in SQL. */
@Dao
interface TransactionDao {

    @Insert
    fun insert(transaction: TransactionEntity): Long

    @Update
    fun update(transaction: TransactionEntity): Int

    @Delete
    fun delete(transaction: TransactionEntity): Int

    /** The newest [limit] flows. `id` breaks ties so flows saved in the same millisecond hold order. */
    @Query("SELECT * FROM outflows ORDER BY timestamp DESC, id DESC LIMIT :limit")
    fun window(limit: Int): Flow<List<TransactionEntity>>

    /** Everything from [from] onwards, newest first — a week or a month, never the whole ledger. */
    @Query("SELECT * FROM outflows WHERE timestamp >= :from ORDER BY timestamp DESC, id DESC")
    fun since(from: Long): Flow<List<TransactionEntity>>

    /** How many flows are recorded. */
    @Query("SELECT COUNT(*) FROM outflows")
    fun count(): Flow<Int>

    /** What the flows from [from] onwards add up to. Pass `0` for the whole ledger. */
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

    /** Spending per category from [from] onwards, largest first. */
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
}
