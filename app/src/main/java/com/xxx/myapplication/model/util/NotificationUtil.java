package com.xxx.myapplication.model.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;

import com.xxx.myapplication.R;
import com.xxx.myapplication.ui.activity.chat.ChatActivity;
import com.xxx.myapplication.model.db.entry.WebSocketMessageBean;

/**
 * 通知栏工具栏
 */
public class NotificationUtil {

    //是否开启了通知栏
    public static boolean isOpen(final Context context) {
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            return true;
        } else {
            final AlertDialog dialog = new AlertDialog.Builder(context).create();
            dialog.show();
            View view = View.inflate(context, R.layout.window_not_notification, null);
            dialog.setContentView(view);
            view.findViewById(R.id.window_not_notification_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                    Intent localIntent = new Intent();
                    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (Build.VERSION.SDK_INT >= 9) {
                        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
                    } else if (Build.VERSION.SDK_INT <= 8) {
                        localIntent.setAction(Intent.ACTION_VIEW);
                        localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                        localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
                    }
                    context.startActivity(localIntent);
                }
            });
        }
        return false;
    }

    public static void showNotification(Context context, WebSocketMessageBean bean, String messageName, long id) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("userId", bean.getSendId());
        intent.putExtra("userName", messageName);

        PendingIntent pendingIntent = PendingIntent.
                getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String chainId = "qbit_id" + id;
        String chainName = "qbit_name" + id;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assert notificationManager != null;
            notificationManager.createNotificationChannel(new NotificationChannel(chainId, chainName, NotificationManager.IMPORTANCE_LOW));
        }

        String message = bean.getMessage();
        if (bean.getMessageType() == 2) {
            message = "[图片]";
        }
        if (bean.getMessageType() == 3) {
            message = "[语音]";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setChannelId(chainId)
                .setPriority(NotificationCompat.DEFAULT_LIGHTS)
                .setContentText(message)
                .setContentTitle(messageName);

        if (notificationManager == null) return;
        notificationManager.notify((int) id, builder.build());
    }

}