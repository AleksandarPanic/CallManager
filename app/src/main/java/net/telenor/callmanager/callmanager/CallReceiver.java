package net.telenor.callmanager.callmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Date;

/**
 * Created by apanic on 9/27/2016.
 */

public class CallReceiver extends MyBroadcastReceiver {

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {

    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
    }

}
