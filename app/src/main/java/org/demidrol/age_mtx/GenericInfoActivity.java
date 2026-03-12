package org.demidrol.age_mtx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.Serializable;

public class GenericInfoActivity extends Activity {
    private TextView tvInfo;
    private ScrollView svInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_info);
        svInfo = findViewById(R.id.svInfo);
        tvInfo = findViewById(R.id.tvInfo);

        Intent intent = getIntent();
        Serializable info = intent.getSerializableExtra("INFO", Serializable.class);
        tvInfo.append(info.toString());
        tvInfo.append("\n");
        svInfo.post(() -> svInfo.fullScroll(View.FOCUS_DOWN));
    }
}
