package com.example.nouno.easydep;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by nouno on 23/03/2017.
 */

public class UserCommentAdapter extends ArrayAdapter<UserComment>  {


    public UserCommentAdapter(Context context,ArrayList<UserComment> list) {
        super(context,0,list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View item = convertView;
        if (item ==null)
        {
            item = LayoutInflater.from(getContext()).inflate(R.layout.comment_list_item ,parent, false);
        }
        TextView userNameText = (TextView)item.findViewById(R.id.nameText);
        TextView commentText = (TextView)item.findViewById(R.id.comment_text);
        RatingBar ratingBar = (RatingBar)item.findViewById(R.id.ratingbar);
        TextView dateText = (TextView)item.findViewById(R.id.date_text);
        TextView signalDeleteText = (TextView)item.findViewById(R.id.signal_delete);
        UserComment userComment = getItem(position);
        userNameText.setText(userComment.getCarOwner().getFirstName()+" "+userComment.getCarOwner().getLastName());
        commentText.setText(userComment.getComment());
        ratingBar.setRating(userComment.getRating());
        DateFormat dateFormat = new SimpleDateFormat();
        String date = dateFormat.format(userComment.getDate());
        dateText.setText(date);
        if (getItem(position).isFromConnectedUser())
        {
            signalDeleteText.setText("Supprimer");
            signalDeleteText.setTextColor(Color.parseColor("#F44336"));

        }
        signalDeleteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return item;
    }
}
