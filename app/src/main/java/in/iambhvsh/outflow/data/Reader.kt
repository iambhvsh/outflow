package `in`.iambhvsh.outflow.data

import android.content.Context
import androidx.core.net.toUri

object Reader {

    fun scanRecentBankSms(context: Context, limit: Int = 150): List<ParsedTransaction> {
        val list = mutableListOf<ParsedTransaction>()
        val uri = "content://sms/inbox".toUri()
        val projection = arrayOf("_id", "body", "date")

        try {
            val cursor = context.contentResolver.query(
                uri,
                projection,
                null,
                null,
                "date DESC"
            )
            cursor?.use {
                val bodyIdx = it.getColumnIndex("body")
                val dateIdx = it.getColumnIndex("date")
                var count = 0
                while (it.moveToNext() && count < limit) {
                    val body = if (bodyIdx != -1) it.getString(bodyIdx).orEmpty() else ""
                    val date = if (dateIdx != -1) it.getLong(dateIdx) else System.currentTimeMillis()

                    val parsed = Parser.parse(body, date)
                    if (parsed != null) {
                        list.add(parsed)
                    }
                    count++
                }
            }
        } catch (_: SecurityException) {
            // Permission not granted
        }

        return list
    }
}
