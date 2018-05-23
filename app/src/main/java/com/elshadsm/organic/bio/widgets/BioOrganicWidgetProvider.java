package com.elshadsm.organic.bio.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.elshadsm.organic.bio.R;
import com.elshadsm.organic.bio.activities.FavoriteListActivity;
import com.elshadsm.organic.bio.activities.ProductSearchActivity;
import com.elshadsm.organic.bio.data.ProductsDao;
import com.elshadsm.organic.bio.models.Product;

import java.util.ArrayList;
import java.util.List;

import static com.elshadsm.organic.bio.models.Constants.WIDGET_EXTRA_NAME_INGREDIENT_FAVORITES;
import static com.elshadsm.organic.bio.models.Constants.WIDGET_EXTRA_NAME_INGREDIENT_PRICES;

public class BioOrganicWidgetProvider extends AppWidgetProvider {

    public static String ACTION_WIDGET_SEARCH = "ActionReceiverSearch";
    public static String ACTION_WIDGET_FAVORITES = "ActionReceiverFavorites";

    private ProductsDao productsDao;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        productsDao = new ProductsDao(context.getContentResolver());
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        applySearchActionConfiguration(context, remoteViews);
        applyFavoritesActionConfiguration(context, remoteViews);
        for (int appWidgetId : appWidgetIds) {
            updateWidgetListView(context, remoteViews, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
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

    private void updateWidgetListView(Context context, RemoteViews remoteViews, int appWidgetId) {
        Intent intent = new Intent(context, ListRemoteViewsService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        remoteViews.setRemoteAdapter(R.id.widget_list_view, intent);
        remoteViews.setEmptyView(R.id.widget_list_view, R.id.empty_view);
        appendListValues(intent);
    }

    private void appendListValues(Intent intent) {
        List<Product> productList = productsDao.getFavoriteProducts();
        ArrayList<String> favoriteList = new ArrayList<>();
        ArrayList<String> priceList = new ArrayList<>();
        for (Product product : productList) {
            favoriteList.add(product.getName());
            priceList.add(String.format("%s $", product.getPrice()));
        }
        intent.putStringArrayListExtra(WIDGET_EXTRA_NAME_INGREDIENT_FAVORITES, favoriteList);
        intent.putStringArrayListExtra(WIDGET_EXTRA_NAME_INGREDIENT_PRICES, priceList);
    }

}
