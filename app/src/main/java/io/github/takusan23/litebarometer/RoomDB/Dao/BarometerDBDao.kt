package io.github.takusan23.litebarometer.RoomDB.Dao

import androidx.room.*
import io.github.takusan23.litebarometer.RoomDB.Entity.BarometerDBEntity

@Dao
interface BarometerDBDao {
    /** 全データ取得 */
    @Query("SELECT * FROM barometer_db")
    fun getAll(): List<BarometerDBEntity>

    /** データ更新 */
    @Update
    fun update(barometerDBEntity: BarometerDBEntity)

    /** データ追加 */
    @Insert
    fun insert(barometerDBEntity: BarometerDBEntity)

    /** データ削除 */
    @Delete
    fun delete(barometerDBEntity: BarometerDBEntity)

    /** データ検索 */
    @Query("SELECT * FROM barometer_db WHERE VALUE_FLOAT = :valueFloat")
    fun find(valueFloat: String): List<BarometerDBEntity>
}