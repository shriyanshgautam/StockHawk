package com.udacity.stockhawk.widget;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.ui.MainActivity;
import com.udacity.stockhawk.ui.StockDetailActivity;

import yahoofinance.Stock;

/**
 * Created by shriyanshgautam on 04/01/17.
 */

public class StockWidgetProvider extends AppWidgetProvider {

    public static final String CLICK_ACTION = "com.udacity.stockhawk.widget.StockWidgetProvider.CLICK_ACTION";
    public static final String REFRESH_ACTION = "com.udacity.stockhawk.widget.StockWidgetProvider.REFRESH_ACTION";
    public static final String ADD_ACTION = "com.udacity.stockhawk.widget.StockWidgetProvider.ADD_ACTION";

    public StockWidgetProvider(){
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if(action.equals(CLICK_ACTION)){
            final String symbol = intent.getStringExtra(Contract.Quote.COLUMN_SYMBOL);
            final String history = intent.getStringExtra(Contract.Quote.COLUMN_HISTORY);
            Intent stockIntent = new Intent(context, StockDetailActivity.class);
            stockIntent.putExtra(Contract.Quote.COLUMN_SYMBOL,symbol);
            stockIntent.putExtra(Contract.Quote.COLUMN_HISTORY,history);
            stockIntent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(stockIntent);
        }else if(intent.getAction().equalsIgnoreCase(REFRESH_ACTION)){
            updateWidget(context);
        }else if(intent.getAction().equalsIgnoreCase(ADD_ACTION)){
            Intent addIntent = new Intent(context,MainActivity.class);
            addIntent.setAction(ADD_ACTION);
            addIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(addIntent);
        }
        super.onReceive(context, intent);
    }

    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = initViews(context,appWidgetManager, R.layout.stock_widget);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.widget_symbol, pendingIntent);

        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_symbol, mainPendingIntent);


        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private RemoteViews initViews(Context context,
                                  AppWidgetManager widgetManager, int widgetId) {

        RemoteViews view = new RemoteViews(context.getPackageName(),
                R.layout.stock_widget);

        Intent intent = new Intent(context, StockWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);



        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        view.setRemoteAdapter(widgetId, R.id.widget_list, intent);

        final Intent onClickIntent = new Intent(context, StockWidgetProvider.class);
        onClickIntent.setAction(CLICK_ACTION);
        onClickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        onClickIntent.setData(Uri.parse(onClickIntent.toUri(Intent.URI_INTENT_SCHEME)));
        final PendingIntent onClickPendingIntent = PendingIntent.getBroadcast(context, 0,
                onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setPendingIntentTemplate(R.id.widget_list, onClickPendingIntent);


        Intent updateIntent = new Intent(context, StockWidgetProvider.class);
        updateIntent.setAction(REFRESH_ACTION);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetId);
        PendingIntent pendingIntentRefresh = PendingIntent.getBroadcast(context,0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.widget_update, pendingIntentRefresh);


        Intent addIntent = new Intent(context, StockWidgetProvider.class);
        addIntent.setAction(ADD_ACTION);
        addIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetId);
        PendingIntent pendingIntentAdd = PendingIntent.getBroadcast(context,0, addIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.widget_add, pendingIntentAdd);

        updateWidget(context);


        return view;
    }

    private void updateWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, StockWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds,R.id.widget_list);
    }


}
