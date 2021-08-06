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
import android.widget.RemoteViews;

import java.util.List;

import io.github.takusan23.litebarometer.service.BarometerWidgetUpdateForegroundService;

/**
 * 気圧計ウィジェット。更新部分の実装は {@link BarometerWidgetUpdateForegroundService} を参照
 */
public class BarometerWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (final int appWidgetId : appWidgetIds) {
            final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.barometer_widget);

            //更新ボタン
            Intent buttonIntent = new Intent(context, BarometerWidgetUpdateForegroundService.class);
            PendingIntent servicePendingIntent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                servicePendingIntent = PendingIntent.getForegroundService(context, 2525, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            else {
                servicePendingIntent = PendingIntent.getService(context, 2525, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            views.setOnClickPendingIntent(R.id.appwidget_image_button, servicePendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
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

}

