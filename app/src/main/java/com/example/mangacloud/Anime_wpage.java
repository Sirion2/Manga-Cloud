package com.example.mangacloud;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class Anime_wpage extends AppCompatActivity {
    float x1, x2, y1, y2;
    WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //On create
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_wpage);

        String  url="https://jkanime.net/";

        shouldOverrideUrlLoading(webview, url);
    }

    private boolean shouldOverrideUrlLoading(WebView webview, String url) {
        if (url.equals(url)) {
            AdBlocker.init(this); //AdBlocker

            webview=(WebView)findViewById(R.id.WebView);
            webview.setWebViewClient(new WebViewClient());

            webview.getSettings().setDomStorageEnabled(true);
            WebSettings webSettings = webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setSupportMultipleWindows(true);

            webview.loadUrl(url);
        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent touchEvent) { //Slide effect
        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();

                if (x1 > x2) {
                    Intent i = new Intent(this, MainActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                    finish();
                }
                else if (x1 < x2){
                    Intent i = new Intent(this, Profile.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                    finish();
                }
                break;
        }
        return false;
    }

    private class MyBrowser extends WebViewClient { //the AdBlocker Stuff
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        private Map<String, Boolean> loadedUrls = new HashMap<>();
        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            boolean ad;
            if (!loadedUrls.containsKey(url)) {
                ad = AdBlocker.isAd(url);
                loadedUrls.put(url, ad);
            } else {
                ad = loadedUrls.get(url);
            }
            return ad ? AdBlocker.createEmptyResource() :
                    super.shouldInterceptRequest(view, url);
        }
    }
}