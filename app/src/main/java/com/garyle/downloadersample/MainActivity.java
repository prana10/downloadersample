package com.garyle.downloadersample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // isi dengan url file zip yang ingin di download
        // pastikan cek url nya secara benar agar file bisa di download
        String url = "";

        // file lokasi output berada di : /data/user/0/com.garyle.downloadersample/files/file.zip
        // ganti ekstensi file .zip nya dengan file yang ingin di download
        String outputFilePath = getFilesDir().getAbsolutePath() + "/file.zip";

        Data inputData = new Data.Builder()
                .putString("file_url", url)
                .putString("output_file_path", outputFilePath)
                .build();

        OneTimeWorkRequest downloadWorkRequest = new OneTimeWorkRequest.Builder(DownloadWorker.class)
                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setInputData(inputData)
                .build();

        triggerDownloadOnWiFi(downloadWorkRequest);
    }

    private void triggerDownloadOnWiFi(OneTimeWorkRequest downloadWorkRequest) {
        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                WorkManager.getInstance(MainActivity.this).enqueue(downloadWorkRequest);
                // mencabut koneksi saat download sudah selesai
                unregisterNetworkCallback(this);
            }
        };

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
    }

    private void unregisterNetworkCallback(ConnectivityManager.NetworkCallback networkCallback) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }
}