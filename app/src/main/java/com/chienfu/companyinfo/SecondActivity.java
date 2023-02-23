package com.chienfu.companyinfo;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;

/**
 * Created by isenw on 2023/2/23.
 */

public class SecondActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1 ;
    private TextView tvResult;
    private Button btnSendEmail;
    private Button btnExport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        tvResult = (TextView)findViewById(R.id.tvResult);
        btnSendEmail = (Button)findViewById(R.id.btnSendEmail);
        btnExport = (Button)findViewById(R.id.btnExport);

        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        String name = pref.getString("name", "");
        String phone = pref.getString("phone", "");

        tvResult.setText("Name: " + name + "\nPhone: " + phone);

        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Send email
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Company Information");
                emailIntent.putExtra(Intent.EXTRA_TEXT, tvResult.getText().toString());
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(SecondActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("MyApp", "Requesting permission to write to external storage");

                    ActivityCompat.requestPermissions(SecondActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    Log.d("MyApp", "Requesting permission to write to external storage");

                } else {
                    Log.d("MyApp", "Permission already granted");
                    // Permission has already been granted
                    // TODO: Export data to .csv file
                    exportData();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MyApp", "Permission granted");
                    exportData();
                } else {
                    Log.d("MyApp", "Permission denied");
                }
                break;
        }
    }

    private void exportData(){
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        String name = pref.getString("name", "");
        String phone = pref.getString("phone", "");

        File folder = new File(Environment.getExternalStorageDirectory(), "MyApp");
        if (!folder.exists()) {
            folder.mkdir();
            Toast.makeText(SecondActivity.this, "make folder successfully!", Toast.LENGTH_SHORT).show();
        }
        File csvFile = new File(folder, "company_info.csv");

        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.write("Name,Phone\n");
            writer.write(name + "," + phone + "\n");
            writer.flush();
            Toast.makeText(SecondActivity.this, "Data exported successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(SecondActivity.this, "Failed to export data!", Toast.LENGTH_SHORT).show();
        }
    }
}