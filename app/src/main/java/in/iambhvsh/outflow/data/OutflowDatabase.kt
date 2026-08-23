package `in`.iambhvsh.outflow.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [TransactionEntity::class], version = 1, exportSchema = true)
abstract class OutflowDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: OutflowDatabase? = null
        fun getDatabase(context: Context): OutflowDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OutflowDatabase::class.java,
                    "outflow_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
