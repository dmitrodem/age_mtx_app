package org.demidrol.age_mtx;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class GenericInfoActivity extends Activity implements View.OnLongClickListener {
    private TextView tvInfo;
    private ScrollView svInfo;
    private static final int CREATE_FILE_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_info);
        svInfo = findViewById(R.id.svInfo);
        tvInfo = findViewById(R.id.tvInfo);
        svInfo.setOnLongClickListener(this);
        tvInfo.setOnLongClickListener(this);

        Intent intent = getIntent();
        Serializable info = intent.getSerializableExtra("INFO", Serializable.class);
        tvInfo.append(info.toString());
        tvInfo.append("\n");
        svInfo.post(() -> svInfo.fullScroll(View.FOCUS_DOWN));
    }

    @Override
    public boolean onLongClick(View view) {
        createFile();
        return true;
    }

    private void createFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "info.txt");

        startActivityForResult(intent, CREATE_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            saveTextToFile(uri);
        }
    }

    private void saveTextToFile(Uri uri) {
        try {
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "w");
            FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor());

            String text = tvInfo.getText().toString();
            fos.write(text.getBytes());

            fos.close();
            pfd.close();

            Toast.makeText(this, "File saved successfully!", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
