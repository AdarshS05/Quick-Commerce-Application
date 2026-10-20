package com.example.quickcommerceapplication.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.quickcommerceapplication.R;
import com.example.quickcommerceapplication.database.DatabaseHelper;
import com.example.quickcommerceapplication.utils.Constants;

public class OrderTrackingService extends Service {
    private static final String CHANNEL_ID = "order_tracking";
    private static final int NOTIFICATION_ID = 17;
    private static final String[] STAGES = {
            "Order Placed", "Packed", "Out for Delivery", "Delivered"
    };
    private final Handler handler = new Handler(Looper.getMainLooper());
    private int stageIndex;
    private long orderId;

    @Override public void onCreate() {
        super.onCreate();
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(new NotificationChannel(CHANNEL_ID,
                "Order tracking", NotificationManager.IMPORTANCE_LOW));
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            stopSelf(startId);
            return START_NOT_STICKY;
        }
        orderId = intent.getLongExtra(Constants.EXTRA_ORDER_ID, -1);
        if (orderId < 0) {
            stopSelf(startId);
            return START_NOT_STICKY;
        }
        stageIndex = 1;
        startForeground(NOTIFICATION_ID, notification(STAGES[0]));
        scheduleNextStage();
        return START_NOT_STICKY;
    }

    private void scheduleNextStage() {
        handler.postDelayed(() -> {
            if (stageIndex >= STAGES.length) {
                stopSelf();
                return;
            }
            String stage = STAGES[stageIndex++];
            new DatabaseHelper(this).updateOrderStatus(orderId, stage);
            sendStatus(stage);
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                    .notify(NOTIFICATION_ID, notification(stage));
            if (stageIndex < STAGES.length) scheduleNextStage();
            else stopSelf();
        }, 8000);
    }

    private Notification notification(String status) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("QuickCommerce order")
                .setContentText(status)
                .setOngoing(!"Delivered".equals(status))
                .build();
    }

    private void sendStatus(String status) {
        Intent update = new Intent(Constants.ACTION_ORDER_STATUS_UPDATE);
        update.putExtra(Constants.EXTRA_ORDER_ID, orderId);
        update.putExtra(Constants.EXTRA_ORDER_STATUS, status);
        LocalBroadcastManager.getInstance(this).sendBroadcast(update);
    }

    @Override public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Nullable @Override public IBinder onBind(Intent intent) { return null; }
}

