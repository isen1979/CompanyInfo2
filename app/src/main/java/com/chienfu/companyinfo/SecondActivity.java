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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by isenw on 2023/2/23.
 */

public class SecondActivity extends AppCompatActivity {

    private TextView tvResult;
    private Button btnSendEmail;
    private Button btnExport;

    private static Retrofit retrofit = null;

    private SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
    private String name = pref.getString("name", "");
    private String phone = pref.getString("phone", "");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        tvResult = (TextView)findViewById(R.id.tvResult);
        btnSendEmail = (Button)findViewById(R.id.btnSendEmail);
        btnExport = (Button)findViewById(R.id.btnExport);

        tvResult.setText("Name: " + name + "\nPhone: " + phone);
        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Send email
                sendmail();
            }
        });

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Export data to Synology Drive
                exportSynologyDrive();
            }
        });
    }

    private void sendmail(){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Company Information");
        emailIntent.putExtra(Intent.EXTRA_TEXT, tvResult.getText().toString());
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private void exportSynologyDrive(){
        // Create Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.synology.com/") //如有需要可替換URL，以符合使用環境
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create a SynologyDriveService instance
        SynologyDriveService service = retrofit.create(SynologyDriveService.class);

        String accessTokenValue = "your_access_token";
        String fileNameValue = "data.csv";
        String fileContentValue = "Name, Phone\nJohn Doe,123-456-7890";

        RequestBody accessToken = RequestBody.create(MediaType.parse("text/plain"), accessTokenValue);
        RequestBody action = RequestBody.create(MediaType.parse("text/plain"), "create");
        RequestBody path = RequestBody.create(MediaType.parse("text/plain"), "/");
        RequestBody fileName = RequestBody.create(MediaType.parse("text/plain"), fileNameValue);
        RequestBody fileContent = RequestBody.create(MediaType.parse("text/plain"), fileContentValue);

        // Create a request to upload a file
        Call<UploadResponse> call = service.uploadFile( accessToken, action, path, fileName, fileContent.getBytes() );

        // Execute the request
        call.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("SecondActivity", "File uploaded successfully");
                } else {
                    Log.e("SecondActivity", "Failed to upload file");
                }
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                Log.e("SecondActivity", "Failed to upload file: " + t.getMessage());
            }
        });
    }
}