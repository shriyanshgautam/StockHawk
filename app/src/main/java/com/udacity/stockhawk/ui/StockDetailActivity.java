package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StockDetailActivity extends AppCompatActivity {

    String stockSymbol;
    String stockHistory;
    List<String> stockHistoryList;
    LineChart chart;
    DecimalFormat dollarFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        init();

        stockHistory.split("\\n");
        stockHistoryList = Arrays.asList(stockHistory.split("\\n"));


        List<Entry> entries = new ArrayList<>();
        for(int i=0;i<stockHistoryList.size();i++){
            String[] item = stockHistoryList.get(stockHistoryList.size()-i-1).split(", ");
            Log.d("Item",item[0]+":"+item[1]+" "+i);
            entries.add(new Entry((float)i,Float.parseFloat(item[1].trim())));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Label");
        dataSet.setColor(getResources().getColor(R.color.colorPrimary));
        LineData lineData = new LineData(dataSet);


        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if(value<stockHistoryList.size()&&value>0){
                    String date=stockHistoryList.get((int)(stockHistoryList.size()-value-1)).split(", ")[0];
                    DateFormat sdf = new SimpleDateFormat("dd MMM yy");
                    Date netDate = (new Date(Long.parseLong(date)));
                    return sdf.format(netDate);
                }else{
                    return "";
                }
            }
        };


        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(formatter);
        xAxis.setCenterAxisLabels(true);
        chart.invalidate(); // refresh

    }

    private void init(){
        chart = (LineChart) findViewById(R.id.chart);
        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);

        Intent intent = getIntent();
        stockSymbol = intent.getStringExtra(Contract.Quote.COLUMN_SYMBOL);
        stockHistory = intent.getStringExtra(Contract.Quote.COLUMN_HISTORY);
        setTitle(stockSymbol);
    }
}

