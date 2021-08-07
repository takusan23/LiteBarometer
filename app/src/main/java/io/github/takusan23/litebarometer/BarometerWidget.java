package io.github.takusan23.litebarometer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.widget.RemoteViews;

import java.util.Arrays;
import java.util.List;

import io.github.takusan23.litebarometer.service.BarometerWidgetUpdateForegroundService;

/**
 * 気圧計ウィジェット。更新部分の実装は {@link BarometerWidgetUpdateForegroundService} を参照
 */
public class BarometerWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // 更新用Service起動
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, BarometerWidgetUpdateForegroundService.class));
        } else {
            context.startService(new Intent(context, BarometerWidgetUpdateForegroundService.class));
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    /**
     * ウィジェット更新メソッド
     */
    public static void updateWidget(Context context, String text) {
        AppWidgetManager appWidgetManager = (AppWidgetManager) context.getSystemService(Context.APPWIDGET_SERVICE);
        int[] idList = appWidgetManager.getAppWidgetIds(new ComponentName(context, BarometerWidget.class));

        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.barometer_widget);
        views.setTextViewText(R.id.appwidget_text, text);
        // 更新ボタン
        Intent buttonIntent = new Intent(context, BarometerWidgetUpdateForegroundService.class);
        PendingIntent servicePendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            servicePendingIntent = PendingIntent.getForegroundService(context, 2525, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        else {
            servicePendingIntent = PendingIntent.getService(context, 2525, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        views.setOnClickPendingIntent(R.id.appwidget_image_button, servicePendingIntent);
        // アプリ起動出来るように
        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(context, 4545, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.appwidget_parent, mainActivityPendingIntent);

        for (int id : idList) {
            appWidgetManager.updateAppWidget(id, views);
        }
    }

}

