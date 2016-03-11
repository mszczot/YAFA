package com.example.szczocik.yafa_yetanotherfitnessapp.HelperClasses;

import android.app.Activity;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.szczocik.yafa_yetanotherfitnessapp.Classes.LocationHandler;
import com.example.szczocik.yafa_yetanotherfitnessapp.Classes.RunningSession;
import com.example.szczocik.yafa_yetanotherfitnessapp.R;

/**
 * Created by szczocik on 09/03/16.
 */
public class SessionsList extends ArrayAdapter<RunningSession> {

    private final Activity context;
    LocationHandler lh;


    public SessionsList(Activity context, int resource, LocationHandler lh) {
        super(context, resource, lh.getRsList());
        this.context = context;
        this.lh = lh;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.sessions_list, parent, false);

        TextView date = (TextView) rowView.findViewById(R.id.date);
        TextView distance = (TextView) rowView.findViewById(R.id.distance);
        TextView totalTime = (TextView) rowView.findViewById(R.id.totalTime);

        Spanned sp = Html.fromHtml(lh.getRSFromList(lh.getRSListSize() - 1 - position).getStartDate());
        date.setText(sp);

        distance.setText(String.valueOf(lh.getRSFromList(lh.getRSListSize() - 1 - position).getDistance()));
        totalTime.setText(lh.getRSFromList(lh.getRSListSize() - 1 - position).getTotalTime());

        return rowView;
    }
}
