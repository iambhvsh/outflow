package `in`.iambhvsh.outflow.data

/**
 * The shapes aggregate reads come back in. No table has these columns — the queries in
 * [TransactionDao] compute them, so a figure is asked of SQLite rather than worked out over a load.
 */

/** What a window of the ledger adds up to. */
data class Totals(val inflow: Double, val outflow: Double)

/** One category's spending within a window, as the raw stored category name. */
data class Split(val category: String, val amount: Double, val count: Int)
