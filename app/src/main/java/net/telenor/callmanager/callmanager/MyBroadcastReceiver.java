package net.telenor.callmanager.callmanager;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.util.Date;


/**
 * Created by apanic on 9/22/2016.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;  //because the passed incoming is only valid in ringing

    @Override
    public void onReceive(Context context, Intent intent) {

        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
        }
        else{
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                state = TelephonyManager.CALL_STATE_IDLE;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                state = TelephonyManager.CALL_STATE_RINGING;
            }
            onCallStateChanged(context, state, number);
        }
    }
    //Derived classes should override these to respond to specific events of interest
    protected void onIncomingCallStarted(Context ctx, String number, Date start){}
    protected void onOutgoingCallStarted(Context ctx, String number, Date start){}
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end){}
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end){}
    protected void onMissedCall(Context ctx, String number, Date start){}

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    public void onCallStateChanged(Context context, int state, String number) {

        if(lastState == state){
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;
                //if(number.equals("+38163230664")) {
                    if (Build.VERSION.SDK_INT >= 21 ) {
                        Intent answerCalintent = new Intent(context, AcceptCallActivity.class);
                        answerCalintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        answerCalintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(answerCalintent);
                    } else {
                        Intent intent = new Intent(context, AcceptCallActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                //}
                //Toast.makeText(context, "Veza uspostavljena" + number , Toast.LENGTH_SHORT).show();
                onIncomingCallStarted(context, number, callStartTime);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //iz idle->offhook, veza u uspostavi, komentar
                if(lastState != TelephonyManager.CALL_STATE_RINGING){
                    isIncoming = false;
                    callStartTime = new Date();
                    onOutgoingCallStarted(context, savedNumber, callStartTime);
                    Toast.makeText(context, "Veza se uspostavlja", Toast.LENGTH_SHORT).show();
                }
                //ringing->offhook, pozvani korisnik se javio
                else if (lastState == TelephonyManager.CALL_STATE_RINGING){

                    int hasContactPermission = ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS);
                    Toast.makeText(context, "Poziv odgovoren, veza uspostavljena", Toast.LENGTH_SHORT).show();
                    if(hasContactPermission == PackageManager.PERMISSION_GRANTED ) {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(savedNumber, null, "Telefonski poziv uspesan", null, null);
                        Toast.makeText(context, "Message Sent", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if(lastState == TelephonyManager.CALL_STATE_RINGING){
                    //Ring but no pickup-  a miss
                    onMissedCall(context, savedNumber, callStartTime);
                }
                else if(isIncoming){
                    onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                }
                else{
                    onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
                    Toast.makeText(context, "Odbijen ili zavrsen poziv", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        lastState = state;
    }
}

