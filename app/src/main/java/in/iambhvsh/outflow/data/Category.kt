package `in`.iambhvsh.outflow.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalGroceryStore
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.Redeem
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material.icons.rounded.Work
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * The fixed set of buckets a transaction can fall into, stored as the enum [name] in the text column
 * the table already had, so anything an older build wrote falls back to [OTHER] via [of]. [hue] is an
 * angle rather than a finished colour, since tone and chroma are the theme's business.
 */
enum class Category(
    val label: String,
    val icon: ImageVector,
    val type: TransactionType,
    val hue: Double
) {

    FOOD("Food", Icons.Rounded.Restaurant, TransactionType.OUTFLOW, hue = 40.0),
    GROCERIES("Groceries", Icons.Rounded.LocalGroceryStore, TransactionType.OUTFLOW, hue = 135.0),
    TRANSPORT("Transport", Icons.Rounded.DirectionsBus, TransactionType.OUTFLOW, hue = 232.0),
    SHOPPING("Shopping", Icons.Rounded.ShoppingBag, TransactionType.OUTFLOW, hue = 322.0),
    BILLS("Bills", Icons.Rounded.Receipt, TransactionType.OUTFLOW, hue = 78.0),
    RENT("Rent", Icons.Rounded.Home, TransactionType.OUTFLOW, hue = 278.0),
    HEALTH("Health", Icons.Rounded.FitnessCenter, TransactionType.OUTFLOW, hue = 8.0),
    ENTERTAINMENT("Fun", Icons.Rounded.Tv, TransactionType.OUTFLOW, hue = 300.0),
    EDUCATION("Education", Icons.Rounded.School, TransactionType.OUTFLOW, hue = 205.0),
    GIFTS("Gifts", Icons.Rounded.CardGiftcard, TransactionType.OUTFLOW, hue = 348.0),

    SALARY("Salary", Icons.Rounded.Work, TransactionType.INFLOW, hue = 158.0),
    BUSINESS("Business", Icons.Rounded.Payments, TransactionType.INFLOW, hue = 186.0),
    INVESTMENT("Investment", Icons.AutoMirrored.Rounded.TrendingUp, TransactionType.INFLOW, hue = 112.0),
    REFUND("Refund", Icons.Rounded.AccountBalance, TransactionType.INFLOW, hue = 252.0),
    BONUS("Bonus", Icons.Rounded.Redeem, TransactionType.INFLOW, hue = 58.0),

    OTHER("Other", Icons.Rounded.Category, TransactionType.OUTFLOW, hue = 0.0);

    companion object {

        /** The categories offered for a given [type], with [OTHER] always last. */
        fun of(type: TransactionType): List<Category> =
            entries.filter { it.type == type && it != OTHER } + OTHER

        /** Resolves a stored value, tolerating both the enum name and the human label. */
        fun of(raw: String?): Category {
            if (raw.isNullOrBlank()) return OTHER
            return entries.firstOrNull { it.name.equals(raw, ignoreCase = true) }
                ?: entries.firstOrNull { it.label.equals(raw, ignoreCase = true) }
                ?: OTHER
        }
    }
}
