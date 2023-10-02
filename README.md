# Downloader Sample
Sample Downloader Using WorkManager &amp; HttpURLConnection in Java

# How To Use
To use this project. Please change the URL in the MainActivity.java file
```java
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
```
Adjust the file extension to the file you want to download.

## To modify the notification, please visit the DownloadWorker.java file.
Please customize the Icon, Title, and Content text as you wish.
```java
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
```

# Authors
- [@dhika_prana](https://www.instagram.com/dhika_prana/)
