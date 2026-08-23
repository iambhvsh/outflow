package `in`.iambhvsh.outflow.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import `in`.iambhvsh.outflow.data.OutflowDatabase
import `in`.iambhvsh.outflow.data.TransactionEntity
import `in`.iambhvsh.outflow.data.TransactionType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OutflowViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = OutflowDatabase.getDatabase(application).transactionDao()

    val transactions: StateFlow<List<TransactionEntity>> = dao.getAllTransactions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    data class Summary(val inflow: Double = 0.0, val outflow: Double = 0.0) {
        val net: Double get() = inflow - outflow
    }

    val summary: StateFlow<Summary> = transactions
        .map { list ->
            var inflow = 0.0
            var outflow = 0.0
            for (transaction in list) {
                when (transaction.type) {
                    TransactionType.INFLOW -> inflow += transaction.amount
                    TransactionType.OUTFLOW -> outflow += transaction.amount
                }
            }
            Summary(inflow = inflow, outflow = outflow)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Summary()
        )

    fun insert(title: String, amount: Double, category: String, type: TransactionType) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            dao.insert(
                TransactionEntity(
                    title = title,
                    amount = amount,
                    category = category,
                    timestamp = System.currentTimeMillis(),
                    type = type
                )
            )
        }
    }

    fun delete(transaction: TransactionEntity) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            dao.delete(transaction)
        }
    }
}
