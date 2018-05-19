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

    public static String ACTION_WIDGET_SEARCH = "ActionReceiverSearch";
    public static String ACTION_WIDGET_FAVORITES = "ActionReceiverFavorites";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        applySearchActionConfiguration(context, remoteViews);
        applyFavoritesActionConfiguration(context, remoteViews);
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }

    private void applySearchActionConfiguration(Context context, RemoteViews remoteViews) {
        Intent active = new Intent(context, ProductSearchActivity.class);
        active.setAction(ACTION_WIDGET_SEARCH);
        PendingIntent actionPendingIntent = PendingIntent.getActivity(context, 0, active, 0);
        remoteViews.setOnClickPendingIntent(R.id.search_widget_edit_text, actionPendingIntent);
    }

    private void applyFavoritesActionConfiguration(Context context, RemoteViews remoteViews) {
        Intent active = new Intent(context, FavoriteListActivity.class);
        active.setAction(ACTION_WIDGET_FAVORITES);
        PendingIntent actionPendingIntent = PendingIntent.getActivity(context, 0, active, 0);
        remoteViews.setOnClickPendingIntent(R.id.favorite_list_widget_button, actionPendingIntent);
    }

}
