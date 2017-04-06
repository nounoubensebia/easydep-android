package com.example.nouno.easydep.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.nouno.easydep.Data.AssistanceRequest;
import com.example.nouno.easydep.Data.CarOwner;
import com.example.nouno.easydep.Data.Position;
import com.example.nouno.easydep.DialogUtils;
import com.example.nouno.easydep.OnButtonClickListener;
import com.example.nouno.easydep.QueryUtils;
import com.example.nouno.easydep.R;
import com.example.nouno.easydep.Data.RepairService;
import com.example.nouno.easydep.Data.UserComment;
import com.example.nouno.easydep.ListAdapters.UserCommentAdapter;
import com.example.nouno.easydep.exceptions.ConnectionProblemException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class RepairServiceInfoActivity extends AppCompatActivity implements OnMapReadyCallback {
    private RepairService repairService;
    private TextView toolBarDurationText;
    private GoogleMap map;
    private ArrayList<UserComment> userComments;
    private TextView noRating;
    private CarOwner carOwner;
    private ProgressDialog progressDialog;
    private String searchPositionJson;
    RepairServiceInfoActivity repairServiceInfoActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        repairServiceInfoActivity = this;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String defaultJson = gson.toJson(new CarOwner(1,"Noureddine","Bensebia",null));

        String Json = sharedPref.getString("carOwner",defaultJson);
        carOwner = gson.fromJson(Json,CarOwner.class);


        Bundle bundle = getIntent().getExtras();
        repairService = gson.fromJson(getIntent().getExtras().getString("repairService"),RepairService.class);
        searchPositionJson = bundle.getString("searchPosition");
        if (bundle.containsKey("commentAdded"))
        {
            DialogUtils.buildInfoDialog("Commentaire ajouté","Votre commentaire a été ajouté merci de votre coopération",this).show();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_service_info);
        noRating = (TextView)findViewById(R.id.no_ratings);
        noRating.setVisibility(View.GONE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        displayRepairServiceData();
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        hideTitleText();
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
        {
            Intent i = new Intent(this,SearchActivity.class);
            i.putExtra("position",searchPositionJson);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Dialog buildDeleteDialog(final UserComment userComment)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Voulez vous vraiment supprimer ce commentaire ?").setTitle("Confirmation");
        builder.setPositiveButton("confirmer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
              LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();
                map.put("deleteCommentId",userComment.getId()+"");
                DeleteCommentTask deleteCommentTask = new DeleteCommentTask();
                deleteCommentTask.execute(map);
                //ProgressDialog progressDialog = buildProgressDialog();
                //progressDialog.show();
                //progressDialog.dismiss();
            }
        });
        builder.setNegativeButton("annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        return builder.create();

    }

    private Dialog buildReportDialog (final UserComment userComment)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Voulez vous vraiment signaler ce commentaire ?").setTitle("Confirmation");
        builder.setPositiveButton("confirmer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();
                map.put("reportCommentId",userComment.getId()+"");
                map.put("carOwnerId",carOwner.getId()+"");
                ReportCommentTask reportCommentTask = new ReportCommentTask();
                reportCommentTask.execute(map);

            }
        });
        builder.setNegativeButton("annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        return builder.create();
    }



    @Override
    protected void onResume() {
        super.onResume();
        RatingBar ratingBar = (RatingBar)findViewById(R.id.user_rating_bar);
        ratingBar.setRating(0);

    }

    public void hideTitleText()
    {
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        collapsingToolbarLayout.setTitle(" ");
        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(repairService.getFullName());
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    @SuppressLint("NewApi")
    private void displayRepairServiceData ()
    {
        TextView requestQuotationText;
        TextView toolBarDistanceText;
        TextView toolBarNameText;
        TextView toolbarTimeText;
        final RatingBar ratingBar;
        TextView locationText;
        TextView availableText;
        TextView priceText;
        final RatingBar userRatingBar;
        toolBarDistanceText = (TextView)findViewById(R.id.toolbar_distanceText);
        toolBarNameText = (TextView)findViewById(R.id.nameText);
        toolbarTimeText = (TextView)findViewById(R.id.toolbar_duration_text);
        userRatingBar = (RatingBar)findViewById(R.id.user_rating_bar);
        locationText = (TextView)findViewById(R.id.locationText);
        ratingBar = (RatingBar)findViewById(R.id.toolbar_ratingbar);
        availableText = (TextView)findViewById(R.id.availableText);
        priceText = (TextView)findViewById(R.id.price_text);
        toolBarDistanceText.setText(repairService.getDistanceString());
        toolBarNameText.setText(repairService.getFullName());
        ratingBar.setRating(repairService.getRating());
        toolbarTimeText.setText(repairService.getDurationString());
        locationText.setText(repairService.getLocation());
        //phoneText.setText(repairService.getPhoneNumber());
        //timeText.setText(repairService.getDurationString());
        priceText.setText(repairService.getPriceString());

        if (repairService.getStatus()==RepairService.AVAILABLE)
        {
            availableText.setText(repairService.getAvailableString());
        }
        else
        {
            availableText.setText(repairService.getAvailableString());
            Drawable img;
            if (repairService.getStatus()==RepairService.NOT_AVAILABLE)
            {
            availableText.setTextColor(getColor(android.R.color.holo_orange_dark));
            img = repairServiceInfoActivity.getDrawable(R.drawable.ic_do_not_disturb2_40_8p);}
            else
            {
                availableText.setTextColor(Color.parseColor("#ff9800"));
                img = repairServiceInfoActivity.getDrawable(R.drawable.ic_history_black_24dp);
            }
            availableText.setCompoundDrawables(null,img,null,null);
            availableText.setCompoundDrawablesWithIntrinsicBounds(null,img,null,null);

        }
        userRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser)
                {
                userRatingBar.setRating((int)rating);
                Intent i = new Intent(getApplicationContext(),AddCommentActivity.class);
                Gson gson = new Gson();
                UserComment userComment = new UserComment(carOwner,repairService,(int)userRatingBar.getRating());
                String json = gson.toJson(userComment);
                i.putExtra("searchPosition",searchPositionJson);
                i.putExtra("userComment",json);
                startActivity(i);
                }
            }
        });
        requestQuotationText = (TextView) findViewById(R.id.request_quotation);
        if (repairService.getDuration()!=RepairService.NO_DURATION) {

            requestQuotationText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                    map.put("carOwnerId", carOwner.getId() + "");
                    map.put("repairServiceId", repairService.getId() + "");
                    CheckRequestExists checkRequestExists = new CheckRequestExists();
                    checkRequestExists.execute(map);
                }
            });


        }
        else
        {
            View addCommentLayout = findViewById(R.id.add_comment_layout);
            View distanceCircle = findViewById(R.id.distance_circle);
            distanceCircle.setVisibility(View.GONE);
            addCommentLayout.setVisibility(View.GONE);
            requestQuotationText.setVisibility(View.GONE);
            //toolbarTimeText.setVisibility(View.GONE);
            toolbarTimeText.setText(repairService.getPhoneNumber());
        }


        getComments();
    }

    private void startAssistanceRequestActivity ()
    {
        Gson gson = new Gson();
        Position searchPosition = gson.fromJson(searchPositionJson,Position.class);
        AssistanceRequest assistanceRequest = new AssistanceRequest(0,false,searchPosition,null,carOwner,repairService);
        String json = gson.toJson(assistanceRequest);
        Intent i = new Intent(getApplicationContext(),AssistanceRequestActivity.class);
        i.putExtra("assistanceRequest",json);
        i.putExtra("requestSent",false);
        startActivity(i);
    }

    public void getComments()
    {
        LinkedHashMap<String,String> map = new LinkedHashMap();
        map.put("repairServiceId",repairService.getId()+"");
        map.put("carOwnerId",carOwner.getId()+"");
        GetCommentsTask getCommentsTask = new GetCommentsTask();
        getCommentsTask.execute(map);
    }

    public void populateCommentList (ArrayList<UserComment> comments)
    {
        ListView listView = (ListView)findViewById(R.id.comments_list);
        UserCommentAdapter userCommentAdapter = new UserCommentAdapter(this,comments);
        userCommentAdapter.setOnButtonClickListener(new OnButtonClickListener<UserComment>() {
            @Override
            public void onButtonClick(UserComment userComment) {
                if (userComment.getCarOwner().getId()==carOwner.getId())
                {
                    Dialog dialog = buildDeleteDialog(userComment);
                    dialog.show();
                }
                else
                {
                    Dialog dialog = buildReportDialog(userComment);
                    dialog.show();
                }
            }
        });
        listView.setAdapter(userCommentAdapter);
        justifyListViewHeightBasedOnChildren(listView);
        TextView ratingText = (TextView)findViewById(R.id.rating_text);
        TextView ratingNumber = (TextView)findViewById(R.id.rating_number);
        if (comments.size()>0)
        {
            NumberFormat nf;
            String s;
            nf = new DecimalFormat("0.#");
            s = nf.format(repairService.getRating());
        ratingText.setText(s);
        ratingNumber.setText(comments.size()+"");
        }
        else
        {
            noRating = (TextView)findViewById(R.id.no_ratings);
            noRating.setVisibility(View.VISIBLE);
            TextView resumeAvis = (TextView)findViewById(R.id.resume_avis);
            TextView resumeUtilisateur = (TextView)findViewById(R.id.resume_avis_avis_utilisateur);
            View layout = findViewById(R.id.resume_avis_layout);
            resumeAvis.setVisibility(View.GONE);
            resumeUtilisateur.setVisibility(View.GONE);
            layout.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setAllGesturesEnabled(false);
        double latitudecenter = repairService.getLatitude();
        double longitudecenter = repairService.getLongitude();
        LatLng centerauto=new LatLng(latitudecenter,longitudecenter);
        Marker marker1 = map.addMarker(new MarkerOptions().position(centerauto).title("Votre position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        map.moveCamera((CameraUpdateFactory.newLatLngZoom(centerauto,12)));

    }
    public void justifyListViewHeightBasedOnChildren (ListView listView) {

        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }
        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }

    private class GetCommentsTask extends AsyncTask<Map<String,String>,Void,String>
    {
        @Override
        protected void onPreExecute() {
            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
            ListView listView = (ListView)findViewById(R.id.comments_list);
            listView.setVisibility(View.GONE);
            View commentsLayout = findViewById(R.id.comments_layout);
            commentsLayout.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String response = null;
            try {
                response = QueryUtils.makeHttpPostRequest(QueryUtils.GET_USER_COMMENTS_LOCAL_URL,params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            userComments = UserComment.parseJson(s);
            populateCommentList(userComments);

            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
            ListView listView = (ListView)findViewById(R.id.comments_list);
            listView.setVisibility(View.VISIBLE);
            View commentsLayout = findViewById(R.id.comments_layout);
            commentsLayout.setVisibility(View.VISIBLE);
        }
    }

    private class DeleteCommentTask extends AsyncTask<Map<String,String>,Void,String>
    {

        @Override
        protected void onPreExecute() {
            progressDialog = (ProgressDialog)DialogUtils.buildProgressDialog("Suppression du commentaire en cours ...",repairServiceInfoActivity);
            progressDialog.show();
        }


        @Override
        protected String doInBackground(Map<String, String>... params) {
            String response = null;
            try {
                response = QueryUtils.makeHttpPostRequest(QueryUtils.GET_USER_COMMENTS_LOCAL_URL,params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            Dialog dialog = DialogUtils.buildInfoDialog("Commentaire supprimé","Votre commentaire a été supprimé",repairServiceInfoActivity);
            dialog.show();
            getComments();
            displayRepairServiceData();

        }
    }

    private class ReportCommentTask extends AsyncTask<Map<String,String>,Void,String>
    {
        @Override
        protected void onPreExecute() {
            progressDialog = (ProgressDialog)DialogUtils.buildProgressDialog("Singalisation du commentaire en cours ...",repairServiceInfoActivity);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String resonse = null;
            try {
                resonse = QueryUtils.makeHttpPostRequest(QueryUtils.GET_USER_COMMENTS_LOCAL_URL,params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
            }
            return resonse;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            Dialog infoDialog;
            if (s.equals("success"))
            {
                infoDialog = DialogUtils.buildInfoDialog("Commentaire signalé","Merci de votre coopération",repairServiceInfoActivity);
            }
            else
            {
                infoDialog = DialogUtils.buildInfoDialog("Erreur","Vous avez deja signaler ce commentaire",repairServiceInfoActivity);
            }
             infoDialog.show();
        }
    }

    private class CheckRequestExists extends AsyncTask<Map<String,String>,Void,String>
    {
        @Override
        protected void onPreExecute() {
            progressDialog = (ProgressDialog)DialogUtils.buildProgressDialog("Veuillez Patientez",repairServiceInfoActivity);
            progressDialog.show();

        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String response = null;
            try {
                response = QueryUtils.makeHttpPostRequest(QueryUtils.SEND_REQUEST_URL,params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if (s.equals("true"))
            {
                Dialog dialog = DialogUtils.buildInfoDialog("Erreur","Vous avez deja envoyé une demande a ce dépanneur",repairServiceInfoActivity);
                dialog.show();
            }
            else
            {
                if (s.equals("false"))
                {
                    startAssistanceRequestActivity();
                }
            }
        }
    }




}
