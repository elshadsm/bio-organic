package com.elshadsm.organic.bio.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.elshadsm.organic.bio.R;
import com.elshadsm.organic.bio.activities.FavoriteListActivity;
import com.elshadsm.organic.bio.activities.ProductSearchActivity;

public class BioOrganicWidgetProvider extends AppWidgetProvider {

    // our actions for our buttons
    public static String ACTION_WIDGET_SEARCH = "ActionReceiverSearch";
    public static String ACTION_WIDGET_FAVORITES = "ActionReceiverFavorites";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        Intent active = new Intent(context, ProductSearchActivity.class);
        active.setAction(ACTION_WIDGET_SEARCH);
        PendingIntent actionPendingIntent = PendingIntent.getActivity(context, 0, active, 0);
        remoteViews.setOnClickPendingIntent(R.id.search_widget_edit_text, actionPendingIntent);

        active = new Intent(context, FavoriteListActivity.class);
        active.setAction(ACTION_WIDGET_FAVORITES);
        actionPendingIntent = PendingIntent.getActivity(context, 0, active, 0);
        remoteViews.setOnClickPendingIntent(R.id.favorite_list_widget_button, actionPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_WIDGET_SEARCH)) {
            System.out.println(ACTION_WIDGET_SEARCH);
        } else if (intent.getAction().equals(ACTION_WIDGET_FAVORITES)) {
            System.out.println(ACTION_WIDGET_FAVORITES);
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Perform any action when an AppWidget for this provider is instantiated
    }

    @Override
    public void onDisabled(Context context) {
        // Perform any action when the last AppWidget instance for this provider is deleted
    }

}
