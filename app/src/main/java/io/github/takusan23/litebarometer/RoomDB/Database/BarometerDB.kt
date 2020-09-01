package io.github.takusan23.litebarometer.RoomDB.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.takusan23.litebarometer.RoomDB.Dao.BarometerDBDao
import io.github.takusan23.litebarometer.RoomDB.Entity.BarometerDBEntity

/**
 * SQLiteから移行する場合はバージョンを上げる必要がある
 * */
@Database(entities = [BarometerDBEntity::class], version = 3)
abstract class BarometerDB : RoomDatabase() {
    abstract fun barometerDBDao(): BarometerDBDao
}