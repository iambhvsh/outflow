package `in`.iambhvsh.outflow.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import `in`.iambhvsh.outflow.ui.Main
import java.util.Locale

object Notifier {

    private const val CHANNEL_ID = "outflow_transactions"
    private const val CHANNEL_NAME = "Transactions"
    private const val CHANNEL_DESC = "Instant notifications for automatically tracked transactions"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }

    fun notifyTransaction(
        context: Context,
        transaction: TransactionEntity,
        category: Category,
        currencySymbol: String = "₹"
    ) {
        createChannel(context)

        val intent = Intent(context, Main::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            transaction.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val formattedAmount = String.format(Locale.getDefault(), "%.2f", transaction.amount)
        val titleText = when (transaction.type) {
            TransactionType.OUTFLOW -> "Sent $currencySymbol$formattedAmount to ${transaction.title}"
            TransactionType.INFLOW -> "Received $currencySymbol$formattedAmount from ${transaction.title}"
        }
        val contentText = "Auto-logged under ${category.label} • 100% private on device"

        val appIcon = context.applicationInfo.icon.takeIf { it != 0 } ?: android.R.drawable.stat_notify_more
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(appIcon)
            .setContentTitle(titleText)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            ) {
                notificationManager.notify((System.currentTimeMillis() % 100000).toInt(), builder.build())
            }
        } catch (_: SecurityException) {
            // Permission not granted
        }
    }
}
