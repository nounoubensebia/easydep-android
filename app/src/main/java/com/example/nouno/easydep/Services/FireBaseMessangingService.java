package com.example.nouno.easydep.Services;

/**
 * Created by nouno on 27/03/2017.
 */

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.example.nouno.easydep.Activities.EstimateActivity;
import com.example.nouno.easydep.Activities.QueueActivity;
import com.example.nouno.easydep.Activities.RequestsListActivity;
import com.example.nouno.easydep.Data.AssistanceRequestListItem;
import com.example.nouno.easydep.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.yayandroid.theactivitymanager.TheActivityManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nouno on 27/03/2017.
 */

public class FireBaseMessangingService extends FirebaseMessagingService {
    private LocalBroadcastManager broadcaster;
    public static int number = 0;
    public static int queueId = 100;
    public static final String TITLE_NEW_POSITION_IN_QUEUE = "position_in_queue_changed";
    public static final String TITLE_NEW_ESTIMATE = "new_estimate";
    public static final String TITLE_INTERVENTION_CANCELED = "intervention_canceled";
    public static final String TITLE_ESTIMATE_REFUSED = "estimate_refused";

    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //handleMessage(remoteMessage.getData().get("data"));
        String msg = remoteMessage.getData().get("data");
        handleMessage(msg);
    }


    private void showNotification(String title, String message, int id, Intent intent) {
        Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(message)
                .setSound(defaultRingtoneUri)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentIntent(pendingIntent);


        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(id, builder.build());

    }

    private void showNewEstimateNotification(String message) {
        Intent i = new Intent(this, EstimateActivity.class);
        long estimate_id = getEstimateId(message);
        i.putExtra("estimateId", estimate_id);
        Intent intent = new Intent("new_estimate");
        broadcaster.sendBroadcast(intent);
        String repairServiceName = getRepairServiceName(message);
        showNotification("Nouveau devis", repairServiceName + " Vous a envoyé un devis", (int) estimate_id, i);
    }

    private void showQueueNotification(String message) {
        Activity activity = TheActivityManager.getInstance().getCurrentActivity();
        if (activity != null && activity instanceof QueueActivity) {
            Intent intent = new Intent("new_position");
            broadcaster.sendBroadcast(intent);
        } else {
            Intent i = new Intent(this, RequestsListActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            int position = getPosition(message);
            showNotification("EasyDep", AssistanceRequestListItem.getPositionString(position), queueId, i);
        }

    }

    private void showEstimateRefusedNotification(String message) {

        Intent i = new Intent(this, RequestsListActivity.class);
        long estimate_id = getEstimateId(message);
        i.putExtra("estimateId",estimate_id);
        String repairServiceName = getRepairServiceName(message);
        showNotification("Demande refusée",repairServiceName+ "a refusé votre demande",(int)estimate_id,i);
        Intent intent = new Intent("new_estimate");
        broadcaster.sendBroadcast(intent);
    }

    private void showCanceledNotification() {
        Activity activity = TheActivityManager.getInstance().getCurrentActivity();
        if (activity != null && activity instanceof QueueActivity) {
            Intent intent = new Intent("intervention_canceled");
            broadcaster.sendBroadcast(intent);
        } else {
            Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Intent i = new Intent(this, RequestsListActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setAutoCancel(true)
                    .setContentTitle("EasyDep")
                    .setContentText("Intervention annulée par le dépanneur")
                    .setSound(defaultRingtoneUri)
                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                    .setContentIntent(pendingIntent);


            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            manager.notify(queueId, builder.build());
        }

    }

    private int getPosition(String json) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);

            return jsonObject.getInt("new_position");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }


    private int getEstimateId(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getInt("estimate_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private String getRepairServiceName(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getString("repair_service_fullname");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void handleMessage(String msg) {
        try {
            JSONObject jsonObject = new JSONObject(msg);
            String title = jsonObject.getString("title");
            if (title.equals(TITLE_NEW_ESTIMATE)) {
                showNewEstimateNotification(msg);
            } else {
                if (title.equals(TITLE_NEW_POSITION_IN_QUEUE))
                    showQueueNotification(msg);
            }
            if (title.equals(TITLE_INTERVENTION_CANCELED)) {
                showCanceledNotification();
            }
            if (title.equals(TITLE_ESTIMATE_REFUSED)) {
                showEstimateRefusedNotification(msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}