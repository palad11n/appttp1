package com.whenupdate.tools.common;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.whenupdate.tools.R;
import com.whenupdate.tools.service.UpdateService;

import io.reactivex.Completable;

public class AppWidget extends AppWidgetProvider {
    private static final String ACTION_WIDGET_CLICKED = "ClickWidget";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        RemoteViews remoteViews;
        ComponentName watchWidget;

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.info_widget);
        watchWidget = new ComponentName(context, AppWidget.class);

        for (int id : appWidgetIds) {
            remoteViews.setOnClickPendingIntent(R.id.widget_btn,
                    getPendingSelfIntent(context, id, ACTION_WIDGET_CLICKED));
        }
        appWidgetManager.updateAppWidget(watchWidget, remoteViews);
    }

    protected PendingIntent getPendingSelfIntent(Context context, int appWidgetId, String action) {
        Intent intent = new Intent(context, AppWidget.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, appWidgetId, intent, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();

        if (ACTION_WIDGET_CLICKED.equals(action)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            RemoteViews remoteViews;
            ComponentName watchWidget;

            remoteViews = new RemoteViews(context.getPackageName(), R.layout.info_widget);
            watchWidget = new ComponentName(context, AppWidget.class);
            remoteViews.setTextViewText(R.id.widget_btn, "Run app...");
            appWidgetManager.updateAppWidget(watchWidget, remoteViews);
            try {
                Toast.makeText(context, context.getResources().getString(R.string.loading),
                        Toast.LENGTH_SHORT).show();
                Completable.fromAction(() -> {
                    UpdateService updateService = new UpdateService(context);
                    updateService.check();
                }).subscribe();

            } catch (Exception e) {
                Log.e("@@@", e.getMessage());
                e.printStackTrace();
            }

            remoteViews.setTextViewText(R.id.widget_btn, "WHENUPDATE");
            appWidgetManager.updateAppWidget(watchWidget, remoteViews);
        }
    }
}
