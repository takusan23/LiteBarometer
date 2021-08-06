package io.github.takusan23.litebarometer.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.takusan23.litebarometer.room.dao.BarometerDBDao
import io.github.takusan23.litebarometer.room.entity.BarometerDBEntity

/**
 * SQLiteから移行する場合はバージョンを上げる必要がある
 * */
@Database(entities = [BarometerDBEntity::class], version = 3)
abstract class BarometerDB : RoomDatabase() {
    abstract fun barometerDBDao(): BarometerDBDao

    companion object {

        private var database: BarometerDB? = null

        /** データベースのインスタンスを返す */
        fun getInstance(context: Context): BarometerDB {
            if (database == null) {
                database = Room.databaseBuilder(context, BarometerDB::class.java, "Barometer.db")
                    .addMigrations(object : Migration(2, 3) {
                        override fun migrate(database: SupportSQLiteDatabase) {
                            // SQLite移行。移行後のデータベースを作成する
                            database.execSQL(
                                """
                        CREATE TABLE barometer_db_tmp (
                          _id INTEGER NOT NULL PRIMARY KEY, 
                          value TEXT NOT NULL,
                          value_float TEXT NOT NULL,
                          create_at TEXT NOT NULL,
                          unix TEXT NOT NULL,
                          setting TEXT NOT NULL
                        )
                        """
                            )
                            // 移行後のデータベースへデータを移す
                            database.execSQL(
                                """
                        INSERT INTO barometer_db_tmp (_id,value,value_float,create_at,unix,setting)
                        SELECT _id, value, value_float, create_at, unix, setting FROM barometer_db
                        """
                            )
                            // 前あったデータベースを消す
                            database.execSQL("DROP TABLE barometer_db")
                            // 移行後のデータベースの名前を移行前と同じにして移行完了
                            database.execSQL("ALTER TABLE barometer_db_tmp RENAME TO barometer_db")
                        }
                    })
                    .build()
            }
            return database!!
        }
    }

}