package io.github.takusan23.litebarometer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.preference.PreferenceManager
import io.github.takusan23.litebarometer.Database.BarometerSQLiteHelper
import kotlinx.android.synthetic.main.fragment_barometer_layout.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.math.roundToInt

class BarometerService : Service() {

    val oneMinuteMilliSecond = 60000
    lateinit var pref_setting: SharedPreferences

    //データベース
    lateinit var sqLiteDatabase: SQLiteDatabase
    lateinit var barometerSQLiteHelper: BarometerSQLiteHelper

    //センサー
    lateinit var sensorManager: SensorManager
    lateinit var sensorEventListener: SensorEventListener

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        pref_setting = PreferenceManager.getDefaultSharedPreferences(this)
        //Serviceなので通知出す
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //通知チャンネル
        val channelID = "barometerServiceNotification"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Oreo以降
            if (notificationManager.getNotificationChannel(channelID) == null) {
                val notificationChannel = NotificationChannel(
                    channelID,
                    getString(R.string.notification),
                    NotificationManager.IMPORTANCE_MIN
                )
                notificationManager.createNotificationChannel(notificationChannel)
            }
            //通知作成
            val notification = Notification.Builder(this, channelID)
                .setContentTitle(getString(R.string.notification))
                .setContentText(getString(R.string.notification_desc))
                .setSmallIcon(R.drawable.icon)
                .build()
            startForeground(1, notification)
        } else {
            //通知作成
            val notification = Notification.Builder(this)
                .setContentTitle(getString(R.string.notification))
                .setContentText(getString(R.string.notification_desc))
                .setSmallIcon(R.drawable.icon)
                .build()
            notificationManager.notify(1, notification)
        }

        initDB()
        backgroundBarometer()


        return START_NOT_STICKY
    }

    private fun backgroundBarometer() {
        //定期的に動かす
        val interval = pref_setting.getInt("interval", 60)
        Timer().schedule(timerTask {
            barometer()
        }, 0, oneMinuteMilliSecond * interval.toLong())
    }

    private fun initDB() {
        if (!this@BarometerService::barometerSQLiteHelper.isInitialized) {
            //初期化してないときはいる
            barometerSQLiteHelper = BarometerSQLiteHelper(this)
            sqLiteDatabase = barometerSQLiteHelper.writableDatabase
            barometerSQLiteHelper.setWriteAheadLoggingEnabled(false)
        }
    }

    fun barometer() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
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
                    // println(barometer)
                    insertDB(barometer, p0.values[0])
                    sensorManager.unregisterListener(sensorEventListener)
                }
            }
        }
        //登録
        sensorManager.registerListener(
            sensorEventListener,
            sensorList[0],
            SensorManager.SENSOR_DELAY_NORMAL  //更新頻度
        )
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

}