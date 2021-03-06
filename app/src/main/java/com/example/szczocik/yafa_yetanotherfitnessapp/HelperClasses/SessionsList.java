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

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by szczocik on 09/03/16.
 */
public class SessionsList extends ArrayAdapter<RunningSession>
    implements Serializable {

    private final Activity context;
    ArrayList<RunningSession> rsList;


    public SessionsList(Activity context, int resource, ArrayList<RunningSession> rsList) {
        super(context, resource, rsList);
        this.context = context;
        this.rsList = rsList;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.sessions_list, parent, false);

        TextView date = (TextView) rowView.findViewById(R.id.date);
        TextView distance = (TextView) rowView.findViewById(R.id.distance);
        TextView totalTime = (TextView) rowView.findViewById(R.id.totalTime);

        Spanned sp = Html.fromHtml(rsList.get(position).getStartDate());
        date.setText(sp);

        distance.setText(String.format("%.3f", rsList.get(position).getDistanceInMiles())
                            + " miles");

        totalTime.setText(rsList.get(position).getTotalTime());

        return rowView;
    }
}
