package com.example.nouno.easydep.Services;

/**
 * Created by nouno on 27/03/2017.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.nouno.easydep.Activities.EstimateActivity;
import com.example.nouno.easydep.Activities.Login_Activity;
import com.example.nouno.easydep.Activities.RequestsListActivity;
import com.example.nouno.easydep.Data.AssistanceRequestListItem;
import com.example.nouno.easydep.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nouno on 27/03/2017.
 */

public class FireBaseMessangingService extends FirebaseMessagingService {

    public static int number = 0;
    public static final String TITLE_NEW_POSITION_IN_QUEUE = "position_in_queue_changed";
    public static final String TITLE_NEW_ESTIMATE = "new_estimate";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //showNotification(remoteMessage.getData().get("data"));
        String msg = remoteMessage.getData().get("data");
        showNotification(msg);
    }

    private void showNewEstimateNotification(String message) {
        Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent i = new Intent(this,EstimateActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        long estimate_id = getEstimateId(message);
        i.putExtra("estimateId",estimate_id);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
        String repairServiceName = getRepairServiceName(message);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("Nouveau devis")
                .setContentText(repairServiceName+" Vous a envoyé un devis")
                .setSound(defaultRingtoneUri)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentIntent(pendingIntent);


        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        number++;
        if (number>5)
            number=0;
        manager.notify(number,builder.build());

    }

    private void showQueueNotification (String message)
    {
        Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent i = new Intent(this,RequestsListActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
        int position = getPosition(message);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("EasyDep")
                .setContentText(AssistanceRequestListItem.getPositionString(position))
                .setSound(defaultRingtoneUri)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentIntent(pendingIntent);


        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        number++;
        manager.notify(number,builder.build());

    }
    private int getPosition (String json)
    {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);

        return jsonObject.getInt("new_position");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }


    private int getEstimateId (String json)
    {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getInt("estimate_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
    private String getRepairServiceName (String json)
    {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getString("repair_service_fullname");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    private void showNotification(String msg)
    {
        try {
            JSONObject jsonObject = new JSONObject(msg);
            String title = jsonObject.getString("title");
            if (title.equals(TITLE_NEW_ESTIMATE))
            {
                showNewEstimateNotification(msg);
            }
            else
            {
                showQueueNotification(msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}