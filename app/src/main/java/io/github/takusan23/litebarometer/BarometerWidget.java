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

/**
 * Implementation of App Widget functionality.
 */
public class BarometerWidget extends AppWidgetProvider {

    //センサー
    SensorManager sensorManager;
    SensorEventListener sensorEventListener;

    boolean isGet = false;

    int[] ids;

    static void updateAppWidget(Context context, RemoteViews remoteViews) {

        ComponentName myWidget = new ComponentName(context, BarometerWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);

       // final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.barometer_widget);
       // Intent buttonIntent = new Intent(context, BarometerWidget.class);
       // PendingIntent btn1Pending = PendingIntent.getBroadcast(context, 0, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
       // views.setOnClickPendingIntent(R.id.appwidget_image_button, btn1Pending);

        manager.updateAppWidget(myWidget, remoteViews);

/*
        CharSequence widgetText = "気圧計";
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.barometer_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
*/
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (final int appWidgetId : appWidgetIds) {

            final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.barometer_widget);

            //気圧取得
            //getBarometer(context, views, appWidgetManager, appWidgetId);

            //更新ボタン
            Intent buttonIntent = new Intent(context, BarometerWidget.class);
            PendingIntent btn1Pending = PendingIntent.getBroadcast(context, 0, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.appwidget_image_button, btn1Pending);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);
        //気圧取得
        //Boolean型を入れ替えて使うことにする
        //一定間隔で呼ばれるのでここで気圧を取得する

        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.barometer_widget);

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_PRESSURE);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                int barometer = Math.round(sensorEvent.values[0]);
                float barometerFloat = sensorEvent.values[0];
                String text = barometer + " hPa" + "\n" + "(" + barometerFloat + " hPa" + ")";
                views.setTextViewText(R.id.appwidget_text, text);
                //更新
                updateAppWidget(context, views);

                //登録解除
                sensorManager.unregisterListener(sensorEventListener);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        sensorManager.registerListener(
                sensorEventListener,
                sensorList.get(0),
                SensorManager.SENSOR_DELAY_NORMAL
        );

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private void getBarometer(Context context, final RemoteViews views, final AppWidgetManager appWidgetManager, final int appWidgetId) {
        //一定間隔で呼ばれるのでここで気圧を取得する
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_PRESSURE);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                int barometer = Math.round(sensorEvent.values[0]);
                float barometerFloat = sensorEvent.values[0];
                String text = barometer + " hPa" + "\n" + "(" + barometerFloat + " hPa" + ")";
                views.setTextViewText(R.id.appwidget_text, text);
                appWidgetManager.updateAppWidget(appWidgetId, views);
                sensorManager.unregisterListener(sensorEventListener);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        sensorManager.registerListener(
                sensorEventListener,
                sensorList.get(0),
                SensorManager.SENSOR_DELAY_NORMAL
        );
    }

}

