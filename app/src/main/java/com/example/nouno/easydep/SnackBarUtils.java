package com.example.nouno.easydep;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

/**
 * Created by nouno on 02/05/2017.
 */

public class SnackBarUtils {
    public static Snackbar buildInfoSnackbar(String text, Context context,View view)
    {
        Snackbar snackbar = Snackbar.make(view,text,Snackbar.LENGTH_LONG);
        View view2 = snackbar.getView();

        view2.setBackgroundColor(context.getResources().getColor(R.color.white));
        TextView tv = (TextView) view2.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(context.getResources().getColor(R.color.backgroundColor));
        tv.setMaxLines(10);
        return snackbar;
    }
}
