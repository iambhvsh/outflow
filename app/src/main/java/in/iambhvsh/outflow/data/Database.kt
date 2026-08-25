package `in`.iambhvsh.outflow.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [TransactionEntity::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {

        /**
         * Version 2 adds the two indices the reads in [TransactionDao] go through. No column changes
         * and no table rebuild, so an existing ledger is left exactly as it was.
         */
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

        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "outflow_database"
                ).addMigrations(Indices).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
