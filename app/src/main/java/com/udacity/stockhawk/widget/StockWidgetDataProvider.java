package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by shriyanshgautam on 04/01/17.
 */

public class StockWidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    Context context;
    private int stockWidgetId;
    private Cursor cursor;

    private final DecimalFormat dollarFormatWithPlus;
    private final DecimalFormat dollarFormat;
    private final DecimalFormat percentageFormat;



    public StockWidgetDataProvider(Context context, Intent intent){
        this.context = context;
        this.stockWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        // Refresh the cursor
        if (cursor != null) {
            cursor.close();
        }
        cursor = context.getContentResolver().query(Contract.Quote.URI, null, null,
                null, Contract.Quote.COLUMN_SYMBOL);
    }

    @Override
    public void onDestroy() {
        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {


            cursor.moveToPosition(position);
            String symbol,price,change,percentage;
            symbol = cursor.getString(Contract.Quote.POSITION_SYMBOL);
            price = dollarFormat.format(cursor.getFloat(Contract.Quote.POSITION_PRICE));

            change = dollarFormatWithPlus.format(cursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE));
            percentage = percentageFormat.format(cursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE)/ 100);


            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.list_item_quote);

            views.setTextViewText(R.id.symbol,symbol);
            views.setTextViewText(R.id.price,price);

            if (cursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE) > 0) {
                views.setInt(R.id.change, "setBackgroundResource",
                        R.drawable.percent_change_pill_green);
            } else {
                views.setInt(R.id.change, "setBackgroundResource",
                        R.drawable.percent_change_pill_red);
            }

            if (PrefUtils.getDisplayMode(context)
                    .equals(context.getString(R.string.pref_display_mode_absolute_key))) {
                views.setTextViewText(R.id.change,change);
            } else {
                views.setTextViewText(R.id.change,percentage);
            }

        final Intent fillInIntent = new Intent();
        fillInIntent.setAction(StockWidgetProvider.CLICK_ACTION);
        fillInIntent.putExtra(Contract.Quote.COLUMN_SYMBOL,symbol);
        fillInIntent.putExtra(Contract.Quote.COLUMN_HISTORY,cursor.getString(Contract.Quote.POSITION_HISTORY));
        views.setOnClickFillInIntent(R.id.list_item_container, fillInIntent);
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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
