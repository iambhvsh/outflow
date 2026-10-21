package `in`.iambhvsh.outflow.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Result of parsing a bank or UPI transaction SMS.
 */
data class ParsedTransaction(
    val title: String,
    val amount: Double,
    val category: Category,
    val type: TransactionType,
    val timestamp: Long,
    val refNo: String? = null
)

object Parser {

    private val OUTFLOW_KEYWORDS = listOf(
        "sent", "debited", "paid", "spent", "withdrawn", "dr"
    )

    private val INFLOW_KEYWORDS = listOf(
        "received", "credited", "deposited", "refunded", "cr"
    )

    private val OTP_EXCLUSIONS = listOf(
        "otp", "one time password", "verification code", "secret code",
        "do not share", "is your code", "request to pay", "requested rs",
        "request from", "pre-approved loan", "apply for loan"
    )

    private val OUTFLOW_CATEGORY_RULES: List<Pair<Category, List<String>>> = listOf(
        Category.FOOD to listOf(
            "swiggy", "zomato", "mcdonald", "burger", "pizza", "starbucks", "domino",
            "kfc", "subway", "cafe", "restaurant", "dining", "dhaba", "baker", "bakery",
            "eats", "kitchen", "biryani", "canteen", "tea", "chai", "coffee", "juice",
            "sweets", "snack", "tiffin", "dine", "barbeque", "haldiram"
        ),
        Category.GROCERIES to listOf(
            "blinkit", "zepto", "instamart", "bigbasket", "dmart", "grofer",
            "supermarket", "hypermarket", "grocery", "groceries", "kirana",
            "provision", "vegetable", "fruits", "dairy", "milk", "nature basket",
            "spencer", "more retail"
        ),
        Category.TRANSPORT to listOf(
            "petroleum", "petrol", "fuel", "diesel", "cng", "hpcl", "bpcl", "iocl",
            "indian oil", "bharat petro", "hindustan petro", "shell", "uber", "ola",
            "rapido", "metro", "railway", "irctc", "toll", "fastag", "auto", "cab",
            "taxi", "flight", "indigo", "air india", "vistara", "bus", "redbus",
            "parking", "transport", "loco", "petro"
        ),
        Category.SHOPPING to listOf(
            "sales", "retail", "store", "shop", "shopping", "amazon", "flipkart",
            "myntra", "meesho", "ajio", "nykaa", "tata cliq", "zara", "h&m",
            "uniqlo", "cloth", "apparel", "garment", "fashion", "mall", "bazaar",
            "bazar", "electronics", "croma", "reliance digital", "vijay sales",
            "decathlon", "trends", "lenskart", "westside", "pantaloons"
        ),
        Category.BILLS to listOf(
            "electricity", "power", "bescom", "tneb", "mseb", "uppcl", "water",
            "gas", "indane", "hp gas", "bharat gas", "broadband", "wifi", "airtel",
            "jio", "vodafone", "vi", "bsnl", "recharge", "bill", "utility",
            "insurance", "lic", "premium", "policybazaar", "credit card", "billdesk"
        ),
        Category.RENT to listOf(
            "rent", "landlord", "flat", "apartment", "housing", "society",
            "maintenance", "brokerage", "nobroker"
        ),
        Category.HEALTH to listOf(
            "pharmacy", "medical", "hospital", "clinic", "chemist", "doctor",
            "apollo", "1mg", "pharmeasy", "netmeds", "medplus", "health",
            "dental", "care", "diagnostics", "pathology", "lab", "medicine"
        ),
        Category.ENTERTAINMENT to listOf(
            "movie", "cinema", "pvr", "inox", "cinepolis", "bookmyshow", "ticket",
            "netflix", "spotify", "prime video", "disney", "hotstar", "youtube",
            "gaming", "steam", "playstation", "theatre", "fun", "amusement",
            "entertainment"
        ),
        Category.EDUCATION to listOf(
            "school", "college", "university", "tuition", "classes", "fee",
            "course", "udemy", "coursera", "unacademy", "byju", "books",
            "stationery", "exam", "academy", "institute", "education"
        ),
        Category.GIFTS to listOf(
            "gift", "present", "donation", "charity", "ngo", "temple", "trust",
            "flowers", "fnp", "fern"
        )
    )

    private val INFLOW_CATEGORY_RULES: List<Pair<Category, List<String>>> = listOf(
        Category.SALARY to listOf(
            "salary", "payroll", "wages", "stipend", "remuneration", "monthly pay"
        ),
        Category.BUSINESS to listOf(
            "client", "invoice", "merchant payout", "settlement", "consulting",
            "services", "contract"
        ),
        Category.INVESTMENT to listOf(
            "dividend", "interest", "zerodha", "groww", "upstox", "angelone",
            "mutual fund", "shares", "stock", "equity", "fixed deposit", "fd",
            "rd", "returns"
        ),
        Category.REFUND to listOf(
            "refund", "cashback", "reversal", "reversed", "returned", "failed"
        ),
        Category.BONUS to listOf(
            "bonus", "reward", "incentive", "prize"
        )
    )

    /**
     * Parses an SMS message body into a [ParsedTransaction] if it represents a financial
     * transaction. Returns null if the SMS is an OTP, promotional, or unrecognized.
     */
    fun parse(body: String, smsTimestamp: Long = System.currentTimeMillis()): ParsedTransaction? {
        val lower = body.lowercase(Locale.ENGLISH)

        // 1. Exclude OTPs, promotional alerts, or payment requests
        for (exclusion in OTP_EXCLUSIONS) {
            if (lower.contains(exclusion)) return null
        }

        // 2. Determine Transaction Type
        val hasOutflowKeyword = OUTFLOW_KEYWORDS.any { Regex("\\b$it\\b").containsMatchIn(lower) }
        val hasInflowKeyword = INFLOW_KEYWORDS.any { Regex("\\b$it\\b").containsMatchIn(lower) }

        if (!hasOutflowKeyword && !hasInflowKeyword) return null

        val type = when {
            hasOutflowKeyword && !hasInflowKeyword -> TransactionType.OUTFLOW
            !hasOutflowKeyword -> TransactionType.INFLOW
            else -> {
                // Both appear: pick the one appearing first in the SMS
                val firstOutflowIdx = OUTFLOW_KEYWORDS
                    .map { lower.indexOf(it) }
                    .filter { it >= 0 }
                    .minOrNull() ?: Int.MAX_VALUE
                val firstInflowIdx = INFLOW_KEYWORDS
                    .map { lower.indexOf(it) }
                    .filter { it >= 0 }
                    .minOrNull() ?: Int.MAX_VALUE

                if (firstOutflowIdx <= firstInflowIdx) TransactionType.OUTFLOW else TransactionType.INFLOW
            }
        }

        // 3. Extract Amount
        val amount = extractAmount(body) ?: return null

        // 4. Extract Title / Party
        val title = extractParty(body, type)

        // 5. Extract Date
        val timestamp = extractDate(body, smsTimestamp)

        // 6. Extract UPI Ref (if present)
        val refNo = extractUpiRef(body)

        // 7. Categorization
        val category = resolveCategory(title, body, type)

        return ParsedTransaction(
            title = title,
            amount = amount,
            category = category,
            type = type,
            timestamp = timestamp,
            refNo = refNo
        )
    }

    private fun extractAmount(body: String): Double? {
        // Formats: "Rs.130.00", "Rs 130.00", "INR 10,000.00", "₹112.86"
        val currencyFirst = Regex(
            """(?:Rs\.?|INR|₹)\s*([0-9]+(?:,[0-9]+)*(?:\.[0-9]{1,2})?)""",
            RegexOption.IGNORE_CASE
        ).find(body)

        val rawAmount = currencyFirst?.groupValues?.getOrNull(1) ?: run {
            // Alternative: "130.00 debited" or "130.00 credited"
            Regex(
                """([0-9]+(?:,[0-9]+)*(?:\.[0-9]{1,2})?)\s*(?:debited|credited|sent|received)""",
                RegexOption.IGNORE_CASE
            ).find(body)?.groupValues?.getOrNull(1)
        } ?: return null

        val clean = rawAmount.replace(",", "").trim()
        val parsed = clean.toDoubleOrNull() ?: return null
        return if (parsed > 0.0) parsed else null
    }

    private fun extractParty(body: String, type: TransactionType): String {
        val cleanBody = body.replace("\n", " ").trim()

        if (type == TransactionType.OUTFLOW) {
            val toRegex = Regex(
                """\b(?:to|at|towards)\s+([A-Za-z0-9\s&._\-@]{2,40}?)(?:\s+on\b|\s+in\b|\s+via\b|\s+using\b|\s+through\b|\s+UPI\b|\s+ref\b|\s+bal\b|\s+avl\b|\.|\n|$)""",
                RegexOption.IGNORE_CASE
            )
            val match = toRegex.find(cleanBody)
            if (match != null) {
                val candidate = match.groupValues[1].trim()
                val cleaned = cleanPartyName(candidate)
                if (cleaned.isNotBlank()) return cleaned
            }
            return "Outflow"
        } else {
            val fromRegex = Regex(
                """\b(?:from|by)\s+([A-Za-z0-9\s&._\-@]{2,40}?)(?:\s+in\s+your\b|\s+in\b|\s+into\b|\s+to\b|\s+on\b|\s+via\b|\s+using\b|\s+through\b|\s+UPI\b|\s+ref\b|\s+a\/c\b|\.|\n|$)""",
                RegexOption.IGNORE_CASE
            )
            val match = fromRegex.find(cleanBody)
            if (match != null) {
                val candidate = match.groupValues[1].trim()
                val cleaned = cleanPartyName(candidate)
                if (cleaned.isNotBlank() && !isAccountNumber(cleaned)) return cleaned
            }
            return "Inflow"
        }
    }

    private fun cleanPartyName(raw: String): String {
        var result = raw.trim()
        result = result.replace(Regex("""^(?:vpa|upi|m\/s|mr|mrs|ms)\.?\s*""", RegexOption.IGNORE_CASE), "")
        result = result.replace(Regex("""(?:\s+upi|\s+ref|\s+no\.?|\s+a\/c.*)$""", RegexOption.IGNORE_CASE), "")
        result = result.trim('.', ',', '-', ' ', ':')
        return result
    }

    private fun isAccountNumber(text: String): Boolean {
        val lower = text.lowercase(Locale.ENGLISH)
        return lower.startsWith("xx") || lower.startsWith("a/c") || lower.startsWith("ac") ||
            text.all { it.isDigit() || it == 'X' || it == 'x' || it == '*' }
    }

    private fun extractDate(body: String, fallbackTimestamp: Long): Long {
        val datePatterns = listOf(
            Regex("""\b(\d{1,2}-[A-Za-z]{3}-\d{2,4})\b""") to listOf("dd-MMM-yy", "dd-MMM-yyyy"),
            Regex("""\b(\d{1,2}/[0-9]{1,2}/\d{2,4})\b""") to listOf("dd/MM/yy", "dd/MM/yyyy"),
            Regex("""\b(\d{1,2}-[0-9]{1,2}-\d{2,4})\b""") to listOf("dd-MM-yy", "dd-MM-yyyy"),
            Regex("""\b(\d{1,2}\s+[A-Za-z]{3}\s+\d{2,4})\b""") to listOf("dd MMM yy", "dd MMM yyyy")
        )

        for ((regex, formats) in datePatterns) {
            val match = regex.find(body) ?: continue
            val dateStr = match.groupValues[1]
            for (format in formats) {
                try {
                    val sdf = SimpleDateFormat(format, Locale.ENGLISH).apply { isLenient = false }
                    val parsedDate = sdf.parse(dateStr)
                    if (parsedDate != null) {
                        val calDate = Calendar.getInstance().apply { time = parsedDate }
                        val calTime = Calendar.getInstance().apply { timeInMillis = fallbackTimestamp }

                        calDate.set(Calendar.HOUR_OF_DAY, calTime.get(Calendar.HOUR_OF_DAY))
                        calDate.set(Calendar.MINUTE, calTime.get(Calendar.MINUTE))
                        calDate.set(Calendar.SECOND, calTime.get(Calendar.SECOND))
                        calDate.set(Calendar.MILLISECOND, calTime.get(Calendar.MILLISECOND))

                        return calDate.timeInMillis
                    }
                } catch (_: Exception) {
                    // Try next format
                }
            }
        }

        return fallbackTimestamp
    }

    private fun extractUpiRef(body: String): String? {
        val refRegex = Regex(
            """(?:UPI\s+ref\s+no\.?|Ref\s+no\.?|UTR|ref)\s*[:.]?\s*([0-9a-zA-Z]{6,})""",
            RegexOption.IGNORE_CASE
        )
        return refRegex.find(body)?.groupValues?.getOrNull(1)
    }

    private fun resolveCategory(title: String, body: String, type: TransactionType): Category {
        val textToSearch = "${title.lowercase(Locale.ENGLISH)} ${body.lowercase(Locale.ENGLISH)}"
        val rules = if (type == TransactionType.OUTFLOW) OUTFLOW_CATEGORY_RULES else INFLOW_CATEGORY_RULES

        for ((category, keywords) in rules) {
            for (keyword in keywords) {
                if (textToSearch.contains(keyword)) {
                    return category
                }
            }
        }

        return Category.OTHER
    }
}
