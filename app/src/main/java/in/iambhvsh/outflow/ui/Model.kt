@file:OptIn(ExperimentalCoroutinesApi::class)

package `in`.iambhvsh.outflow.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import `in`.iambhvsh.outflow.data.AppDatabase
import `in`.iambhvsh.outflow.data.Category
import `in`.iambhvsh.outflow.data.Currency
import `in`.iambhvsh.outflow.data.Options
import `in`.iambhvsh.outflow.data.PreferencesManager
import `in`.iambhvsh.outflow.data.ThemeColor
import `in`.iambhvsh.outflow.data.ThemeMode
import `in`.iambhvsh.outflow.data.Totals
import `in`.iambhvsh.outflow.data.TransactionEntity
import `in`.iambhvsh.outflow.data.TransactionType
import `in`.iambhvsh.outflow.ui.utils.plusDays
import `in`.iambhvsh.outflow.ui.utils.startOfDay
import `in`.iambhvsh.outflow.ui.utils.startOfMonth
import `in`.iambhvsh.outflow.ui.utils.startOfWeek
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** How many flows the list loads at a time. */
private const val Page = 80

/** Money in and out over some window. */
data class Summary(val inflow: Double = 0.0, val outflow: Double = 0.0) {
    val net: Double get() = inflow - outflow
    val total: Double get() = inflow + outflow
    val isEmpty: Boolean get() = total <= 0.0
}

/** One calendar day's worth of transactions. */
data class Day(val timestamp: Long, val transactions: List<TransactionEntity>) {
    val summary: Summary = transactions.summarise()
}

/** One category's share of a window. */
data class Slice(val category: Category, val amount: Double, val count: Int)

private fun List<TransactionEntity>.summarise(): Summary {
    var inflow = 0.0
    var outflow = 0.0
    for (transaction in this) {
        when (transaction.type) {
            TransactionType.INFLOW -> inflow += transaction.amount
            TransactionType.OUTFLOW -> outflow += transaction.amount
        }
    }
    return Summary(inflow, outflow)
}

private val Totals.summary: Summary get() = Summary(inflow, outflow)

/**
 * The whole app's state, and none of it the whole ledger: every figure is one SQLite worked out
 * against an index, and the only rows loaded are the page on screen and the week the chart draws.
 * Every figure starts as null rather than zero, so a screen shows a loader instead of guessing.
 */
class AppModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).transactionDao()
    private val preferences = PreferencesManager(application)

    /** How far back the list currently reaches. Widened by [expand] as it is scrolled. */
    private val reach = MutableStateFlow(Page)

    private fun <T> Flow<T>.share(initial: T): StateFlow<T> =
        flowOn(Dispatchers.Default).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = initial
        )

    /** A window worked out on subscription, not at construction, so an app left open reports the new day. */
    private fun <T> opening(query: (Long) -> Flow<T>, from: () -> Long): Flow<T> =
        flow { emitAll(query(from())) }

    private val window: StateFlow<List<TransactionEntity>?> = reach
        .flatMapLatest { dao.window(it) }
        .share(null)

    /** The loaded flows grouped into days, newest first, less the oldest while it is only half loaded. */
    val days: StateFlow<List<Day>?> = combine(window, reach) { rows, size ->
        rows?.let { group(it, whole = it.size < size) }
    }.share(null)

    val count: StateFlow<Int?> = dao.count().share(null)

    val lifetime: StateFlow<Summary?> = dao.totals(0L).map { it.summary }.share(null)

    val today: StateFlow<Summary?> =
        opening(dao::totals) { startOfDay(System.currentTimeMillis()) }
            .map { it.summary }
            .share(null)

    val month: StateFlow<Summary?> =
        opening(dao::totals) { startOfMonth(System.currentTimeMillis()) }
            .map { it.summary }
            .share(null)


    /** This week's spending, day by day, with the days that had none left out. */
    val week: StateFlow<List<Day>?> = flow {
        val start = startOfWeek(System.currentTimeMillis())
        emitAll(dao.since(start).map { rows -> spread(start, rows).filter { it.transactions.isNotEmpty() } })
    }.share(null)

    /** This month's spending by category, largest first. */
    val slices: StateFlow<List<Slice>?> =
        opening(dao::splits) { startOfMonth(System.currentTimeMillis()) }
            .map { splits ->
                splits.groupBy { Category.of(it.category) }
                    .map { (category, rows) ->
                        Slice(category, rows.sumOf { it.amount }, rows.sumOf { it.count })
                    }
                    .sortedByDescending { it.amount }
            }
            .share(null)

    /** The stored settings, null until the first read lands. Eager, since the launch screen waits on it. */
    val options: StateFlow<Options?> = preferences.options.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    /** The last deleted transaction, kept so a snackbar can put it back. */
    private val _deleted = MutableStateFlow<TransactionEntity?>(null)
    val deleted: StateFlow<TransactionEntity?> = _deleted.asStateFlow()

    /** Reaches a page further back: a short window is the whole ledger, an unread one is already growing. */
    fun expand() {
        val loaded = window.value ?: return
        if (loaded.size < reach.value) return
        reach.update { it + Page }
    }

    fun saveThemeMode(mode: ThemeMode) {
        viewModelScope.launch { preferences.saveThemeMode(mode) }
    }

    fun saveThemeColor(color: ThemeColor) {
        viewModelScope.launch { preferences.saveThemeColor(color) }
    }

    fun saveIsDynamicColor(isDynamic: Boolean) {
        viewModelScope.launch { preferences.saveIsDynamicColor(isDynamic) }
    }

    fun saveCurrency(currency: Currency) {
        viewModelScope.launch { preferences.saveCurrency(currency) }
    }

    /** Inserts when [transaction] has no id yet, updates otherwise. */
    fun save(transaction: TransactionEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            if (transaction.id == 0L) dao.insert(transaction) else dao.update(transaction)
        }
    }

    fun delete(transaction: TransactionEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.delete(transaction)
            _deleted.value = transaction
        }
    }

    /** Puts the last deleted transaction back, keeping its original id. */
    fun restore() {
        val transaction = _deleted.value ?: return
        _deleted.value = null
        viewModelScope.launch(Dispatchers.IO) { dao.insert(transaction) }
    }

    fun forget() {
        _deleted.value = null
    }

    fun clear() {
        viewModelScope.launch(Dispatchers.IO) { dao.clear() }
        reach.value = Page
    }
}

/** Splits [rows] into days, newest first, dropping the oldest when it is only partly loaded. */
private fun group(rows: List<TransactionEntity>, whole: Boolean): List<Day> {
    val days = rows.groupBy { startOfDay(it.timestamp) }
        .map { (timestamp, items) -> Day(timestamp, items) }
    return if (whole || days.size < 2) days else days.dropLast(1)
}

/** Seven days from [start], each holding whichever of [rows] fell on it. */
private fun spread(start: Long, rows: List<TransactionEntity>): List<Day> {
    val byDay = rows.groupBy { startOfDay(it.timestamp) }
    return List(7) { offset ->
        val timestamp = plusDays(start, offset)
        Day(timestamp, byDay[timestamp].orEmpty())
    }
}
