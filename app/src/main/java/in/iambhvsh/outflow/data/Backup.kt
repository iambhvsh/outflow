package `in`.iambhvsh.outflow.data

import android.content.Context
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject

/**
 * Representation of a complete Outflow backup, including user preferences and every recorded flow.
 */
data class BackupData(
    val version: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val options: Options,
    val transactions: List<TransactionEntity>,
    val budgets: List<BudgetEntity> = emptyList()
) {
    fun toJson(): String {
        val root = JSONObject()
        root.put("version", version)
        root.put("app", "Outflow")
        root.put("exportedAt", exportedAt)

        val settingsObj = JSONObject()
        settingsObj.put("themeMode", options.themeMode.name)
        settingsObj.put("themeColor", options.themeColor.name)
        settingsObj.put("isDynamicColor", options.isDynamicColor)
        settingsObj.put("currency", options.currency.name)
        settingsObj.put("isAutoSmsTracking", options.isAutoSmsTracking)
        root.put("settings", settingsObj)

        val transactionsArr = JSONArray()
        for ((_, title, amount, category, timestamp, type) in transactions) {
            val itemObj = JSONObject()
            itemObj.put("title", title)
            itemObj.put("amount", amount)
            itemObj.put("category", category)
            itemObj.put("timestamp", timestamp)
            itemObj.put("type", type.name)
            transactionsArr.put(itemObj)
        }
        root.put("transactions", transactionsArr)

        val budgetsArr = JSONArray()
        for ((category, limit) in budgets) {
            val bObj = JSONObject()
            bObj.put("category", category)
            bObj.put("limit", limit)
            budgetsArr.put(bObj)
        }
        root.put("budgets", budgetsArr)

        return root.toString(2)
    }

    companion object {
        fun fromJson(jsonStr: String): BackupData {
            val root = JSONObject(jsonStr)
            val version = root.optInt("version", 1)
            val exportedAt = root.optLong("exportedAt", System.currentTimeMillis())

            val settingsObj = root.optJSONObject("settings")
            val options = Options(
                themeMode = settingsObj?.optString("themeMode")?.let { raw ->
                    runCatching { ThemeMode.valueOf(raw) }.getOrNull()
                } ?: ThemeMode.SYSTEM,
                themeColor = settingsObj?.optString("themeColor")?.let { raw ->
                    runCatching { ThemeColor.valueOf(raw) }.getOrNull()
                } ?: ThemeColor.BLUE,
                isDynamicColor = settingsObj?.optBoolean("isDynamicColor", false) ?: false,
                currency = settingsObj?.optString("currency")?.let { raw ->
                    runCatching { Currency.valueOf(raw) }.getOrNull()
                } ?: Currency.INR,
                isAutoSmsTracking = settingsObj?.optBoolean("isAutoSmsTracking", false) ?: false
            )

            val transactionsArr = root.optJSONArray("transactions") ?: JSONArray()
            val list = mutableListOf<TransactionEntity>()
            for (i in 0 until transactionsArr.length()) {
                val itemObj = transactionsArr.getJSONObject(i)
                val typeStr = itemObj.optString("type", "OUTFLOW")
                val type = runCatching { TransactionType.valueOf(typeStr) }.getOrDefault(TransactionType.OUTFLOW)
                val title = itemObj.optString("title", "")
                val amount = itemObj.optDouble("amount", 0.0)
                val category = itemObj.optString("category", Category.OTHER.name)
                val timestamp = itemObj.optLong("timestamp", System.currentTimeMillis())
                if (amount > 0.0) {
                    list.add(
                        TransactionEntity(
                            id = 0L,
                            title = title,
                            amount = amount,
                            category = category,
                            timestamp = timestamp,
                            type = type
                        )
                    )
                }
            }

            val budgetsArr = root.optJSONArray("budgets") ?: JSONArray()
            val budgetList = mutableListOf<BudgetEntity>()
            for (i in 0 until budgetsArr.length()) {
                val bObj = budgetsArr.getJSONObject(i)
                val cat = bObj.optString("category", "")
                val limit = bObj.optDouble("limit", 0.0)
                if (cat.isNotBlank() && limit > 0.0) {
                    budgetList.add(BudgetEntity(category = cat, limit = limit))
                }
            }

            return BackupData(
                version = version,
                exportedAt = exportedAt,
                options = options,
                transactions = list,
                budgets = budgetList
            )
        }
    }
}

/** Writes [data] as JSON to the destination [uri] via Storage Access Framework. */
fun exportBackup(context: Context, uri: Uri, data: BackupData): Boolean {
    return try {
        context.contentResolver.openOutputStream(uri)?.use { stream ->
            stream.bufferedWriter().use { writer ->
                writer.write(data.toJson())
            }
        }
        true
    } catch (_: Exception) {
        false
    }
}

/** Reads and parses [BackupData] from the source [uri] via Storage Access Framework. */
fun importBackup(context: Context, uri: Uri): BackupData? {
    return try {
        val jsonStr = context.contentResolver.openInputStream(uri)?.use { stream ->
            stream.bufferedReader().use { it.readText() }
        } ?: return null
        BackupData.fromJson(jsonStr)
    } catch (_: Exception) {
        null
    }
}
