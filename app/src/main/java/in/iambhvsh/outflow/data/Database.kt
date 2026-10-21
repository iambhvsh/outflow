package `in`.iambhvsh.outflow.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [TransactionEntity::class, BudgetEntity::class], version = 3, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        private val Indices = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_outflows_timestamp` " +
                        "ON `outflows` (`timestamp`)"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_outflows_type_timestamp` " +
                        "ON `outflows` (`type`, `timestamp`)"
                )
            }
        }

        private val BudgetsTable = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `budgets` (" +
                        "`category` TEXT NOT NULL, " +
                        "`limit` REAL NOT NULL, " +
                        "PRIMARY KEY(`category`))"
                )
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "outflow_database"
                ).addMigrations(Indices, BudgetsTable).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
