package `in`.iambhvsh.outflow.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

/**
 * Background BroadcastReceiver that intercepts incoming SMS messages,
 * extracts bank/UPI transactions, logs them locally into Room, and fires
 * an alert notification without needing the app to be open or running in foreground.
 */
class Receiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION && action != ACTION_TEST_SMS) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Check if user has enabled auto SMS tracking in settings
                val isTracking = context.dataStore.data.first()[PreferencesManager.IS_AUTO_SMS_TRACKING_KEY] ?: false
                if (!isTracking) return@launch

                val testBody = intent.getStringExtra(EXTRA_TEST_BODY)
                val testTimestamp = intent.getLongExtra(EXTRA_TEST_TIMESTAMP, System.currentTimeMillis())

                val (fullBody, timestamp) = if (!testBody.isNullOrBlank()) {
                    testBody to testTimestamp
                } else {
                    val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                    if (messages.isNullOrEmpty()) return@launch
                    val body = messages.joinToString("") { it.displayMessageBody ?: "" }
                    val time = messages.firstOrNull()?.timestampMillis ?: System.currentTimeMillis()
                    body to time
                }

                val parsed = Parser.parse(fullBody, timestamp) ?: return@launch

                val db = AppDatabase.getDatabase(context)
                val dao = db.transactionDao()

                // Deduplicate within a 2-hour window
                val windowMs = 2 * 60 * 60 * 1000L
                val duplicates = dao.countSimilar(
                    title = parsed.title,
                    amount = parsed.amount,
                    from = parsed.timestamp - windowMs,
                    to = parsed.timestamp + windowMs
                )

                if (duplicates == 0) {
                    val entity = TransactionEntity(
                        title = parsed.title,
                        amount = parsed.amount,
                        category = parsed.category.name,
                        timestamp = parsed.timestamp,
                        type = parsed.type
                    )
                    val insertedId = dao.insert(entity)
                    val insertedEntity = entity.copy(id = insertedId)

                    val prefsManager = PreferencesManager(context)
                    val currency = prefsManager.options.first().currency

                    Notifier.notifyTransaction(
                        context = context,
                        transaction = insertedEntity,
                        category = parsed.category,
                        currencySymbol = currency.symbol
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Fail silently to prevent crashing the system receiver
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_TEST_SMS = "in.iambhvsh.outflow.TEST_SMS"
        const val EXTRA_TEST_BODY = "body"
        const val EXTRA_TEST_TIMESTAMP = "timestamp"
    }
}
