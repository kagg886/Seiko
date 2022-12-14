package com.kagg886.seiko.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import org.json.JSONException;
import org.json.JSONObject;

public class CaptchaActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView view = new WebView(this);
        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setDomStorageEnabled(true);
        view.setWebViewClient(new WebViewClient() {
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (request.getUrl().getPath().equals("/onVerifyCAPTCHA")) {
                    try {
                        JSONObject obj = new JSONObject(request.getUrl().getQueryParameter("p"));
                        Intent data = new Intent();
                        data.putExtra("ticket",obj.optString("ticket"));
                        setResult(RESULT_OK,data);
                        finish();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }
                return super.shouldInterceptRequest(view, request);
            }
        });
        view.loadUrl(getIntent().getStringExtra("url"));
        setContentView(view);
    }
}