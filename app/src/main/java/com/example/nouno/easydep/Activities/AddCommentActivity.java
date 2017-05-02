package com.example.nouno.easydep.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.nouno.easydep.DialogUtils;
import com.example.nouno.easydep.QueryUtils;
import com.example.nouno.easydep.R;
import com.example.nouno.easydep.Data.UserComment;
import com.example.nouno.easydep.SnackBarUtils;
import com.example.nouno.easydep.exceptions.ConnectionProblemException;
import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.Map;

public class AddCommentActivity extends AppCompatActivity {

    private UserComment userComment;
    private TextView nameText;
    private TextInputLayout commentWrapper;
    private View upImage;
    private RatingBar ratingBar;
    private ProgressBar progressBar;
    private String searchPositionJson;
    private AddCommentActivity addCommentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        addCommentActivity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        Gson gson = new Gson();
        userComment = gson.fromJson(extras.getString("userComment"),UserComment.class);
        if (extras.containsKey("searchPosition"))
        {
            searchPositionJson = extras.getString("searchPosition");
        }
        nameText = (TextView)findViewById(R.id.nameText);
        commentWrapper = (TextInputLayout)findViewById(R.id.comment_wrapper);
        ratingBar = (RatingBar)findViewById(R.id.ratingbar);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingBar.setRating((int)rating);
                userComment.setRating((int)ratingBar.getRating());
            }
        });

        ratingBar.setRating(userComment.getRating());
        nameText.setText(userComment.getCarOwner().getFullName());
        upImage = findViewById(R.id.go_button);
        upImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try  {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {}
                if (ratingBar.getRating()>0)
                {
                    LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();
                    map.put("carOwnerId",userComment.getCarOwner().getId()+"");
                    map.put("repairServiceId",userComment.getRepairService().getId()+"");
                    map.put("rating",userComment.getRating()+" ");
                    String commentText = commentWrapper.getEditText().getText().toString();
                    map.put("commentText",commentText);
                    map.put("deleteUserComment",0+"");
                    CommentTask commentTask = new CommentTask();
                    commentTask.execute(map);
                }
                else
                {
                    Snackbar snackbar = SnackBarUtils.buildInfoSnackbar("Erreur veuillez entrer une note valide\n",addCommentActivity,upImage);
                    snackbar.show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                restartInfoActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void restartInfoActivity()
    {
        Intent i = new Intent(getApplicationContext(),RepairServiceInfoActivity.class);
        Gson gson = new Gson();
        String json = gson.toJson(userComment.getRepairService());
        i.putExtra("repairService",json);
        i.putExtra("searchPosition",searchPositionJson);
        startActivity(i);
        finish();
    }

    private void restartInfoActivity(float rating)
    {
        userComment.getRepairService().setRating(rating);
        Intent i = new Intent(getApplicationContext(),RepairServiceInfoActivity.class);
        Gson gson = new Gson();
        String json = gson.toJson(userComment.getRepairService());
        i.putExtra("repairService",json);
        i.putExtra("commentAdded",true);
        i.putExtra("searchPosition",searchPositionJson);
        startActivity(i);
        finish();
    }

    private class CommentTask extends AsyncTask<Map<String,String>,Void,String>

    {
        @Override
        protected void onPreExecute() {
            upImage.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
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
            /*need error handling*/
            String[] strings = s.split("success");
            float rating = Float.parseFloat(strings[1]);
            if (searchPositionJson!=null)
                restartInfoActivity(rating);
            else
                startSearchActivity();

        }
    }

    private void startSearchActivity()
    {
        Intent i = new Intent(getApplicationContext(),SearchActivity.class);
        startActivity(i);
    }




}
