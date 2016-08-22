package com.androidclass.fetchasynctask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView stockInfo;
    EditText stockList;
    Button fetchNow;

    StockService stockService;
    ListView lv;
    SimpleAdapter myAdapter;

    List<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();

    ProgressBar prBar;


    String[] from = { Constants.KEY_SYMBOL, Constants.KEY_CURPRICE,
            Constants.KEY_NAME };
    int[] to = { R.id.sym, R.id.price, R.id.name };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stockList = (EditText) findViewById(R.id.stocksToFetch);
        fetchNow = (Button) findViewById(R.id.btnFetch);
        prBar=(ProgressBar) findViewById(R.id.pr_bar);

        fetchNow.setOnClickListener(this);

        stockService = new StockService();

        lv = (ListView) findViewById(R.id.stock_list);

        myAdapter = new SimpleAdapter(this, al, R.layout.item_row, from, to);

        lv.setAdapter(myAdapter);

    }



    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnFetch) {
            String stocks = stockList.getText().toString().trim();
            if ((stocks != null) && (stocks.length() > 0)) {
                new FetchStockInfofromInternet().execute(stocks);

            } else {
                Toast.makeText(getBaseContext(), "Please enter stock symbols",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }


    // Async Task Class
    class FetchStockInfofromInternet extends AsyncTask<String, String, String> {

        List<HashMap<String, String>> nl;

        // Show Progress bar before fetching
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Shows Progress Bar Dialog and then call doInBackground method
            prBar.setVisibility(View.VISIBLE);
            lv.setVisibility(View.GONE);
        }


        @Override
        protected String doInBackground(String... stocks) {
            int count;
            try {
                String stocksToFetch = stocks[0].replace(" ", "+");

                nl = stockService.getStockInformation(stocksToFetch);

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }


        // Once stock info has been downloaded

        @Override
        protected void onPostExecute(String stocks) {

            prBar.setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);
            if ((nl!=null) && (nl.size() > 0)) {
                al.clear();
                al.addAll(nl);
                myAdapter.notifyDataSetChanged();


            }
            else {

                Toast.makeText(getBaseContext(), "No Stock Information Found",
                        Toast.LENGTH_SHORT).show();

            }

        }
    }
}
