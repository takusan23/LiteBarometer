package io.github.takusan23.litebarometer.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * データベースに入れるデータ
 * SQLite時代と名前を合わせておく必要がある
 * なお変数名に大文字（カラムが大文字になっているせいで）を使いたくない場合は@ColumnInfoを使うことでカラム名と変数名を別にすることができます。
 * */
@Entity(tableName = "barometer_db")
data class BarometerDBEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id") val id: Int = 0, // 主キー
    @ColumnInfo(name = "value") val value: String,
    @ColumnInfo(name = "value_float") val valueFloat: String,
    @ColumnInfo(name = "create_at") val createAt: String,
    @ColumnInfo(name = "unix") val unixTime: String,
    @ColumnInfo(name = "setting") val setting: String
)