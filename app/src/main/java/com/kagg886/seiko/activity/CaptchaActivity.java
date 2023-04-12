package com.kagg886.seiko.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kagg886.seiko.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * @param null:
 * @return null
 * @author kagg886
 * @description 不能用AlertDialog显示WebView，会有BUG且不知道应该如何解决
 * @date 2023/01/16 20:25
 */
public class CaptchaActivity extends AppCompatActivity implements View.OnClickListener {

    private List<WebResourceRequest> historyRequests = new ArrayList<>();

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captcha);
        WebView view = findViewById(R.id.activity_captcha_main);
        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setDomStorageEnabled(true);
        view.setWebViewClient(new WebViewClient() {
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (historyRequests == null) {
                    return super.shouldInterceptRequest(view,request);
                }
                historyRequests.add(request);
                if (request.getUrl().getPath().equals("/onVerifyCAPTCHA")) {
                    try {
                        JSONObject obj = new JSONObject(request.getUrl().getQueryParameter("p"));
                        Intent data = new Intent();
                        data.putExtra("ticket", obj.optString("ticket"));
                        setResult(RESULT_OK, data);
                        finish();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }
                return super.shouldInterceptRequest(view, request);
            }
        });
        view.loadUrl(getIntent().getStringExtra("url"));

        FloatingActionButton button = findViewById(R.id.activity_captcha_debug);
        button.setOnClickListener(this);

        Toast.makeText(this, R.string.slide_toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        historyRequests.clear();
        historyRequests = null;
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.slide_debug_title);
        StringBuilder builder1 = new StringBuilder();
        builder1.append(getText(R.string.slide_debug_description));

        for (WebResourceRequest request : historyRequests) {
            builder1.append("\n------\n").append("(").append(request.getMethod()).append(")").append(request.getUrl().toString());
            builder1.append("\nHeader:");
            for (Map.Entry<String,String> entry : request.getRequestHeaders().entrySet()) {
                builder1.append("\n").append(entry.getKey()).append("---").append(entry.getValue());
            }
        }

        EditText editText = new EditText(this);
        editText.setText(builder1.toString());
        builder.setView(editText);
        builder.create().show();
    }
}