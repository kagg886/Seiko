package com.kagg886.seiko.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.kagg886.seiko.R;

public class SMSActivity extends AppCompatActivity {
    public static final int RESULT_RETRY = 2;
    private Button retry, submit;
    private TextView text;
    private EditText verify;

    private static long sendTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        sendTime = System.currentTimeMillis();
        retry = findViewById(R.id.activity_sms_retry);
        submit = findViewById(R.id.activity_sms_submit);
        text = findViewById(R.id.activity_sms_message);
        verify = findViewById(R.id.activity_sms_verify);
        Intent data = getIntent();

        text.setText(String.format(getText(R.string.sms_tips).toString(), data.getStringExtra("country"), data.getStringExtra("phone")));

        submit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(verify.getText().toString())) {
                Snackbar.make(getWindow().getDecorView(), R.string.sms_code_empty, BaseTransientBottomBar.LENGTH_LONG).show();
                return;
            }
            Intent o = new Intent();
            o.putExtra("code", verify.getText().toString());
            setResult(RESULT_OK, o);
            finish();
        });

        retry.setOnClickListener(v -> {
            if (System.currentTimeMillis() - sendTime < 60000) {
                long time = 60 - (System.currentTimeMillis() - sendTime) / 1000;
                Snackbar.make(getWindow().getDecorView(), String.format(getText(R.string.sms_retry).toString(), time), Snackbar.LENGTH_LONG).show();
                return;
            }
            setResult(RESULT_RETRY, new Intent());
            finish();
        });
    }
}