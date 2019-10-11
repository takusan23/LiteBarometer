package io.github.takusan23.litebarometer.Fragment

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import io.github.takusan23.litebarometer.Database.BarometerSQLiteHelper
import io.github.takusan23.litebarometer.R
import kotlinx.android.synthetic.main.fragment_barometer_layout.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.math.roundToInt

class BarometerFragment : Fragment() {

    //センサー
    lateinit var sensorManager: SensorManager
    lateinit var sensorEventListener: SensorEventListener

    //データベース
    lateinit var sqLiteDatabase: SQLiteDatabase
    lateinit var barometerSQLiteHelper: BarometerSQLiteHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_barometer_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //データベース用意
        initDB()

        sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        //気圧計
        val sensorList = sensorManager.getSensorList(Sensor.TYPE_PRESSURE)
        //受け取る
        sensorEventListener = object : SensorEventListener {
            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

            }

            override fun onSensorChanged(p0: SensorEvent?) {
                //気圧計の値？
                if (p0?.sensor?.type == Sensor.TYPE_PRESSURE) {
                    //整数に
                    val barometer = p0.values[0].roundToInt()
                    if (baromter_textview.text == "") {
                        //最初のときはデータベースに値を保存する
                        insertDB(barometer, p0.values[0])
                    }
                    //表示
                    baromter_textview.text = "$barometer hPa\n(${p0.values[0]} hPa)"
                }
            }
        }
        //登録
        sensorManager.registerListener(
            sensorEventListener,
            sensorList[0],
            SensorManager.SENSOR_DELAY_NORMAL  //更新頻度
        )

        //共有できるように
        fab.setOnClickListener {
            //時間
            val simpleDateFormat = SimpleDateFormat("MM/dd HH:mm:ss:SSS")
            val calender = Calendar.getInstance()
            //共有内容
            val text = "${baromter_textview.text}\n${simpleDateFormat.format(calender.time)}"
            val shareCompat = ShareCompat.IntentBuilder.from(activity)
            shareCompat.setChooserTitle(getString(R.string.barometer_share))
            shareCompat.setText(text)
            shareCompat.setType("text/plain");
            shareCompat.startChooser();
        }

    }

    //データベースに追加する
    private fun insertDB(barometer: Int, barometer_long: Float) {
        val simpleDateFormat = SimpleDateFormat("MM/dd HH:mm:ss:SSS")
        val calender = Calendar.getInstance()
        val contentValues = ContentValues()
        contentValues.put("setting", "")//将来使う？
        contentValues.put("value", barometer.toString())
        contentValues.put("value_float", barometer_long.toString())
        contentValues.put("create_at", simpleDateFormat.format(calender.time))
        contentValues.put("unix", (System.currentTimeMillis() / 1000L).toString())
        sqLiteDatabase.insert("barometer_db", null, contentValues)
    }

    private fun initDB() {
        if (!this@BarometerFragment::barometerSQLiteHelper.isInitialized && context != null) {
            //初期化してないときはいる
            barometerSQLiteHelper = BarometerSQLiteHelper(context!!)
            sqLiteDatabase = barometerSQLiteHelper.writableDatabase
            barometerSQLiteHelper.setWriteAheadLoggingEnabled(false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //登録解除しておく
        sensorManager.unregisterListener(sensorEventListener)
    }

}