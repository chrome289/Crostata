package xyz.siddharthseth.crostata.data.repository.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import xyz.siddharthseth.crostata.data.dao.local.database.UserDao
import xyz.siddharthseth.crostata.domain.model.User

@Database(
    entities = [User::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        private var appDatabase: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (appDatabase == null) {
                appDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "pandoro.db")
                    .build()
            }
            return appDatabase as AppDatabase
        }
    }
}