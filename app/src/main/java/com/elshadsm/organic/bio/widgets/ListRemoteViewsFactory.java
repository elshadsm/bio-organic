package com.elshadsm.organic.bio.widgets;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.elshadsm.organic.bio.R;

import java.util.ArrayList;

import static com.elshadsm.organic.bio.models.Constants.WIDGET_EXTRA_NAME_INGREDIENT_FAVORITES;
import static com.elshadsm.organic.bio.models.Constants.WIDGET_EXTRA_NAME_INGREDIENT_PRICES;

public class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final Context context;
    private final ArrayList<String> favoriteList;
    private final ArrayList<String> priceList;

    ListRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
        favoriteList = intent.getStringArrayListExtra(WIDGET_EXTRA_NAME_INGREDIENT_FAVORITES);
        priceList = intent.getStringArrayListExtra(WIDGET_EXTRA_NAME_INGREDIENT_PRICES);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return favoriteList != null ? favoriteList.size() : 0;
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_favorite_list_item);
        views.setTextViewText(R.id.text_favorite_name, favoriteList.get(i));
        views.setTextViewText(R.id.text_favorite_price, priceList.get(i));
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
