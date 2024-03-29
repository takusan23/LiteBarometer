package io.github.takusan23.litebarometer.service

import android.app.*
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.preference.PreferenceManager
import io.github.takusan23.litebarometer.BarometerWeatherWidget
import io.github.takusan23.litebarometer.BarometerWidget
import io.github.takusan23.litebarometer.MainActivity
import io.github.takusan23.litebarometer.R

/**
 * 気圧計ウィジェットを更新する「フォアグラウンド」サービス
 *
 * フォアグラウンドでないと気圧センサーへアクセスできないらしいので
 * */
class BarometerWidgetUpdateForegroundService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //Serviceなので通知出す
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //通知チャンネル
        val channelID = "barometer_widget_update_foreground_service"
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Oreo以降
            if (notificationManager.getNotificationChannel(channelID) == null) {
                val notificationChannel = NotificationChannel(channelID, getString(R.string.barometer_widget_update_service_name), NotificationManager.IMPORTANCE_MIN)
                notificationManager.createNotificationChannel(notificationChannel)
            }
            //通知作成
            Notification.Builder(this, channelID)
        } else {
            //通知作成
            Notification.Builder(this)
        }.apply {
            setContentTitle(getString(R.string.barometer_widget_update_service_name))
            setContentText(getString(R.string.barometer_widget_update_service_working))
            setSmallIcon(R.drawable.icon)
        }.build()

        startForeground(1, notification)

        // ウイジェット更新
        updateWidget()

        return START_NOT_STICKY
    }

    /** ウイジェット更新 */
    private fun updateWidget() {
        val context = this@BarometerWidgetUpdateForegroundService
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val sensorList: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_PRESSURE)
        val sensorEventListener: SensorEventListener
        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(sensorEvent: SensorEvent) {
                val barometer = Math.round(sensorEvent.values[0])
                val barometerFloat = sensorEvent.values[0]

                val barometerWidgetText = """
                    $barometer hPa
                    (${String.format("%.3f", barometerFloat)} hPa)
                    """.trimIndent()
                val weatherWidgetText = "$barometer hPa"
                val weatherIcon = if (barometer >= 1013) R.drawable.ic_weather_sun else R.drawable.ic_weather_rain

                // ウィジェット更新
                BarometerWidget.updateWidget(context, barometerWidgetText)
                BarometerWeatherWidget.updateAppWidget(context, weatherIcon, weatherWidgetText)

                //登録解除
                sensorManager.unregisterListener(this)
                // サービス終了
                stopSelf()
            }

            override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
        }
        sensorManager.registerListener(sensorEventListener, sensorList[0], SensorManager.SENSOR_DELAY_NORMAL)
    }

}