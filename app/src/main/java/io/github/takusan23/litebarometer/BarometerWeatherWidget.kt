package io.github.takusan23.litebarometer

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.widget.RemoteViews
import androidx.core.graphics.drawable.IconCompat
import io.github.takusan23.litebarometer.service.BarometerWidgetUpdateForegroundService

/**
 * Implementation of App Widget functionality.
 */
class BarometerWeatherWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // 更新用Service起動
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(Intent(context, BarometerWidgetUpdateForegroundService::class.java))
        } else {
            context.startService(Intent(context, BarometerWidgetUpdateForegroundService::class.java))
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        /**
         * ウィジェット更新
         * @param context Context
         * @param iconResId 天気ウィジェットのアイコン
         * @param text 天気ウィジェットのテキスト
         * */
        fun updateAppWidget(context: Context, iconResId: Int, text: String) {
            val widgetManager = context.getSystemService(Context.APPWIDGET_SERVICE) as AppWidgetManager
            val widgetIds = widgetManager.getAppWidgetIds(ComponentName(context, BarometerWeatherWidget::class.java))

            // ウィジェットの中身
            val views = RemoteViews(context.packageName, R.layout.barometer_weather_widget)
            views.setTextViewText(R.id.appwidget_weather_text, text)
            views.setImageViewResource(R.id.appwidget_weather_icon_imageview, iconResId)
            // 更新ボタン
            val updateIntent = Intent(context, BarometerWidgetUpdateForegroundService::class.java)
            val updatePendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                PendingIntent.getForegroundService(context, 2020, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            } else {
                PendingIntent.getService(context, 2020, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            }
            views.setOnClickPendingIntent(R.id.appwidget_weather_image_button, updatePendingIntent)
            // アプリ起動出来るように
            val mainActivityIntent = Intent(context, MainActivity::class.java)
            val mainActivityPendingIntent = PendingIntent.getActivity(context, 810, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.appwidget_parent, mainActivityPendingIntent)

            // 更新
            widgetIds.forEach { id -> widgetManager.updateAppWidget(id, views) }
        }
    }

}