package com.garyle.downloadersample;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadWorker extends Worker {

    private static final String CHANNEL_ID = "downloader_notification_sample_channel";
    private static final int NOTIFICATION_ID = 1;
    private static final int NOTIFICATION_ID_SUCCESS = 2;

    public DownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String url = getInputData().getString("file_url");
        String outputFilePath = getInputData().getString("output_file_path");

        showNotification("Download Starting", NOTIFICATION_ID);
        try {
            downloadFile(url, outputFilePath);
            clearNotification(NOTIFICATION_ID);
            showNotification("Download Success", NOTIFICATION_ID_SUCCESS);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            showNotification("Download Failed", NOTIFICATION_ID);
            return Result.failure();
        }
    }

    private void downloadFile(String fileUrl, String outputFilePath) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();

        // check jika url http response tidak ok
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP Error Code: " + connection.getResponseCode());
        }

        // input stream & file output Stream
        InputStream inputStream = new BufferedInputStream(connection.getInputStream());
        FileOutputStream fileOutputStream = new FileOutputStream(outputFilePath);

        byte[] buffer = new byte[1024];
        int byteRead;
        while((byteRead = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, byteRead);
        }

        fileOutputStream.close();
        inputStream.close();
        connection.disconnect();
    }


    private void showNotification(String message, int notificationId) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "downloader_notification_sample_channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );

        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Download Status")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(notificationId, builder.build());
    }


    private void clearNotification(int notificationId) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }
}
