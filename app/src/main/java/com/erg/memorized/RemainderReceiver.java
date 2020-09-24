package com.erg.memorized;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.erg.memorized.helpers.RealmHelper;
import com.erg.memorized.model.ItemVerse;

import static com.erg.memorized.util.Constants.NOTIFY_CHANNEL_ID;
import static com.erg.memorized.util.Constants.SPACE;
import static com.erg.memorized.util.Constants.VERSE_COLUMN_ID;
import static com.erg.memorized.util.Constants.VIBRATE_TIME;

public class RemainderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();
        long verseId = extras.getLong(VERSE_COLUMN_ID);
        RealmHelper realmHelper = new RealmHelper(context);
        ItemVerse verse = realmHelper.findItemVerseById(verseId);

        long when = System.currentTimeMillis();

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String contentTitle = context.getString(R.string.app_name)
                + SPACE + context.getString(R.string.remainder_msg);

        String contentText = context.getString(R.string.ready_msg)
                + SPACE + verse.getTitle();

        NotificationCompat.Builder notifyBuilder =
                new NotificationCompat.Builder(context, NOTIFY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_learning_launcher)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setContentInfo(verse.getVerseText())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(sound)
                .setAutoCancel(true)
                .setWhen(when)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{VIBRATE_TIME, VIBRATE_TIME,
                        VIBRATE_TIME, VIBRATE_TIME, VIBRATE_TIME});

        NotificationManagerCompat nManagerCompat = NotificationManagerCompat.from(context);

        nManagerCompat.notify((int)verseId, notifyBuilder.build());
    }
}
