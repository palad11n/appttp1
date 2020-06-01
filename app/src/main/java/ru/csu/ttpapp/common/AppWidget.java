package ru.csu.ttpapp.common;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import ru.csu.ttpapp.R;
import ru.csu.ttpapp.mvp.MainActivity;

public class AppWidget extends AppWidgetProvider {
    private static String ACTION_WIDGET_CLICKED = "ClickWidget";
    private RemoteViews remoteViews;
    private ComponentName watchWidget;
    private AppWidgetManager appWidgetManager;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        if (appWidgetManager == null)
            this.appWidgetManager = appWidgetManager;
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.info_widget);
        watchWidget = new ComponentName(context, AppWidget.class);

        remoteViews.setOnClickPendingIntent(R.id.widget_btn, getPendingSelfIntent(context, ACTION_WIDGET_CLICKED));
        appWidgetManager.updateAppWidget(watchWidget, remoteViews);
        //  Log.i("@@@@@@@@", "UPDATE!!!!!");
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();

        if (ACTION_WIDGET_CLICKED.equals(action)) {
            if (appWidgetManager == null)
                this.appWidgetManager = AppWidgetManager.getInstance(context);
            MainActivity.presenter.loadUpdate();
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.info_widget);
            watchWidget = new ComponentName(context, AppWidget.class);
            // remoteViews.setTextViewText(R.id.widget_btn, "TESTING");
            appWidgetManager.updateAppWidget(watchWidget, remoteViews);
            // Log.i("@@@@@@@@", "click!!!");
        }
    }
}
