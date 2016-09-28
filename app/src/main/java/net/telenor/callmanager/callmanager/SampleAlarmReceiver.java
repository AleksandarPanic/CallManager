package net.telenor.callmanager.callmanager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.WakefulBroadcastReceiver;




/**
 * Created by apanic on 9/27/2016.
 */

public class SampleAlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        PackageManager pm = context.getPackageManager();
        int hasPerm = pm.checkPermission(Manifest.permission.CALL_PHONE,context.getPackageName());
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED){
            Intent myIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+38163670695"));;
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(myIntent);
        }
    }
}
