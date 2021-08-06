package io.github.takusan23.litebarometer.fragment

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.takusan23.litebarometer.R
import io.github.takusan23.litebarometer.room.database.BarometerDB
import io.github.takusan23.litebarometer.room.entity.BarometerDBEntity
import kotlinx.android.synthetic.main.fragment_barometer_layout.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.roundToInt

class BarometerFragment : Fragment() {

    //センサー
    lateinit var sensorManager: SensorManager
    lateinit var sensorEventListener: SensorEventListener

    //データベース
    private val barometerDB by lazy { BarometerDB.getInstance(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_barometer_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            val shareCompat = ShareCompat.IntentBuilder.from(requireActivity())
            shareCompat.setChooserTitle(getString(R.string.barometer_share))
            shareCompat.setText(text)
            shareCompat.setType("text/plain");
            shareCompat.startChooser();
        }

    }

    //データベースに追加する
    private fun insertDB(barometer: Int, barometer_long: Float) {
        thread {
            val simpleDateFormat = SimpleDateFormat("MM/dd HH:mm:ss:SSS")
            val calender = Calendar.getInstance()
            // DBへ追加
            val dbEntity = BarometerDBEntity(
                value = barometer.toString(),
                createAt = simpleDateFormat.format(calender.time),
                setting = "",
                unixTime = (System.currentTimeMillis() / 1000L).toString(),
                valueFloat = barometer_long.toString()
            )
            barometerDB.barometerDBDao().insert(dbEntity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //登録解除しておく
        sensorManager.unregisterListener(sensorEventListener)
    }

}