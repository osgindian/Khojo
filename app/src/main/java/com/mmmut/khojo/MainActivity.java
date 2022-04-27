package com.mmmut.khojo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");

    SQLiteDatabase database;
    SwipeRefreshLayout swipe;
    WebView webView;
    ImageButton del,share,settings;
    EditText path;
    String appTitle="",appUrl="";
    boolean errorFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        database = this.openOrCreateDatabase("Khojo",MODE_PRIVATE,null);
        database.execSQL("CREATE TABLE IF NOT EXISTS `history` (`title` VARCHAR(100),`url` TEXT,`date` VARCHAR(8),`time` VARCHAR(6))");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initWebView();
            }
        });

        path = (EditText) findViewById(R.id.path);
        path.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    webView = (WebView) findViewById(R.id.webView);
                    String url = String.valueOf(path.getText());
                    if(url == null || url.trim().length() == 0)
                        return false;
                    url = protocolCheck(url);
                    errorFlag = false;
                    webView.loadUrl(url);
                    return true;
                }
                return false;
            }
        });

        del = (ImageButton)findViewById(R.id.del);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                path = (EditText) findViewById(R.id.path);
                path.setText("");
                appUrl = "";
            }
        });

        share = (ImageButton)findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(errorFlag){
                    Toast.makeText(getApplicationContext(),"Share not possible",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, appTitle + "\n" + appUrl);
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });

        settings = (ImageButton)findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Settings.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        String u = intent.getStringExtra("URL");
        if(u != null)
            path.setText(u);


        initWebView();
    }

    public void setWebViewSettings(){
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        swipe.setRefreshing(true);
        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                errorFlag = true;
                webView.loadData("<h1>Oops!</h1><p>Please enter correct URL</p><p>"+failingUrl+"</p><p>"+errorCode+"</p><p>"+description+"</p>","text/html","utf-8");
            }
            public void onPageFinished(WebView view, String url) {
                path = (EditText) findViewById(R.id.path);
                if(errorFlag){
                    appTitle = "Sorry";
                    appUrl = "";
                    setTitle(appTitle);
                    path.setText(appUrl);
                    return;
                }
                else{
                    appTitle = view.getTitle();
                    appUrl = url;
                    setTitle(appTitle);
                    path.setText(appUrl);
                }
                if(appTitle.length() > 100)
                    appTitle = appTitle.substring(0,100);
                Date date = new Date();
                String d = dateFormat.format(date);
                String t = timeFormat.format(date);
                database.execSQL("INSERT INTO `history` VALUES ('"+appTitle+"', '"+url+"', '"+d+"', '"+t+"')");
                swipe.setRefreshing(false);
            }
        });
    }

    public void initWebView() {
        setWebViewSettings();
        path = (EditText) findViewById(R.id.path);
        appUrl = String.valueOf(path.getText());
        if(appUrl == null || appUrl.length() == 0) {
            SharedPreferences sharedPreferences = this.getSharedPreferences("com.mmmut.khojo", Context.MODE_PRIVATE);
            appUrl = sharedPreferences.getString("homePage", "https://google.com");
        }
        appUrl = protocolCheck(appUrl);
        path.setText(appUrl);
        webView.loadUrl(appUrl);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }

    public String protocolCheck(String url){
        if(url.indexOf("http") < 0)
            return "http://"+url;
        return url;
    }
}