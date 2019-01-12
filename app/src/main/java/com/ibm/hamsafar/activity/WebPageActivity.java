package com.ibm.hamsafar.activity;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.hamsafar.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by maryam on 1/9/2019.
 */
public class WebPageActivity extends Activity {

    private Context context = this;
    private Button toolbar_back = null;
    private TextView toolbar_title = null;
    private WebView page = null;
    private String url = null;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_page);

        page = findViewById(R.id.web_view);
        toolbar_back = findViewById(R.id.toolbar_back);
        toolbar_title = findViewById(R.id.toolbar_text);

        url = getIntent().getStringExtra("url");

        toolbar_back.setOnClickListener(view -> onBackPressed());

        toolbar_title.setText(getIntent().getStringExtra("title"));

        if( checkInternetConnection() ) {
            page.getSettings().setJavaScriptEnabled(true);
            page.loadUrl(url);
            page.getSettings().setBuiltInZoomControls(true);
        } else {
            Toast.makeText(context, getResources().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private boolean checkInternetConnection() {
        //if connected return true
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null || !info.isAvailable() || !info.isConnected()) {
            return false;
        }
        return true;
    }
}
