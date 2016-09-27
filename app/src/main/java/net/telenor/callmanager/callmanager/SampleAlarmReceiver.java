package net.telenor.callmanager.callmanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by apanic on 9/27/2016.
 */

public class SampleAlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PackageManager pm = context.getPackageManager();
        int hasPerm = pm.checkPermission(android.Manifest.permission.PROCESS_OUTGOING_CALLS,context.getPackageName());
        if (hasPerm != PackageManager.PERMISSION_GRANTED) {
            Intent myIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+38163670832"));;
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(myIntent);
        }
    }
}
