package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by shriyanshgautam on 04/01/17.
 */

public class StockWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        StockWidgetDataProvider dataProvider = new StockWidgetDataProvider(
                getApplicationContext(), intent);
        return dataProvider;
    }
}
